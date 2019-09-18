package com.proximipro.engage.android.core

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.proximipro.engage.android.util.Constants
import timber.log.Timber

/*
 * Created by Birju Vachhani on 14 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Provides location and manages [FusedLocationProviderClient]
 * @property fusedLocationProviderClient is the actual location client that provides location
 * @property locationRequest LocationRequest is the request configuration for the [FusedLocationProviderClient]
 * @property location Location? is the property that holds the latest location
 * @property isOnGoing Boolean determines whether the continuous location is on-going or not
 * @property locationCallback <no name provided> provides a way to listen for the location results
 */
internal class LocationProvider(context: Context) {

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val pendingIntent: PendingIntent by lazy {
        LocationBroadcastReceiver.getPendingIntent(context)
    }


    private val locationRequest = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = Constants.INTERVAL_IN_MS
        fastestInterval = Constants.FASTEST_INTERVAL_IN_MS
        maxWaitTime = Constants.MAX_WAIT_TIME_IN_MS
    }

    private var location: Location? = null

    init {
        backgroundLocationLiveData.observeForever {
            if (it != null) {
                this.location = it
            }
        }
    }

    private var isOnGoing = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.let { result ->
                Timber.e("Received Location Update")
                if (result.locations.isNotEmpty()) {
                    location = result.locations.first()
                }
            }
        }
    }

    /**
     * Returns the most recent location
     * @return Location? is the most recent location
     */
    @SuppressLint("MissingPermission")
    internal fun getLocation(): Location? = location

    /**
     * starts continuous location retrieval process
     * @param context Context is the Android context
     */
    @SuppressLint("MissingPermission")
    internal fun startLocationUpdates(context: Context) {
        if (!hasLocationPermission(context)) {
            return
        }
        Timber.e("Starting location updates")
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            pendingIntent
        )?.addOnFailureListener {
            Timber.e(it)
            fusedLocationProviderClient.lastLocation?.addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                task.result?.let { location ->
                    backgroundLocationLiveData.postValue(location)
                }
            }?.addOnFailureListener { e ->
                Timber.e(e)
            }
        }
        isOnGoing = true
    }

    /**
     * Check whether the app has location permission or not
     * @param context Context is the Android context
     * @return Boolean true, if the app has location permission, false otherwise
     */
    private fun hasLocationPermission(context: Context) = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * Stops on-going continuous location retrieval process
     */
    fun stopLocationUpdates() {
        isOnGoing = false
        Timber.e("Location updates stopped")
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}