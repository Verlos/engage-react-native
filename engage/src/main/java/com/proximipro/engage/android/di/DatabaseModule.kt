package com.proximipro.engage.android.di

import androidx.room.Room
import com.proximipro.engage.android.analytics.AnalyticsManager
import com.proximipro.engage.android.model.local.DatabaseInfo
import com.proximipro.engage.android.model.local.dao.EngageDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

/*
 * Created by Birju Vachhani on 24 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

val databaseModule = module(override = true) {

    single {
        Room.databaseBuilder(androidContext(), EngageDatabase::class.java, DatabaseInfo.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        get<EngageDatabase>().ruleDao()
    }

    single {
        get<EngageDatabase>().zoneDao()
    }

    single {
        AnalyticsManager(androidContext())
    }
}