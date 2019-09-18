package com.proximipro.engage.android.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.proximipro.engage.android.util.Constants
import timber.log.Timber

/*
 * Created by Birju Vachhani on 10 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * SharedPreference wrapper for managing preferences specific to this SDK
 * @property pref SharedPreferences is the injected instance of [SharedPreferences] for the sdk
 * @property data EngageInfo is the data class which contains all the configurable information of the sdk
 */
class EngagePref internal constructor(private val pref: SharedPreferences) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    internal constructor(context: Context) : this(
        context.getSharedPreferences(
            Constants.PREF_NAME,
            Context.MODE_PRIVATE
        )
    )

    init {
        pref.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        data = retrieveData()
    }

    internal var data: EngageInfo = retrieveData()

    /**
     * Retrieves store data from preferences and converts raw data into model class [EngageInfo]
     *
     * On empty result, creates a default instance of [EngageInfo] with all the default configurations.
     * @return EngageInfo is the data class which contains all the configurable information of the sdk
     */
    private fun retrieveData(): EngageInfo = if (!pref.contains(this::class.java.simpleName)) {
        EngageInfo()
    } else {
        try {
            Gson().fromJson(pref.getString(this::class.java.simpleName, ""), EngageInfo::class.java)
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            EngageInfo()
        }
    }

    /**
     * Invoke operator that provides [EngageInfo] object access on object invocation as method
     * @return EngageInfo is the data class which contains all the configurable information of the sdk
     */
    internal operator fun invoke() = data

    /**
     * Saves current [data] instance into the preferences asynchronously.
     */
    internal fun saveAsync() {
        pref.edit().putString(this::class.java.simpleName, Gson().toJson(data)).apply()
    }

    /**
     * Saves current [data] instance into the preferences synchronously.
     */
    @SuppressLint("ApplySharedPref")
    internal fun save() {
        pref.edit().putString(this::class.java.simpleName, Gson().toJson(data)).commit()
    }

    /**
     * Provides a lambda block to edit [EngageInfo].
     *
     * Responsible for saving changes after the process of updating [EngageInfo].
     * It writes changes to the preferences asynchronously.
     *
     * @param func EngageInfo.() -> Unit is the lambda block that provides access to the [EngageInfo] instance
     */
    internal fun update(func: EngageInfo.() -> Unit) {
        data.func()
        saveAsync()
    }

    /**
     * Provides a lambda block to edit [EngageInfo].
     *
     * Responsible for saving changes after the process of updating [EngageInfo].
     * It writes changes to the preferences synchronously.
     *
     * @param func EngageInfo.() -> Unit is the lambda block which provides access to the [EngageInfo] instance.
     */
    internal fun updateSync(func: EngageInfo.() -> Unit) {
        data.func()
        save()
    }

    /**
     * Provides a lambda block to edit [NotificationInfo].
     *
     * Responsible for saving changes after the process of updating [NotificationInfo].
     * It writes changes to the preferences asynchronously.
     *
     * @param func EngageInfo.() -> Unit is the lambda block which provides access to the [EngageInfo] instance.
     */
    internal fun updateNotificationInfo(func: NotificationInfo.() -> Unit) {
        data.serviceNotificationInfo.func()
        saveAsync()
    }

    /**
     * Resets the sdk preferences synchronously
     */
    @SuppressLint("ApplySharedPref")
    internal fun reset() {
        pref.edit().clear().commit()
        data = retrieveData()
    }

    /**
     * Provides the stored api key for the sdk
     * @return String is the api key of the sdk
     */
    fun getApiKey() = data.apiKey

    /**
     * Provides the stored beacon uuid for the sdk
     * @return String is the beacon uuid of the sdk
     */
    fun getBeaconUuid() = data.beaconUUID
}