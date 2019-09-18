package com.proximipro.engage.android.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule

/*
 * Created by Birju Vachhani on 03 July 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * LiveData that can be observed for getting scan results
 */
internal var eventResultLiveData = MutableLiveData<EventResult>()

/**
 * Scan results event holder sealed class. It defines what kind of
 * event is trigger upon receiving scan results
 */
internal sealed class EventResult {

    /**
     * Based on the event type, determines which method to call for the beacon event
     * @param listener BeaconScanResultListener is the listeners on which the methods will be invoked
     */
    internal fun resolveFor(listener: BeaconScanResultListener) {
        when (this) {
            is BeaconCamped -> listener.onBeaconCamped(beacon)
            is BeaconExit -> listener.onBeaconExit(beacon)
            is EnterRegion -> listener.onEnterRegion(name)
            is ExitRegion -> listener.onExitRegion(name)
            is RangedBeacons -> listener.onRangedBeacons(beacons)
            is RuleTriggered -> {
                listener.onRuleTriggered(rule)
                listener.onRuleTriggered(beacon, rule)
            }
            is StopScan -> listener.onScanStopped()
        }
    }

    /**
     * Represents Enter Region event. It literally means that we entered in a beacon region
     * @property name String is the name the sdk defined string value that will be helpful in some ways to
     * make use of this event
     */
    data class EnterRegion(val name: String) : EventResult()

    /**
     * Represents Exit Region event. It literally means that we exited from a beacon region
     * @property name String is the name the sdk defined string value that will be helpful in some ways to
     * make use of this event
     */
    data class ExitRegion(val name: String) : EventResult()

    /**
     * Represents beacon camping event, also known as beacon enter event.
     * @property beacon ProBeacon is the beacon on which the camping event is triggered
     */
    data class BeaconCamped(val beacon: ProBeacon) : EventResult()

    /**
     * Represents beacon exit event. This event is triggered when a beacon is not reachable or it is not the
     * closest beacon
     * @property beacon ProBeacon is the beacon on which the exit event is triggered
     */
    data class BeaconExit(val beacon: ProBeacon) : EventResult()

    /**
     * Represents ranging event for beacon scan. It means that we have some ranged beacons as a result of
     * the scanning process
     * @property beacons List<ProBeacon> is the list of ranged beacons for a particular beacon scan
     */
    data class RangedBeacons(val beacons: List<ProBeacon>) : EventResult()

    /**
     * Represents trigger event for rules. It is called whenever a rule is triggered on a beacon
     * @property rule Rule is the rule that is triggered
     */
    data class RuleTriggered(val beacon: ProBeacon, val rule: Rule) : EventResult()

    /**
     * Represents that the scan is stopped. Used to inform observers that the scan is stopped
     */
    object StopScan : EventResult()
}

/**
 * Helper function to set observers on [eventResultLiveData] with lifecycle awareness
 * @param owner LifecycleOwner is the owner of the lifecycle for the [listener]
 * @param listener BeaconScanResultListener is the [BeaconScanResultListener] instance on which the events
 * will be triggered on receiving results on [eventResultLiveData] observations
 */
internal fun observeScanResult(owner: LifecycleOwner, listener: BeaconScanResultListener) {
    eventResultLiveData.observe(owner, Observer { result -> result.resolveFor(listener) })
}

/**
 * Helper function to set observers on [eventResultLiveData]
 * @param listener BeaconScanResultListener is the [BeaconScanResultListener] instance on which the events
 * will be triggered on receiving results on [eventResultLiveData] observations
 */
internal fun observeScanResult(listener: BeaconScanResultListener) {
    eventResultLiveData.observeForever { result -> result.resolveFor(listener) }
}