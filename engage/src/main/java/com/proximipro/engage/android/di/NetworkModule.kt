package com.proximipro.engage.android.di

import com.google.gson.GsonBuilder
import com.proximipro.engage.android.model.remote.EngageApi
import com.proximipro.engage.android.model.remote.EngageApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/*
 * Created by Birju Vachhani on 06 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Provides Network related dependencies for SDK
 */
internal val networkModule = module(override = true) {

    // Logging Interceptor for logging network requests
    single<Interceptor> {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.d(message)
            }

        }).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttp Client
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .build()
    }

    // EngageApiService Instance
    single<EngageApiService> {
        Retrofit.Builder()
            .baseUrl(EngageApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get())
            .build()
            .create(EngageApiService::class.java)
    }
}