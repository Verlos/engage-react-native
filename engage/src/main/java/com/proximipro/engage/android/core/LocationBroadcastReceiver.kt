/*
 * Copyright © 2019 Birju Vachhani (https://github.com/BirjuVachhani)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.proximipro.engage.android.core

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationResult
import timber.log.Timber

/*
 * Created by Birju Vachhani on 12 August 2019
 * Copyright © 2019 locus-android. All rights reserved.
 */

internal var backgroundLocationLiveData = MutableLiveData<Location>()

/**
 * Receives location updates and sets location live data value
 */
class LocationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "com.proximipro.engage.android.core.LocationBroadcastReceiver.action.PROCESS_UPDATES"

        /**
         * Creates pending intent that will be used for fusedLocationProviderClient to send location updates
         * @param context Context is the Android Context
         */
        fun getPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationBroadcastReceiver::class.java)
            intent.action =
                ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Received location update broadcast")
        intent ?: return
        if (intent.action == ACTION_PROCESS_UPDATES) {
            LocationResult.extractResult(intent)?.let { result ->
                if (result.locations.isNotEmpty()) {
                    Timber.d("Received location ${result.lastLocation}")
                    backgroundLocationLiveData.postValue(result.lastLocation)
                }
            }
        }
    }
}