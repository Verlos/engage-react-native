package com.proximipro.engage.android.analytics.model.remote

/**
 * Holds data that needs to be sent for analytics call
 * @property locationAccuracy String is the accuracy of the location
 * @property osVersion String is the current version of the Android OS
 * @property created String is the time when the header was created
 * @property appid String is the app ID of the SDK
 * @property location LocationItem? is the location recorded for the header
 * @property sdkVersion String is the current version of the SDK
 * @property key String is the beacon key
 * @property deviceInfo String is the manufacturer and model info
 * @property events List<EventsItem>? holds all the events for the same header
 */
data class AnalyticLogRequest(
    val locationAccuracy: String = "",
    val osVersion: String = "",
    val created: String = "",
    val appid: String = "",
    val location: LocationItem?,
    val sdkVersion: String = "",
    val key: String = "",
    val deviceInfo: String = "",
    val events: List<EventsItem>?
)

/**
 * Represents location for analytics api call
 * @property lon String is the longitude
 * @property lat String is the latitude
 */
data class LocationItem(
    val lon: String = "",
    val lat: String = ""
)

/**
 * Holds info related to an event that needs to be sent via analytics api call
 * @property eventInfo String contains extra info about the event
 * @property regionSessionId String is the same as session ID
 * @property organization String is the client ID
 * @property eventType String is the type of the event occurred on beacon or region
 * @property time String is the time when the event was recorded
 * @property sessionId String is the ID of the event session
 * @property beaconKey String is the string made from beacon uuid, major and minor
 */
data class EventsItem(
    val eventInfo: String = "",
    val regionSessionId: String = "",
    val organization: String = "",
    val eventType: String = "",
    val time: String = "",
    val sessionId: String = "",
    val beaconKey: String = ""
)


