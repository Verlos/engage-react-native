package com.proximipro.engage.android.analytics

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/*
 * Created by Birju Vachhani on 12 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Worker for syncing analytics data to the server periodically
 * @property manager AnalyticsManager manages analytics and provides access to the analytics apis
 */
class AnalyticsLogWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val manager: AnalyticsManager by lazy {
        AnalyticsManager(appContext)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val result = runCatching {
            manager.syncAnalytics()
        }
        if (result.isSuccess) {
            Result.success()
        } else {
            Timber.e(result.exceptionOrNull())
            Result.failure()
        }
    }

}