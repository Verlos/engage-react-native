package com.proximipro.engage.android.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.proximipro.engage.android.R
import com.proximipro.engage.android.util.createServiceNotification
import com.proximipro.engage.android.util.getActivityIntent
import com.proximipro.engage.android.util.hasPermission
import com.proximipro.engage.android.util.isBluetoothOn
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import org.altbeacon.bluetooth.BluetoothMedic
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 13 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Manages beacon scanning and event listeners
 *
 * Responsible for starting, stopping scanning process and setting event listeners.
 * @property beaconManager BeaconManager is the injected instance of [BeaconManager] which will be used to manage beacon scanning process
 * @property adapter BluetoothAdapter? is the instance of [BluetoothAdapter] which will be null when the device doesn't have bluetooth functionality
 * @property pref EngagePref is the injected instance of [EngagePref] which contains all the configurable information for the sdk
 * @property isScanning Boolean true is the scanning process is ongoing, false otherwise
 * @property beaconScanController BeaconScanController is the injected instance of [BeaconScanController] which is responsible for processing scan results and determining beacon events
 * @property beaconRegion Region is the region which will be used to start scanning process
 * @property beaconConsumer BeaconConsumer is the [BeaconConsumer] which is responsible for underlying beacon scan
 * @property rangeNotifier RangeNotifier is the instance [RangeNotifier] interface that will receive scan results from the underlying framework
 */
class EngageManager(applicationContext: Context) : KoinComponent {

    companion object {
        private const val PERMISSION_NOTIFICATION_REQUEST_CODE = 124
        private const val PERMISSION_NOTIFICATION_ID = 875
        private const val ACTIVE_SCAN_PERIOD = 8000L
        private const val PASSIVE_SCAN_PERIOD = 60000L
        private const val EVENT_PROPAGATION_THRESHOLD = 5000
    }

    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(applicationContext)
    private val adapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val pref: EngagePref by inject()
    private var isScanning = false
    private val beaconScanController: BeaconScanController by inject()
    private val beaconRegion: Region by inject()

    private val beaconConsumer = EngageBeaconConsumer
    private var previousCallTime: Long = -1

    private val rangeNotifier = RangeNotifier { beacons, _ ->
        if (beacons?.isEmpty() == true) return@RangeNotifier
        if (previousCallTime != -1L) {
            if ((System.currentTimeMillis() - previousCallTime) <= EVENT_PROPAGATION_THRESHOLD) {
                return@RangeNotifier
            }
        }
        previousCallTime = System.currentTimeMillis()
        beacons?.let {

            Timber.e("BIRJU Received scan result[${it.size}]: $it")
            beaconScanController.processList(it)
        }
    }

    /**
     * Starts beacon scan process
     * @param listener BluetoothScanListener on which scanning event will be triggered
     */
    fun startBackgroundScan(context: Context, listener: BeaconScanResultListener) {
        if (!context.hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showNoPermissionNotification(context)
            return
        }
        if (isScanOngoing()) {
            return
        }
        observeScanResult(listener)
        setBackgroundScanMode(context)
        val medic = BluetoothMedic.getInstance()

        medic.enablePowerCycleOnFailures(context)
        medic.enablePeriodicTests(context, BluetoothMedic.SCAN_TEST or BluetoothMedic.TRANSMIT_TEST)
        when {
            isBluetoothOn() -> {
                Timber.e("Bluetooth is already Enabled")
                startRangingBeacons()
            }
            turnBluetoothOn() == true -> startRangingBeacons()
            else -> Timber.e("Unable to enable bluetooth")
        }
    }

    /**
     * Shows no permission notification to the user
     * @param context Context is Android context
     */
    private fun showNoPermissionNotification(context: Context) {
        val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                pref().serviceNotificationInfo.channelId,
                pref().serviceNotificationInfo.channelName,
                pref().serviceNotificationInfo.priority
            )
            manager.createNotificationChannel(channel)
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                PERMISSION_NOTIFICATION_REQUEST_CODE,
                getActivityIntent(context, pref().pendingIntentClassName),
                PendingIntent.FLAG_ONE_SHOT
            )
        val notification = NotificationCompat.Builder(context, pref().serviceNotificationInfo.channelId)
            .setContentTitle("No Location Permission")
            .setContentText("Location permission is required to start background scan. Please allow location permission")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_notifications)
            .build()
        manager.notify(PERMISSION_NOTIFICATION_ID, notification)
    }

    /**
     * Enables background scan mode to start scanning in background
     * @param context Context
     */
    private fun setBackgroundScanMode(context: Context) {
        beaconManager.backgroundMode = true
        beaconManager.enableForegroundServiceScanning(
            context.createServiceNotification(pref()),
            pref().serviceNotificationInfo.id
        )
        beaconManager.setEnableScheduledScanJobs(false)
    }

    /**
     * Turns the device bluetooth on
     * @return Boolean? true if the device bluetooth is turned on successfully, false otherwise.
     */
    private fun turnBluetoothOn() = adapter?.enable()

    /**
     * Initiates beacon scan on the underlying framework
     */
    private fun startRangingBeacons() {
        Timber.e("Scan is not ongoing")
        beaconScanController.setScanModeListener { newMode ->
            when (newMode) {
                is ScanMode.Active -> {
                    Timber.e("Switching to Active scan mode")
                    beaconManager.backgroundScanPeriod = ACTIVE_SCAN_PERIOD
                    beaconManager.updateScanPeriods()
                }
                is ScanMode.Passive -> {
                    Timber.e("Switching to Passive scan mode, scan cycles will be longer than usual")
                    beaconManager.backgroundScanPeriod = PASSIVE_SCAN_PERIOD
                    beaconManager.updateScanPeriods()
                }
            }
        }
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(pref().beaconLayout))
        beaconManager.removeAllRangeNotifiers()
        beaconManager.removeRangeNotifier(rangeNotifier)
        beaconManager.addRangeNotifier(rangeNotifier)
        beaconConsumer.setConnectionCallback {
            beaconManager.startRangingBeaconsInRegion(beaconRegion)
        }
        beaconManager.bind(beaconConsumer)
        isScanning = true
        Timber.e("Ranging beacons started")
        beaconManager.backgroundScanPeriod = ACTIVE_SCAN_PERIOD
        beaconManager.backgroundBetweenScanPeriod = 0
    }

    /**
     * Stops the ongoing beacon scan process
     */
    fun stopScan() {
        if (isScanning || beaconManager.isBound(beaconConsumer)) {
            Timber.e("Stopping scan")
            beaconManager.stopRangingBeaconsInRegion(beaconRegion)
            beaconManager.removeRangeNotifier(rangeNotifier)
            beaconManager.unbind(beaconConsumer)
            beaconScanController.reset()
            beaconScanController.stopScan()
            eventResultLiveData = MutableLiveData()
            isScanning = false
        }
    }

    /**
     * Determines whether the scan is on going or not
     * @return Boolean true if the scan is ongoing, false otherwise
     */
    internal fun isScanOngoing(): Boolean = isScanning || beaconManager.isBound(beaconConsumer)
}

/**
 * Represents the scan modes for beacon scanning process
 */
sealed class ScanMode {
    /**
     * Represents Active scan mode which means we entered a beacon region
     */
    object Active : ScanMode()

    /**
     * Represents Passive scan mode which means we exited a beacon region
     */
    object Passive : ScanMode()
}