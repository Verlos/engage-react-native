package com.proximipro.engage.android.util

/*
 * Created by Birju Vachhani on 07 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Holds all the constants used in the library
 */
internal object Constants {
    const val APP_ID_NULL_MSG = "App ID must not be null. Please make sure that the user is registered."
    const val EVENT_ENTER = "enter"
    const val EVENT_EXIT = "exit"
    const val EVENT_CAMPING = "camping"
    const val NOTIFICATION_ACTION_REQUEST_CODE = 707
    const val PREF_NAME = "engage"
    const val BUNDLE_KEY_BEACON = "probeacon"
    const val BUNDLE_KEY_EVENT_INFO = "eventinfo"
    const val BUNDLE_KEY_EVENT = "event"
    const val BUNDLE_KEY_RULE = "rule"
    const val BUNDLE_KEY_ACTION = "action"
    const val STOP_SERVICE_BROADCAST_ACTON = "com.proximipro.engage.android.receiver.ServiceStopBroadcastReceiver"
    const val BACKGROUND_SCAN_BROADCAST_ACTON = "com.proximipro.engage.android.receiver.BackgroundScanResultReceiver"
    const val BLUETOOTH_STATE_LIVEDATA = "BLUETOOTH_STATE"
    const val SERVICE_NOTIFICATION_REQUEST_CODE = 457
    const val PATH_IBEACON = "ibeacon"
    const val PATH_LOCATION = "location"
    const val NETWORK_TYPE_NOT_CONNECTED = "not connected"
    const val NETWORK_TYPE_WIFI = "WIFI"
    const val NETWORK_TYPE_2G = "2G"
    const val NETWORK_TYPE_3G = "3G"
    const val NETWORK_TYPE_4G = "4G"
    internal const val DENIED = "denied"
    internal const val GRANTED = "granted"
    internal const val PERMANENTLY_DENIED = "permanently_denied"
    internal const val RESOLUTION_FAILED = "resolution_failed"
    internal const val LOCATION_SETTINGS_DENIED = "location_settings_denied"
    internal const val INTENT_EXTRA_CONFIGURATION = "request"
    internal const val INTENT_EXTRA_IS_BACKGROUND = "is_background"
    internal const val INTENT_EXTRA_PERMISSION_RESULT = "permission_result"
    internal const val INTERVAL_IN_MS = 2000L
    internal const val FASTEST_INTERVAL_IN_MS = 2000L
    internal const val MAX_WAIT_TIME_IN_MS = 2000L
}