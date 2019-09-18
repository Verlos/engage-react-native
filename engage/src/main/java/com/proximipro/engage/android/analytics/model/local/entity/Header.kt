package com.proximipro.engage.android.analytics.model.local.entity

import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.proximipro.engage.android.BuildConfig
import com.proximipro.engage.android.util.currentTimeStamp

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Entity class for Header table
 * @property id String is the id of the header
 * @property locationAccuracy Float? is the accuracy of the location when the header was created
 * @property latitude Double? latitude value of the location at the time of header creation.
 * @property longitude Double? longitude value of the location at the time of header creation
 * @property deviceInfo String contains manufacturer and model info of the device
 * @property sdkVersion String is the current version of the used sdk
 * @property appId String it the registered user ID for the sdk
 * @property osVersion String is the current os version name of the device (e.g. M, N, O, P)
 * @property clientId String retrieved from the rule triggered
 * @property createdTime String the of the creation of the header [Epoch Time Millis]
 */
@Entity
data class Header(
    @PrimaryKey
    val id: String,
    val locationAccuracy: Float? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val deviceInfo: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    val sdkVersion: String = BuildConfig.VERSION_NAME,
    val appId: String,
    val osVersion: String,
    val clientId: String,
    val createdTime: String = currentTimeStamp()
)