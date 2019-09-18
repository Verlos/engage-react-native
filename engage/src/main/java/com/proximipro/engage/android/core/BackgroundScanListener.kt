package com.proximipro.engage.android.core

import android.content.Context
import android.content.Intent
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.receiver.BackgroundScanResultReceiver
import com.proximipro.engage.android.util.Constants
import timber.log.Timber

/*
 * Created by Birju Vachhani on 05 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * An implementation of [BeaconScanResultListener] that handles events emitted by background scan.
 */
class BackgroundScanListener(val context: Context) : BeaconScanResultListener() {

    override fun onExitRegion(params: String) {}

    override fun onEnterRegion(params: String) {}

    override fun onBeaconExit(beacon: ProBeacon) {}

    override fun onBeaconCamped(beacon: ProBeacon) {}

    override fun onRangedBeacons(beacons: List<ProBeacon>) {}

    override fun onRuleTriggered(rule: Rule) {}

    override fun onRuleTriggered(beacon: ProBeacon, rule: Rule) {
        Timber.e("onRuleTriggered called from background scan: $rule")
        val intent = Intent().apply {
            setClass(context, BackgroundScanResultReceiver::class.java)
            action = Constants.BACKGROUND_SCAN_BROADCAST_ACTON
            putExtra(Constants.BUNDLE_KEY_RULE, rule)
            putExtra(Constants.BUNDLE_KEY_BEACON, beacon)
            putExtra(Constants.BUNDLE_KEY_EVENT, "enter")
        }
        context.sendBroadcast(intent)
    }
}