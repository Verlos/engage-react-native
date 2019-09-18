package com.proximipro.engage.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.proximipro.engage.android.core.BackgroundScanListener
import com.proximipro.engage.android.core.EngageManager
import com.proximipro.engage.android.core.EngagePref
import com.proximipro.engage.android.core.EngageRepository
import com.proximipro.engage.android.core.LocationProvider
import com.proximipro.engage.android.di.coreModule
import com.proximipro.engage.android.di.databaseModule
import com.proximipro.engage.android.di.networkModule
import org.koin.android.ext.koin.with
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.get
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 14 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Broadcast Receiver to receive Android BOOT_COMPLETED events
 *
 * This receiver starts a foreground service on the device booths if the configs are matched.
 * @property pref EngagePref is the injected instance of [EngagePref] that will be used to perform operations on the preferences.
 */
class BootBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val backgroundScanListener: BackgroundScanListener by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.toString() != Intent.ACTION_BOOT_COMPLETED) return
        if (context == null) return
        Timber.e("Received Boot broadcast")
        loadKoinModules(coreModule, networkModule, databaseModule).with(context.applicationContext)
        val pref: EngagePref = get()
        EngageRepository.sync()
        val locationProvider: LocationProvider = get()
        locationProvider.startLocationUpdates(context)
        if (pref().isLocationBasedContentEnabled) {
            val provider: LocationProvider = get()
            provider.startLocationUpdates(context)
        }
        if (pref.data.isBackground && pref.data.isNotificationEnabled) {
            Timber.e("Starting background scan on boot")
            EngageManager(context.applicationContext).startBackgroundScan(
                context.applicationContext, backgroundScanListener
            )
        }
    }
}