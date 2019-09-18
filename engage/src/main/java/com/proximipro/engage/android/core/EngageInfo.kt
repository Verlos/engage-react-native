package com.proximipro.engage.android.core

import android.app.NotificationManager
import androidx.annotation.DrawableRes
import com.proximipro.engage.android.R
import java.util.UUID

/*
 * Created by Birju Vachhani on 10 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Dsl marker for [EngageConfig] to create type safe dsl
 */
@DslMarker
internal annotation class EngageConfigMarker

/**
 * Stores all the configuration info related to the sdk
 * @property apiKey String is the api key used for the sdk
 * @property pendingIntentClassName String is the class name of the activity that should be invoked
 * whenever a notification is clicked
 * @property appId String is the appId for registered user.
 * @property isApiKeyVerified Boolean true if the sdk is verified by api key and is initialized, false otherwise
 * @property isBackground Boolean true if background scan is allowed, false otherwise
 * @property isNotificationEnabled Boolean true if beacon scan notification are allowed to be shown, false otherwise
 * @property regionId String is the id to create a region in order to start beacon scan process
 * @property beaconUUID String is the beacon uuid that will be used to filter beacons in scanning process
 * @property beaconLayout String is the layout format for the beacons to identify them based on its type.
 * @property serviceNotificationInfo NotificationInfo contains all the configurable information about the notification that will be created in order to start a service in foreground
 */
@EngageConfigMarker
data class EngageInfo internal constructor(
    internal var apiKey: String = "",
    internal var appId: String = "daa${UUID.randomUUID()}",
    var isBackground: Boolean = false,
    var appName: String = "Engage",
    var clientId: String = "",
    var isNotificationEnabled: Boolean = false,
    var scanInterval: Long = 5,
    var backgroundScanInterval: Long = 5000,
    internal var backgroundBetweenScanInterval: Long = 0,
    var enableAutoRefresh: Boolean = false,
    var autoRefreshInterval: Long = 30L,
    var pendingIntentClassName: String = "",
    var regionId: String = "proximiPro",
    internal var isApiKeyChanged: Boolean = false,
    internal var isApiKeyVerified: Boolean = false,
    var isLocationBasedContentEnabled: Boolean = false,
    var beaconUUID: String = "A7AE2EB7-1F00-4168-B99B-A749BAC1CA64",
    internal var beaconLayout: String = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24",
    internal var serviceNotificationInfo: NotificationInfo = NotificationInfo()
) {

    /**
     * Provides access to the [NotificationInfo] object for notification configuration
     * @param func NotificationInfo.() -> Unit is the lambda block in which [NotificationInfo] can be accessed
     */
    fun configureNotification(func: NotificationInfo.() -> Unit) {
        serviceNotificationInfo.func()
    }
}

/**
 * Stored all the configuration related to service notification for the sdk
 * @property id Int is the notification ID
 * @property channelId String is the channel id used for the notification
 * @property channelName String is the name of the channel in which the notification will be displayed
 * @property priority Int determines the priority level of the notification
 * @property content String is the content of the notification
 * @property autoCancel Boolean true if the notification is supposed to be cancelled automatically, false otherwise
 * @property smallIconId Int is resource id of the small icon used in the notification
 */
@EngageConfigMarker
data class NotificationInfo internal constructor(
    internal var id: Int = 44,
    internal var channelId: String = "444",
    var channelName: String = "Engage",
    var priority: Int = NotificationManager.IMPORTANCE_DEFAULT,
    var content: String = "Engage is scanning",
    internal var autoCancel: Boolean = true,
    @DrawableRes var smallIconId: Int = R.drawable.ic_update,
    var actionText: String = "Stop Scan"
)