package com.proximipro.engage.android.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.proximipro.engage.android.core.EngageInfo
import com.proximipro.engage.android.receiver.ServiceStopBroadcastReceiver

/*
 * Created by Birju Vachhani on 13 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */


/**
 * Checks whether the [permission] is granted or not
 * @receiver Context is the Android [Context]
 * @param permission String is the [permission] which need to be checked
 * @return Boolean true if the [permission] is granted, false otherwise.
 */
internal fun Context.hasPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    } else {
        hasPermissionsDefinedInManifest(permission)
    }
}

/**
 * Checks whether the [permission] is defined in the manifest or not
 * @receiver Context is the Android [Context]
 * @param permission String is the [permission] which needs to be checked
 * @return Boolean true if the [permission] is defined in the manifest file, false otherwise.
 */
internal fun Context.hasPermissionsDefinedInManifest(permission: String): Boolean {
    return packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        ?.requestedPermissions?.contains(permission)
        ?: false
}

/**
 * Creates a notification object that will be used for starting foreground service
 * @receiver Context is the Android [Context]
 * @param info EngageInfo is the data class which contains all the configurable information of the sdk
 * @return Notification which will be used to start a service in foreground
 */
fun Context.createServiceNotification(info: EngageInfo): Notification {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    val notificationInfo = info.serviceNotificationInfo
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(
                notificationInfo.channelId,
                notificationInfo.channelName,
                notificationInfo.priority
            )
        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(this, notificationInfo.channelId)
        .setSmallIcon(notificationInfo.smallIconId)
        .setContentTitle(info.appName)
        .setContentText(notificationInfo.content)
        .setAutoCancel(notificationInfo.autoCancel)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                Constants.SERVICE_NOTIFICATION_REQUEST_CODE,
                getActivityIntent(this, info.pendingIntentClassName),
                PendingIntent.FLAG_ONE_SHOT
            )
        )
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                priority = notificationInfo.priority
            }
        }.addAction(
            NotificationCompat.Action(
                0, notificationInfo.actionText, PendingIntent.getBroadcast(
                    this,
                    Constants.NOTIFICATION_ACTION_REQUEST_CODE,
                    Intent().apply {
                        setClass(this@createServiceNotification, ServiceStopBroadcastReceiver::class.java)
                        action = Constants.STOP_SERVICE_BROADCAST_ACTON
                    },
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
        )
        .build()
}

/**
 * Provides an intent that contains launch activity component class.
 * @param context Context is the Android context
 * @return Intent is the returned intent with launcher activity component
 */
fun getActivityIntent(context: Context, name: String = ""): Intent {
    return if (name.isBlank()) {
        val packManager = context.packageManager
        val intent = packManager.getLaunchIntentForPackage(context.packageName)
        val cName = intent?.component
        Intent.makeRestartActivityTask(cName).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    } else {
        val c = Class.forName(name)
        Intent(context, c).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

}

/**
 * Extension function to check whether internet connectivity is available or not
 * @return true is internet connectivity is available, false otherwise
 * */
fun Context.hasInternet(): Boolean = (this.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
    ?.activeNetworkInfo?.isConnected
    ?: false