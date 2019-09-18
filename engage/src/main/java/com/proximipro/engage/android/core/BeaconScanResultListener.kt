package com.proximipro.engage.android.core

import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule

/*
 * Created by Birju Vachhani on 03 July 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Interface that provides beacon scan events based on processed results
 */
abstract class BeaconScanResultListener {

    /**
     * Should be called when a beacon leaves the parameter when specified exit conditions met.
     * @param beacon ProBeacon is the beacon on which this event is triggered
     */
    abstract fun onBeaconExit(beacon: ProBeacon)

    /**
     * Should be called when a beacon stay in range for specified interval of time.
     * @param beacon ProBeacon is the beacon on which this event is triggered
     */
    abstract fun onBeaconCamped(beacon: ProBeacon)

    /**
     * Should be called when a rule is triggered on beacon scan result
     * @param rule Rule is the rule that is triggered
     */
    abstract fun onRuleTriggered(rule: Rule)

    /**
     * Should be called when a rule is triggered on beacon scan result
     * @param rule Rule is the rule that is triggered
     */
    open fun onRuleTriggered(beacon: ProBeacon, rule: Rule) {

    }

    /**
     * Should be called when we exit the region which happens when we switch to passive mode
     */
    internal open fun onExitRegion(params: String) {}

    /**
     * Should be called when we enter the region which happens when we switch to active mode
     */
    internal open fun onEnterRegion(params: String) {}

    /**
     * Should be called when we receive beacon scan results
     * @param beacons List<ProBeacon>
     */
    internal open fun onRangedBeacons(beacons: List<ProBeacon>) {}

    /**
     * Called when beacon is stopped
     */
    open fun onScanStopped() {

    }
}