package com.proximipro.engage.android.analytics

import android.content.Context
import android.location.Location
import android.os.Build
import com.proximipro.engage.android.analytics.model.local.entity.Event
import com.proximipro.engage.android.analytics.model.local.entity.Header
import com.proximipro.engage.android.analytics.model.remote.AnalyticLogRequest
import com.proximipro.engage.android.analytics.model.remote.EventsItem
import com.proximipro.engage.android.analytics.model.remote.LocationItem
import com.proximipro.engage.android.core.EngagePref
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.model.local.dao.EngageDatabase
import com.proximipro.engage.android.model.remote.EngageApiService
import com.proximipro.engage.android.util.Constants
import com.proximipro.engage.android.util.getQualityOfService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.TimeUnit

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Manages all the things related to analytics
 * @property db EngageDatabase is the database used by the sdk
 * @property pref EngagePref is the preferences used by the sdk
 * @property job CompletableJob used for creating coroutine scope
 * @property scope CoroutineScope used to execute db commands
 */
class AnalyticsManager(private val context: Context) : KoinComponent {

    val db: EngageDatabase by inject()
    val pref: EngagePref by inject()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val apiService: EngageApiService by inject()
    private var eventCount: Int = 0
    private var lastCreatedTime: Long = -1
    private lateinit var previousHeader: Header

    companion object {
        private const val HEADER_THRESHOLD_IN_HOURS = 1L
        private const val MAX_EVENT_COUNT_THRESHOLD = 10
    }

    /**
     * Creates new header using the information provided and resets the time of
     * the most recent header creation
     * @param appId String is the appID for the registered user
     * @param clientId String retrieved from the rule triggered
     * @param location Location? is the location of the device at time when the rule was triggered
     * @return Header that will be associated with the event
     */
    private fun createNewHeader(appId: String, clientId: String, location: Location?): Header {
        lastCreatedTime = System.currentTimeMillis()
        return Header(
            id = UUID.randomUUID().toString(),
            locationAccuracy = location?.accuracy,
            latitude = location?.latitude,
            longitude = location?.longitude,
            appId = appId,
            clientId = clientId,
            osVersion = getOsVersion()
        )
    }

    /**
     * Retrieves the operation system version name
     * @return String the name of the os that is running in the device currently
     */
    private fun getOsVersion(): String {
        return when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1 -> "L"
            Build.VERSION_CODES.M -> "M"
            Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1 -> "N"
            Build.VERSION_CODES.O, Build.VERSION_CODES.O_MR1 -> "O"
            Build.VERSION_CODES.P -> "P"
            Build.VERSION_CODES.Q -> "Q"
            else -> "Undefined"
        }
    }

    /**
     * Returns appropriate header for the event
     * @param appId String is the user id of the sdk
     * @param clientId String is the client ID for the sdk
     * @param location Location? is the current location
     * @return Header that will be linked to the events
     */
    private fun getHeader(appId: String, clientId: String, location: Location? = null): Header {
        if (!::previousHeader.isInitialized || (System.currentTimeMillis() - lastCreatedTime) >= TimeUnit.HOURS.toMillis(
                HEADER_THRESHOLD_IN_HOURS
            ) || eventCount >= MAX_EVENT_COUNT_THRESHOLD
        ) {
            eventCount = 0
            previousHeader = createNewHeader(
                appId = appId,
                clientId = clientId,
                location = location
            )
        }
        return previousHeader
    }

    /**
     * Records the event by storing it in database using proper sessionID and headerID
     * @param beacon ProBeacon is the beacon for which the event is fired
     * @param rule Rule is the rule triggered on the event
     * @param location Location? is the location of the device when the event was triggered
     */
    fun logBeaconEvent(
        beacon: ProBeacon,
        rule: Rule,
        eventInfo: String,
        location: Location? = null
    ) {
        val header = getHeader(pref().appId, rule.clientId.toString(), location)
        val event = Event(
            id = UUID.randomUUID().toString(),
            type = if (rule.triggerOn == Constants.EVENT_ENTER) Analytics.EVENT_TYPE_BEACON_ENTER else Analytics.EVENT_TYPE_BEACON_EXIT,
            beaconUUID = beacon.uuid,
            major = beacon.major.toString(),
            minor = beacon.minor.toString(),
            qos = getQualityOfService(context),
            beaconID = rule.zone,
            sessionID = SessionIdGenerator.getSessionId(location),
            headerID = header.id,
            eventInfo = eventInfo
        )
        record(event, header)
    }

    /**
     * Logs login event for analytics
     * @param eventInfo String is the extra info related to the event
     */
    fun logLoginEvent(eventInfo: String) {
        val header = getHeader(pref().appId, pref().clientId)
        val event = Event(
            type = Analytics.EVENT_TYPE_LOGIN,
            qos = getQualityOfService(context),
            sessionID = SessionIdGenerator.getSessionId(),
            headerID = header.id,
            eventInfo = eventInfo
        )
        record(event, header)
    }

    /**
     * Logs region enter event for analytics
     * @param eventInfo String is the extra info related to the event
     */
    fun logRegionEnterEvent(eventInfo: String) {
        val header = getHeader(pref().appId, pref().clientId)
        val event = Event(
            type = Analytics.EVENT_TYPE_REGION_ENTER,
            qos = getQualityOfService(context),
            sessionID = SessionIdGenerator.getSessionId(),
            headerID = header.id,
            eventInfo = eventInfo
        )
        record(event, header)
    }

    /**
     * Logs region exit event for analytics
     * @param eventInfo String is the extra info related to the event
     */
    fun logRegionExitEvent(eventInfo: String) {
        val header = getHeader(pref().appId, pref().clientId)
        val event = Event(
            type = Analytics.EVENT_TYPE_REGION_EXIT,
            qos = getQualityOfService(context),
            sessionID = SessionIdGenerator.getSessionId(),
            headerID = header.id,
            eventInfo = eventInfo
        )
        record(event, header)
    }

    /**
     * Logs rule trigger event for analytics
     * @param eventInfo String is the extra info related to the event
     */
    fun logRuleTriggeredEvent(eventInfo: String) {
        val header = getHeader(pref().appId, pref().clientId)
        val event = Event(
            type = Analytics.EVENT_TYPE_RULE_TRIGGERED,
            qos = getQualityOfService(context),
            sessionID = SessionIdGenerator.getSessionId(),
            headerID = header.id,
            eventInfo = eventInfo
        )
        record(event, header)
    }

    /**
     * Stores the analytics logs to the db
     * @param event Event is the event data created for analytics
     * @param header Header is the header used for the event
     */
    private fun record(event: Event, header: Header) {
        eventCount += 1
        scope.launch {
            runCatching {
                Timber.e("Storing event in db")
                db.eventDao().insert(event)
                Timber.e("Storing header in db")
                db.headerDao().insert(header)
            }.onFailure(Timber::e)
        }
    }

    /**
     * Syncs the analytics information to the server and removes synced data from the db
     */
    suspend fun syncAnalytics() {
        runCatching {
            db.headerDao().getAllHeaders().forEach { header ->
                val events = db.eventDao().getHeaderEvents(header.id)
                val request = createAnalyticsRequest(header, events)
                val response = apiService.sendAnalyticsAsync(request)
                if (response == "ok") {
                    // remove synced data from db
                    Timber.e("Deleting synced data from db")
                    db.eventDao().deleteEventsForHeader(header.id)
                    db.headerDao().delete(header)
                }
            }
        }.onFailure(Timber::e)
            .onSuccess {
                Timber.e("Synced analytics successfully with the server")
            }
    }

    /**
     * Creates [AnalyticLogRequest] instance using provided header and events information
     * @param header Header is the header for all the events that needs to be sent via api call
     * @param events List<Event> is the events having the same header
     */
    private fun createAnalyticsRequest(header: Header, events: List<Event>): AnalyticLogRequest {
        return AnalyticLogRequest(
            osVersion = header.osVersion,
            created = header.createdTime,
            appid = header.appId,
            locationAccuracy = header.locationAccuracy.toString(),
            sdkVersion = header.sdkVersion,
            key = pref().apiKey,
            deviceInfo = header.deviceInfo,
            events = events.map {
                EventsItem(
                    eventInfo = it.eventInfo,
                    regionSessionId = it.regionID,
                    organization = pref().clientId,
                    eventType = it.type,
                    time = it.time,
                    sessionId = it.sessionID,
                    beaconKey = it.getBeaconKey()
                )
            },
            location = LocationItem(
                lon = "${header.longitude}",
                lat = "${header.latitude}"
            )
        )
    }
}