package com.proximipro.engage.android.model.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.proximipro.engage.android.analytics.model.local.dao.EventDao
import com.proximipro.engage.android.analytics.model.local.dao.HeaderDao
import com.proximipro.engage.android.analytics.model.local.entity.Event
import com.proximipro.engage.android.analytics.model.local.entity.Header
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.model.common.Zone
import com.proximipro.engage.android.model.common.ZoneRule
import com.proximipro.engage.android.model.local.DatabaseInfo

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Database implementation for room
 */
@Database(
    entities = [Rule::class, Zone::class, Event::class, Header::class],
    views = [ZoneRule::class],
    version = DatabaseInfo.VERSION,
    exportSchema = true
)
abstract class EngageDatabase : RoomDatabase() {

    /**
     * Provides instance of [RuleDao]
     * @return RuleDao
     */
    abstract fun ruleDao(): RuleDao

    /**
     * Provides instance of [ZoneDao]
     * @return RuleDao
     */
    abstract fun zoneDao(): ZoneDao

    /**
     * Provides instance of [ZoneRuleDao]
     * @return RuleDao
     */
    abstract fun zoneRuleDao(): ZoneRuleDao

    /**
     * Provides instance of [EventDao]
     * @return EventDao
     */
    abstract fun eventDao(): EventDao

    /**
     * Provides instance of [HeaderDao]
     * @return HeaderDao
     */
    abstract fun headerDao(): HeaderDao

    /**
     * Syncs the local database by removing all the old entries and adding new ones
     * @param rules List<Rule> is the list of rules fetched from the server
     * @param zones List<Zone> is the list of the zones fetched from the server
     */
    suspend fun syncData(rules: List<Rule>, zones: List<Zone>) = withTransaction {
        ruleDao().apply {
            clear()
            insert(rules)
        }
        zoneDao().apply {
            clear()
            insert(zones)
        }
    }

    /**
     * Retrieves [ZoneRule] object from the local database that satisfies the given constraints
     * @param major Int is the major value of the beacon
     * @param minor Int is the minor value of the beacon
     * @param event String is the beacon event string
     * @param uuid String is the unique uuid of the beacon
     * @return ZoneRule?
     */
    suspend fun getZoneRule(major: Int, minor: Int, event: String, uuid: String): ZoneRule? {
        return zoneRuleDao().getZoneRule(major, minor, event, uuid)
    }
}