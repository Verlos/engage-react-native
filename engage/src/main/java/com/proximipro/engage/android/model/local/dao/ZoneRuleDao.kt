package com.proximipro.engage.android.model.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.proximipro.engage.android.model.common.ZoneRule

/*
 * Created by Birju Vachhani on 25 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * DatabaseView for getting information of zone and rule association
 */
@Dao
abstract class ZoneRuleDao {

    /**
     * Retrieves information related to zone and rule that matched the given criteria
     * @param major Int is the major value of the zone
     * @param minor Int is the minor value of the zone
     * @param event String is the beacon event
     * @param uuid String is the unique uuid of the beacon
     * @return ZoneRule?
     */
    @Query("SELECT * FROM zonerule WHERE major=:major and minor = :minor and triggerOn = :event and uuid = UPPER(:uuid)")
    abstract suspend fun getZoneRule(major: Int, minor: Int, event: String, uuid: String): ZoneRule?
}