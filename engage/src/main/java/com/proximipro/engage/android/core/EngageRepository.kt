package com.proximipro.engage.android.core

import com.proximipro.engage.android.model.common.ZoneRule
import com.proximipro.engage.android.model.local.dao.EngageDatabase
import com.proximipro.engage.android.model.remote.EngageApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Repository that manages zones and rules
 */
internal object EngageRepository : KoinComponent {

    private val apiService: EngageApiService by inject()
    private val db: EngageDatabase by inject()
    private val pref: EngagePref by inject()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    /**
     * Syncs the local db with the server
     */
    fun sync() {
        if (pref().apiKey.isBlank()) {
            Timber.e("API key is not set")
            return
        }
        Timber.e("Starting synchronization...")
        scope.launch {
            runCatching {
                Timber.e("Fetching rules")
                val rules = apiService.fetchRules(pref().apiKey)
                Timber.e("Fetching zones")
                val zones = apiService.fetchZones(pref().apiKey)
                Timber.e("Syncing database...")
                db.syncData(rules, zones)
                Timber.e("Database synced successfully")
            }.onFailure {
                Timber.e(it)
                Timber.e("Failed to sync database")
            }
        }
    }

    /**
     * Retrieves Zone and rule from the local database
     * @param major Int is the beacon major value
     * @param minor Int is the beacon minor value
     * @param event String is the beacon event for which the query is performed
     * @param uuid String is the uuid of the beacon
     * @return ZoneRule? that is the matched active rules for the current beacon
     */
    suspend fun getZoneRule(major: Int, minor: Int, event: String, uuid: String): ZoneRule? =
        db.getZoneRule(major, minor, event, uuid)
}