package com.proximipro.engage.android.core

import android.os.Handler
import com.google.gson.Gson
import com.proximipro.engage.android.analytics.Analytics
import com.proximipro.engage.android.model.ProBeacon
import org.altbeacon.beacon.Beacon
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 13 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Responsible for managing and processing beacon scan results and determining events.
 */
class BeaconScanController : KoinComponent {
    private var currentCampedOnBeacon: ProBeacon? = null
    private var lastRangedTime: Long = -1
    private var switchModeTo: (ScanMode) -> Unit = {}
    private var currentScanMode: ScanMode = ScanMode.Passive
    private val rangedBeacons = arrayListOf<ProBeacon>()
    private val eventHandler = BeaconEventHandler
    private var lastBeaconCampedOnTime = -1L
    private var lastRegionEnterTime = -1L
    private var lastExitedBeacon: ProBeacon? = null
    private val pref: EngagePref by inject()
    private val gson = Gson()

    companion object {
        private const val PASSIVE_MODE_THRESHOLD = 300000L // 5 min
        private const val EXIT_THRESHOLD = 3
        private const val EVENT_THRESHOLD = 2000L
    }

    init {
        eventHandler.regionId = pref().regionId
    }

    /*
    * This counter helps to manage the issue where the camped beacon is not found on subsequent scan results
    * and it causes the it to call exit event on it
    * so this count will be used to create some kind of threshold[3], if the threshold[3] is reached,
    * we will call exit beacon event and if the beacon is found on subsequent scan then we will reset it to 0
    * */
    private var exitCounter = 0

    /**
     * Processes scanned beacons and determines enter, exit and dwell events.
     * Also manages current associated beacon.
     * Whenever a beacon event is triggered, a transition is considered to be ongoing until
     * the triggered event's task is not completed. In this case, it will not process anything if
     * one transition is ongoing.
     */
    private fun determineEvents(list: List<ProBeacon>) {
        Timber.e("Determining event")
        rangedBeacons.clear()
        rangedBeacons.addAll(list)
        eventHandler.onRangedBeacons(rangedBeacons.toList())
        val campedOnBeacon = currentCampedOnBeacon
        if (campedOnBeacon == null) {
            Timber.e("No previous beacon found")
            // no previously detected beacon, find closest one and start camping
            /*
            * Condition-3 ->
            * If our campedOnBeacon is null and our RSSI > -75 then we take the closest beacon
            * and make that our campedOnBeacon. We log the CAMPED_BEACON event
            * (i.e. save analytics in local DB), execute any rules attached to
            * this beacon and trigger the onBeaconCamped callback.
            * */
            val closestBeacon = list.min() ?: return
            if (!closestBeacon.isInRange()) return
            currentCampedOnBeacon = closestBeacon
            setEnterRegion(closestBeacon)
            campedOnBeaconEvent(closestBeacon)
            // resetting the exit event counter
            exitCounter = 0
        } else {
            // we have camped beacon already
            if (campedOnBeacon !in list) {
                /* Condition-1 ->
                * If our campedOnBeacon is not null (i.e. we set it in a previous loop) and
                * we can’t find the campedOnBeacon in the latest rangedBeacons
                * (i.e. we did not find that beacon in out last scan) then
                * we perform an exitBeacon on our campedOnBeacon.
                * During exitBeacon, currentCampedOnBeacon is reset to null.
                * */
                Timber.e("Camped beacon is not found in the scan results")
                exitCounter += 1
                if (exitCounter >= EXIT_THRESHOLD) {
                    exitCounter = 0
                    exitBeaconEvent(campedOnBeacon)
                    currentCampedOnBeacon = null
                }
                return
            }
            Timber.e("Camped beacon is in the list: $campedOnBeacon")
            if (campedOnBeacon.isNotReachable()) {
                /* Condition-4 ->
                * If our campedOnBeacon is not null and its RSSI is < -85 [Not Reachable]
                * then we exit the campedOnBeacon.
                * */
                Timber.e("Camped beacon is not reachable [Not in range]")
                exitBeaconEvent(campedOnBeacon)
                currentCampedOnBeacon = null
                return
            }
            exitCounter = 0
            val closestBeacon = list.min()
                ?: Timber.wtf("WTF: Closest Beacon can't be null, $campedOnBeacon is in the list").let { return }
            if (campedOnBeacon != closestBeacon) {
                /* Condition-2 ->
                * If our campedOnBeacon is not null and this beacon is NOT the nearest beacon
                * (i.e. the keys of the camped beacon and closest beacon do not match) and the RSSI of
                * this beacon is > -75 then we perform exitBeacon with this beacon.
                * So if there is a beacon closer than our campedOnBeacon is a bit too far away,
                * we exit the campedOnBeacon.
                * */
                Timber.e("Camped beacon is not the nearest beacon")
                exitBeaconEvent(campedOnBeacon)
                currentCampedOnBeacon = null
                // camp on new closest beacon
                currentCampedOnBeacon = closestBeacon
                Handler().postDelayed({
                    campedOnBeaconEvent(closestBeacon)
                }, EVENT_THRESHOLD)
                return
            } else {
                // current camped beacon is still in the range so update
                // the object to update new distance and rssi values
                currentCampedOnBeacon = closestBeacon
                Timber.e("Current camped beacon: $currentCampedOnBeacon")
            }
        }
    }

    // ============================ Callback Events ============================ //

    /**
     * calls beacon exit event on [eventHandler]
     * @param beacon ProBeacon is the beacon on which the event is triggered
     */
    private fun exitBeaconEvent(beacon: ProBeacon) {
        val eventInfo: HashMap<String, String> = hashMapOf(
            Analytics.MAP_KEY_REGION_ID to pref().regionId,
            Analytics.MAP_KEY_RSSI to beacon.rssi.toString(),
            Analytics.MAP_KEY_BEACON_KEY to beacon.getBeaconKey(),
            Analytics.MAP_KEY_DURATION to (System.currentTimeMillis() - lastBeaconCampedOnTime).toString()
        )
        lastExitedBeacon = beacon
        eventHandler.onBeaconExit(beacon, gson.toJson(eventInfo))
    }

    /**
     * calls camping event on [eventHandler]
     * @param beacon ProBeacon is the beacon on which the event is triggered
     */
    private fun campedOnBeaconEvent(beacon: ProBeacon) {
        val eventInfo: HashMap<String, String> = hashMapOf(
            Analytics.MAP_KEY_REGION_ID to pref().regionId,
            Analytics.MAP_KEY_RSSI to beacon.rssi.toString(),
            Analytics.MAP_KEY_BEACON_KEY to beacon.getBeaconKey()
        )
        eventHandler.onBeaconCamped(beacon, gson.toJson(eventInfo))
        lastBeaconCampedOnTime = System.currentTimeMillis()
    }

    // ============================ Callback Events End ============================ //

    /**
     * Sets Enter region event on given beacon
     * @param beacon ProBeacon
     */
    private fun setEnterRegion(beacon: ProBeacon) {
        Timber.e("Entered region: $beacon")
        lastRangedTime = System.currentTimeMillis()

        if (currentScanMode !is ScanMode.Active) {
            switchModeTo(ScanMode.Active)
            val eventInfo: HashMap<String, String> = hashMapOf(
                Analytics.MAP_KEY_REGION_ID to pref().regionId
            )
            lastRegionEnterTime = System.currentTimeMillis()
            eventHandler.onEnterRegion(gson.toJson(eventInfo))
        }
    }

    /**
     * Processes the newly found beacon list.
     *
     * Responsible for removing null objects, converting [Beacon] to [ProBeacon]
     * and add them to the global map that contains all the scanned devices so far.
     * @param list Collection<Beacon?> is the newly scan result which contains beacons detected at the given time.
     */
    fun processList(list: Collection<Beacon?>) {
        val newList = list.filterNotNull().map(::ProBeacon)
        Timber.e("Scan Result[count: ${newList.size}]: $newList")
        if (currentScanMode is ScanMode.Active && needToSwitchToPassiveMode(newList)) {
            currentScanMode = ScanMode.Passive
            switchModeTo(ScanMode.Passive)
            val eventInfo: HashMap<String, String> = hashMapOf(
                Analytics.MAP_KEY_REGION_ID to pref().regionId,
                Analytics.MAP_KEY_DURATION to (System.currentTimeMillis() - lastRegionEnterTime).toString(),
                Analytics.MAP_KEY_REGION_ENTER_TIME to lastRegionEnterTime.toString(),
                Analytics.MAP_KEY_ON_BEACON to lastExitedBeacon?.getBeaconKey().toString()
            )
            eventHandler.onExitRegion(gson.toJson(eventInfo))
        } else {
            determineEvents(newList)
        }
    }

    /**
     * Determines whether there's a need to switch to passive mode or not
     * @param list List<ProBeacon>
     * @return Boolean
     */
    private fun needToSwitchToPassiveMode(list: List<ProBeacon>): Boolean {
        return if (list.isNotEmpty()) {
            false
        } else System.currentTimeMillis() - lastRangedTime >= PASSIVE_MODE_THRESHOLD
    }

    /**
     * resets all the settings and scan results for the previous scan.
     */
    fun reset() {
        currentCampedOnBeacon = null
        rangedBeacons.clear()
        exitCounter = 0
        currentScanMode = ScanMode.Active
        lastRangedTime = -1
        lastExitedBeacon = null
        lastRegionEnterTime = -1L
        lastBeaconCampedOnTime = -1L
        switchModeTo = {}
    }

    /**
     * Sets listener for changing scan modes (Active and Passive scan modes)
     * @param func Function1<ScanMode, Unit> is the lambda that will be invoked on scan mode changes
     */
    fun setScanModeListener(func: (ScanMode) -> Unit) {
        switchModeTo = func
    }

    /**
     * Lets observers know that the scan is stopped
     */
    fun stopScan() {
        eventHandler.onScanStopped()
    }
}