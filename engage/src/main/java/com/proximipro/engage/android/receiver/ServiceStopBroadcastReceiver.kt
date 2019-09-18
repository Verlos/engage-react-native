package com.proximipro.engage.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.proximipro.engage.android.core.EngageBeaconConsumer
import com.proximipro.engage.android.core.LocationProvider
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.service.BeaconService
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 14 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Broadcast receiver to stop [BeaconService]
 *
 * Triggered when exit action is performed on the notification.
 * Stops the running [BeaconService].
 */
class ServiceStopBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val region: Region by inject()
    private val locationProvider: LocationProvider by inject()
    private val consumer = EngageBeaconConsumer

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("Received broadcast to stop Foreground Service")
        if (context == null) return
        BeaconManager.getInstanceForApplication(context).apply {
            backgroundMode = false
            if (isBound(consumer)) {
                removeAllRangeNotifiers()
                stopRangingBeaconsInRegion(region)
                unbind(consumer)
                locationProvider.stopLocationUpdates()
                disableForegroundServiceScanning()
            }
        }
    }
}