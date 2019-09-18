package com.proximipro.engage.android.di

import android.content.Context
import android.content.SharedPreferences
import com.proximipro.engage.android.core.EngagePref
import com.proximipro.engage.android.core.LocationProvider
import com.proximipro.engage.android.util.Constants
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import java.util.UUID

/*
 * Created by Birju Vachhani on 06 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

val coreModule = module(override = true) {

    // provides singleton of [SharedPreferences] dedicated for library use
    single<SharedPreferences> {
        androidContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    // EngagePref - data that persists app restarts
    single {
        EngagePref(get<SharedPreferences>())
    }

    // provides [Region] instance
    single {
        val pref = get<EngagePref>()
        Region(
            pref().regionId,
            // TODO: check for valid uuid when changed from settings
            Identifier.fromUuid(UUID.fromString(pref().beaconUUID)),
            null,
            null
        )
    }

    single {
        LocationProvider(get())
    }
}