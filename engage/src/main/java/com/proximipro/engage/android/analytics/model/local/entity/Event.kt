package com.proximipro.engage.android.analytics.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.proximipro.engage.android.util.currentTimeStamp
import java.util.UUID

/*
 * Created by Birju Vachhani on 06 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Entity class for events table
 * @property id String is the id of the event
 * @property type String is the type of the event (e.g. enter | exit)
 * @property beaconUUID String is the uuid of the beacon
 * @property major Int is the major value of the beacon
 * @property minor Int is the minor value of the beacon
 * @property time String is the time of creation
 * @property qos String is the quality of service determined from the device
 * @property beaconID String is the zone id for the rule triggered
 * @property sessionID String is the id of the current session used for this event
 * @property regionID String is same as [sessionID]
 * @property headerID String is the header id used for current session
 * @property eventInfo String contains other event related information
 */
@Entity
data class Event(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val beaconUUID: String = "",
    val major: String = "",
    val minor: String = "",
    val time: String = currentTimeStamp(),
    val qos: String = "",
    val beaconID: String = "",
    val sessionID: String,
    val regionID: String = sessionID,
    val headerID: String,
    val eventInfo: String
) {
    /**
     * Creates beacon key by appending [beaconUUID], [major] and [minor]
     */
    internal fun getBeaconKey(): String = "$beaconUUID:$major:$minor"
}