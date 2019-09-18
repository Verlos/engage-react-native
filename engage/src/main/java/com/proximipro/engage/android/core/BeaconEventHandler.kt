package com.proximipro.engage.android.core

import com.google.gson.Gson
import com.proximipro.engage.android.analytics.Analytics
import com.proximipro.engage.android.analytics.AnalyticsManager
import com.proximipro.engage.android.core.EventResult.BeaconCamped
import com.proximipro.engage.android.core.EventResult.BeaconExit
import com.proximipro.engage.android.core.EventResult.EnterRegion
import com.proximipro.engage.android.core.EventResult.ExitRegion
import com.proximipro.engage.android.core.EventResult.RangedBeacons
import com.proximipro.engage.android.core.EventResult.RuleTriggered
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 11 July 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Handles all the beacon events and posts the values on [eventResultLiveData]
 */
internal object BeaconEventHandler : KoinComponent {
    private const val EVENTS_DELAY = 1000L
    private const val RULE_THRESHOLD = 1000L
    private val repo = EngageRepository
    internal var regionId = ""
    private val analyticsManager: AnalyticsManager by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * called upon receiving scan results
     * @param list List<ProBeacon> is the ranged beacons for the scan
     */
    fun onRangedBeacons(list: List<ProBeacon>) {
        eventResultLiveData.postValue(RangedBeacons(list))
    }

    /**
     * Called upon entering a beacon region
     */
    fun onEnterRegion(eventInfo: String) {
        Timber.e("onEnterRegion")
        postValue(EnterRegion(regionId))
        analyticsManager.logRegionEnterEvent(eventInfo)
    }

    /**
     * called upon exiting a beacon region
     */
    fun onExitRegion(eventInfo: String) {
        Timber.e("onExitRegion")
        postValue(ExitRegion(regionId))
        analyticsManager.logRegionExitEvent(eventInfo)
    }

    /**
     * Called upon camping on a beacon
     * @param beacon ProBeacon is the beacon on which the event is triggered
     */
    fun onBeaconCamped(beacon: ProBeacon, eventInfo: String) {
        Timber.e("onBeaconCamped")
        postValue(BeaconCamped(beacon))
        processForRule(beacon, "enter", eventInfo)
    }

    /**
     * Called upon exiting a beacon
     * @param beacon ProBeacon is the beacon on which the event is triggered
     */
    fun onBeaconExit(beacon: ProBeacon, eventInfo: String) {
        Timber.e("onBeaconExit")
        postValue(BeaconExit(beacon))
        processForRule(beacon, "exit", eventInfo)
    }

    /**
     * Process the beacon event and determines rules if any related
     * @param beacon ProBeacon is the beacon on which the event is triggered
     * @param event String is the (camping/exit) event of the beacon
     */
    private fun processForRule(beacon: ProBeacon, event: String, eventInfo: String) {
        Timber.e("Processing rule $beacon")
        scope.launch {
            val zoneRule = repo.getZoneRule(
                major = beacon.major,
                minor = beacon.minor,
                event = event,
                uuid = beacon.uuid
            ) ?: let {
                Timber.e("No rule found, returning $beacon")
                return@launch
            }
//            delay(RULE_THRESHOLD)
            analyticsManager.logBeaconEvent(
                beacon = beacon,
                rule = zoneRule.rule,
                eventInfo = eventInfo
            )
            onRuleTriggered(beacon, zoneRule.rule)
        }
    }

    /**
     * Called when a rule is triggered for a beacon
     * @param rule Rule is the triggered rule
     */
    private fun onRuleTriggered(beacon: ProBeacon, rule: Rule) {
        Timber.e("onRuleTriggered")
        val eventInfo = Gson().toJson(
            hashMapOf(
                Analytics.MAP_KEY_RULE_ID to rule.id,
                Analytics.MAP_KEY_RULE_NAME to rule.name.toString(),
                Analytics.MAP_KEY_REGION_ID to regionId,
                Analytics.MAP_KEY_BEACON_KEY to beacon.getBeaconKey()
            )
        )
        postValue(RuleTriggered(beacon, rule))
        analyticsManager.logRuleTriggeredEvent(eventInfo)
    }

    /**
     * posts events to the [eventResultLiveData]
     * @param result EventResult is the result wrapper for the beacon events
     */
    private fun postValue(result: EventResult) {
        scope.launch {
            delay(EVENTS_DELAY)
            eventResultLiveData.postValue(result)
        }
    }

    /**
     * Sets scan stopped event on [eventResultLiveData]
     */
    fun onScanStopped() {
        postValue(EventResult.StopScan)
    }
}