package com.proximipro.engage.android.core

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.proximipro.engage.android.util.Constants
import timber.log.Timber

/*
 * Created by Birju Vachhani on 27 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Worker that runs periodically and sync the database with the server
 * @property pref EngagePref is the preference that contains all the configurable fields
 */
class DatabaseSyncWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {

    val pref = EngagePref(appContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE))

    override fun doWork(): Result {
        if (pref().apiKey.isBlank()) return Result.success()
        runCatching {
            Timber.e("Starting Db sync worker")
            EngageRepository.sync()
        }
        return Result.success()
    }
}