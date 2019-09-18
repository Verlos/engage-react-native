package com.proximipro.engage.android.di

import androidx.lifecycle.MutableLiveData
import com.proximipro.engage.android.core.BackgroundScanListener
import com.proximipro.engage.android.core.BeaconScanController
import com.proximipro.engage.android.core.EngageManager
import com.proximipro.engage.android.util.Constants
import org.altbeacon.beacon.BeaconManager
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

/*
 * Created by Birju Vachhani on 07 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Provides internal dependencies for SDK module
 */
internal val sdkModule = module(override = true) {

    // BeaconManager - AltBeacon manager responsible for beacon scanning
    single {
        BeaconManager.getInstanceForApplication(androidContext())
    }

    // EngageManager - Manager for ProBeacon scanning and scanning events
    factory {
        EngageManager(androidApplication())
    }

    // Provides scan controller which manages scan results and determines events
    factory {
        BeaconScanController()
    }

    // state live data for observing bluetooth on/off events
    single(Constants.BLUETOOTH_STATE_LIVEDATA) {
        MutableLiveData<Boolean>()
    }

    // provides [BackgroundScanListener] instance
    single {
        BackgroundScanListener(androidContext())
    }
}