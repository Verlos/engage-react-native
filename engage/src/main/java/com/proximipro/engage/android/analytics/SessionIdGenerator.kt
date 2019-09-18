package com.proximipro.engage.android.analytics

import android.location.Location
import com.proximipro.engage.android.analytics.SessionIdGenerator.LOCATION_DISTANCE_THRESHOLD
import com.proximipro.engage.android.analytics.SessionIdGenerator.TIME_THRESHOLD_IN_MINUTES
import java.util.UUID
import java.util.concurrent.TimeUnit

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Generates session ID based on the conditions defined below:
 *
 * 1. If the previous session ID is null which mean if this is
 * the first request for session ID then create a new session ID.
 *
 * 2. If the time of the previous session ID generation is [TIME_THRESHOLD_IN_MINUTES] minutes or more,
 * then create a new session ID and reset the generation time.
 *
 * 3. If the location of previously generated session ID is
 * far then [LOCATION_DISTANCE_THRESHOLD] meters or more then
 * create a new session ID.
 *
 * 4. If none of the above conditions match then return the same previous session ID
 */
object SessionIdGenerator {

    private const val RESULT_SIZE = 3
    private var lastCreatedTime: Long = -1
    private var sessionId: String = ""
    private var previousLocation: Location? = null
    private const val LOCATION_DISTANCE_THRESHOLD = 1000
    private const val TIME_THRESHOLD_IN_MINUTES = 15L

    /**
     * Retrieves the sessionID using above rules
     * @param location Location? is the location of the device at the time when the rule was triggered
     * @return String is the preferred sessionID
     */
    fun getSessionId(location: Location? = null): String {
        if (lastCreatedTime == -1L) {
            sessionId = createNew()
            return sessionId
        }
        val diff = System.currentTimeMillis() - lastCreatedTime
        if (diff >= TimeUnit.MINUTES.toMillis(TIME_THRESHOLD_IN_MINUTES)) {
            sessionId = createNew()
            return sessionId
        }
        if (location != null && previousLocation != null && isThresholdReached(location)) {
            sessionId = createNew()
        }
        return sessionId
    }

    /**
     * Determines whether the current location of the device is far
     * enough to be equal to [LOCATION_DISTANCE_THRESHOLD] or more.
     *
     * @param currentLocation Location is the current location of the devices
     * @return Boolean true if current location is far than previous location, false otherwise.
     */
    private fun isThresholdReached(currentLocation: Location): Boolean {
        val previous = previousLocation ?: return false
        val result = FloatArray(RESULT_SIZE)
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            previous.latitude,
            previous.longitude,
            result
        )
        return result.first() >= LOCATION_DISTANCE_THRESHOLD
    }

    /**
     * Creates new session ID and resets the [lastCreatedTime]
     * @return String
     */
    private fun createNew(): String {
        lastCreatedTime = System.currentTimeMillis()
        return UUID.randomUUID().toString()
    }
}