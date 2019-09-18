package com.proximipro.engage.android.model.common

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import kotlinx.android.parcel.Parcelize

/*
 * Created by Birju Vachhani on 25 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Contains combined properties of rules and zones
 */
@DatabaseView("""SELECT * FROM rules AS rule INNER JOIN zones AS zone ON zone.zone_id = rule.zone""")
@Parcelize
data class ZoneRule(
    @Embedded
    val zone: Zone,
    @Embedded
    val rule: Rule
) : Parcelable