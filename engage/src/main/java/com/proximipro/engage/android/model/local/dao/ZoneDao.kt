package com.proximipro.engage.android.model.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.proximipro.engage.android.model.common.Zone

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Dao for zones table
 */
@Dao
abstract class ZoneDao : BaseDao<Zone> {

    /**
     * Retrieves all the zones from the local database
     * @return LiveData<List<Zone>>
     */
    @Query("SELECT * FROM zones")
    abstract fun getZones(): LiveData<List<Zone>>

    /**
     * Retrieves a particular zone from the local database
     * @param zoneId String
     * @return Zone
     */
    @Query("SELECT * FROM zones where zone_id = :zoneId")
    abstract suspend fun getZone(zoneId: String): Zone

    /**
     * Clears all the entries from the table
     * @return Int
     */
    @Query("DELETE from zones")
    abstract suspend fun clear(): Int
}