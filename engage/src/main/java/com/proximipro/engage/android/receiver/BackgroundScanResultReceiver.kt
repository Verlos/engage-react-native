package com.proximipro.engage.android.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.proximipro.engage.android.R
import com.proximipro.engage.android.core.EngagePref
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Action
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.model.remote.EngageApiService
import com.proximipro.engage.android.util.Constants
import com.proximipro.engage.android.util.getActivityIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import kotlin.random.Random

/*
 * Created by Birju Vachhani on 05 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Broadcast receiver that receives background scan results and creates notifications out of it
 * @property apiService EngageApiService is the retrofit service that will be used to make api calls
 * @property pref EngagePref provides access to sdk preferences
 * @property notificationChannelId String will be used to provide channel to the notifications
 * @property notificationChannelName String is the name of the channel in which notifications will be displayed
 * @property coroutineScope CoroutineScope is the [CoroutineScope] that will be used to fetch data asynchronously.
 */
class BackgroundScanResultReceiver : BroadcastReceiver(), KoinComponent {

    private val apiService: EngageApiService by inject()
    private val pref: EngagePref by inject()
    private val notificationChannelId = "background scan"
    private val notificationChannelName = "background scan"
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("Received background scan results")
        val rule = intent?.getParcelableExtra<Rule>(Constants.BUNDLE_KEY_RULE) ?: return
        val beacon = intent.getParcelableExtra<ProBeacon>(Constants.BUNDLE_KEY_BEACON) ?: return
        if (context == null) return
        Timber.e("processing actions")
        processActions(context, beacon, rule)
    }

    /**
     * Processes actions retrieved from the rule and generates notification for it
     * @param context Context is the Android context
     * @param rule Rule is the triggered rule
     */
    private fun processActions(context: Context, beacon: ProBeacon, rule: Rule) {
        rule.getActionList().forEach { action ->
            showNotification(context, action, beacon, rule.triggerOn.toString())
        }
    }

    /**
     * Shows a notification based on the content on the actions retrieved from the rule
     * @param context Context is the Android Context
     * @param action Action is the action retrieved from the triggered rule
     * @param beacon ProBeacon is the beacon on which the rule is triggered
     * @param event String is the event that is triggered
     */
    private fun showNotification(context: Context, action: Action, beacon: ProBeacon, event: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as? NotificationManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val notification = createNotificationFromAction(context, action, beacon, event)
        Timber.e("Displaying notification for $event event. Action: $action")
        manager.notify(getNotificationId(), notification)
    }

    /**
     * Creates notification instance using action data
     * @param context Context is the Android context
     * @param action Action is the action from which the notification will be created
     * @param beacon ProBeacon is the beacon from which the action is extracted
     * @param event String is the event triggered on the beacon
     * @return Notification that contains info from the action
     */
    private fun createNotificationFromAction(
        context: Context,
        action: Action,
        beacon: ProBeacon,
        event: String
    ): Notification {
        val intent = getActivityIntent(context, pref().pendingIntentClassName).apply {
            putExtra(Constants.BUNDLE_KEY_ACTION, action)
            putExtra(Constants.BUNDLE_KEY_EVENT, event)
        }
        val builder = NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle("ProximiPro Demo")
            .setSmallIcon(R.drawable.ic_notifications)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)

        when (action.type) {
            Action.TYPE_TEXT -> builder.setContentText(action.meta.params.text)
            Action.TYPE_PROMOTION -> {
                builder.setContentText("Tap to see ${action.meta.params.text}")
                builder.setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        getNotificationId(),
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                    )
                )
            }
            Action.TYPE_WEB -> {
                builder.setContentText("Tap for details")
                builder.setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        getNotificationId(),
                        Intent(Intent.ACTION_VIEW, Uri.parse(action.meta.params.url)),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                )
            }
        }
        return builder.build()
    }

    /**
     * Randomly generates a random notification ID
     * @return Int is the randomly generated ID
     */
    private fun getNotificationId() = Random.nextInt()
}