package com.proximipro.engage.android.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import com.proximipro.engage.android.Engage
import com.proximipro.engage.android.core.InitializationRequest
import com.proximipro.engage.android.exception.SdkNotInitializedException

/*
 * Created by Birju Vachhani on 08 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Top level function that returns singleton instance of [Engage] class
 * @return Engage which is the singleton instance and only entry point of the sdk
 */
@JvmSynthetic
fun getEngage(): Engage = Engage.getInstance() ?: throw SdkNotInitializedException()

/**
 * Top level function that returns singleton instance of [Engage] class
 * @return Engage which is the singleton instance and only entry point of the sdk, returns null if the sdk is not initialized at the current time
 */
@JvmSynthetic
fun getEngageOrNull(): Engage? = Engage.getInstance()

/**
 * Initializes the SDK after verifying the provided [apiKey].
 *
 * If the api key is incorrect than the sdk will not be initialized. API key is verified using internet
 * connection. In case of no internet connection, it will not be initialized.
 *
 * @param application Application is the Android [Application] instance
 * @param apiKey String is the api key which will be verified and after that the sdk will be initialized
 * @param callback InitializationCallback is the callback instance on which initialization event will be triggered
 */
@JvmSynthetic
fun initializeEngage(
    application: Application,
    request: InitializationRequest,
    callback: InitializationCallback = object : InitializationCallback() {}
) {
    Engage.initialize(application, request, callback)
}

/**
 * Retrieves current quality of service which is the type of internet connection used by the device
 * @param context Context is the android context
 * @return String which is qos
 */
fun getQualityOfService(context: Context): String {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return Constants.NETWORK_TYPE_NOT_CONNECTED
    val network =
        connectivityManager.allNetworks.firstOrNull() ?: return Constants.NETWORK_TYPE_NOT_CONNECTED
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
        Constants.NETWORK_TYPE_WIFI
    } else {
        getInternetType(context)
    }
}

/**
 * Retrieves which type of internet connection is being used when this method called. [e.g. 2G, 3G, 4G]
 * @param context Context is the android context
 * @return String which is the type of internet being used
 */
private fun getInternetType(context: Context): String {
    val mTelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return Constants.NETWORK_TYPE_NOT_CONNECTED
    return when (mTelephonyManager.networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS,
        TelephonyManager.NETWORK_TYPE_EDGE,
        TelephonyManager.NETWORK_TYPE_CDMA,
        TelephonyManager.NETWORK_TYPE_1xRTT,
        TelephonyManager.NETWORK_TYPE_IDEN -> Constants.NETWORK_TYPE_2G
        TelephonyManager.NETWORK_TYPE_UMTS,
        TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_EVDO_A,
        TelephonyManager.NETWORK_TYPE_HSDPA,
        TelephonyManager.NETWORK_TYPE_HSUPA,
        TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_EVDO_B,
        TelephonyManager.NETWORK_TYPE_EHRPD,
        TelephonyManager.NETWORK_TYPE_HSPAP -> Constants.NETWORK_TYPE_3G
        TelephonyManager.NETWORK_TYPE_LTE -> Constants.NETWORK_TYPE_4G
        else -> Constants.NETWORK_TYPE_NOT_CONNECTED
    }
}

/**
 * Creates current timestamp which is divided by 1000
 */
fun currentTimeStamp(): String = (System.currentTimeMillis() / 1000).toString()