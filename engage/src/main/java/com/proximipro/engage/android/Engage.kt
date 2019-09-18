package com.proximipro.engage.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.proximipro.engage.android.analytics.Analytics
import com.proximipro.engage.android.analytics.AnalyticsLogWorker
import com.proximipro.engage.android.analytics.AnalyticsManager
import com.proximipro.engage.android.core.BackgroundScanListener
import com.proximipro.engage.android.core.BeaconScanResultListener
import com.proximipro.engage.android.core.DatabaseSyncWorker
import com.proximipro.engage.android.core.EngageInfo
import com.proximipro.engage.android.core.EngageManager
import com.proximipro.engage.android.core.EngagePref
import com.proximipro.engage.android.core.InitializationRequest
import com.proximipro.engage.android.core.LocationProvider
import com.proximipro.engage.android.core.LocationSettingsActivity
import com.proximipro.engage.android.core.NotificationInfo
import com.proximipro.engage.android.core.isRequestingPermission
import com.proximipro.engage.android.core.observeScanResult
import com.proximipro.engage.android.di.coreModule
import com.proximipro.engage.android.di.databaseModule
import com.proximipro.engage.android.di.networkModule
import com.proximipro.engage.android.di.sdkModule
import com.proximipro.engage.android.exception.InvalidApiKeyException
import com.proximipro.engage.android.model.remote.EngageApi
import com.proximipro.engage.android.model.remote.EngageApiService
import com.proximipro.engage.android.model.remote.request.ApiKeyVerificationRequest
import com.proximipro.engage.android.model.remote.response.ApiKeyVerificationResponse
import com.proximipro.engage.android.model.remote.response.RegisterUserResponse
import com.proximipro.engage.android.util.Box
import com.proximipro.engage.android.util.Constants
import com.proximipro.engage.android.util.Gender
import com.proximipro.engage.android.util.InitializationCallback
import com.proximipro.engage.android.util.hasInternet
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.with
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import timber.log.Timber
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

/*
 * Created by Birju Vachhani on 06 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Entry class for the sdk that manages the whole sdk.
 * @property application Application is the instance of the main application class
 */
class Engage private constructor(private val application: Application) : KoinComponent {

    private val pref: EngagePref by inject()
    private val manager = EngageManager(application)
    private val locationProvider: LocationProvider by inject()
    private var disposable: Disposable? = null
    private val backgroundScanListener: BackgroundScanListener by inject()
    private val analyticsManager: AnalyticsManager by inject()
    private val permissionBroadcastReceiver = PermissionBroadcastReceiver()

    init {
        initApplicationLifecycleObserver()
    }

    companion object : KoinComponent {

        private const val PERIODIC_SYNC_INTERVAL = 24L
        private const val ACCESS_GRANTED = "correct"
        private var mInstance: Engage? = null
        private var job = SupervisorJob()
        private var scope = CoroutineScope(Dispatchers.Main + job)
        private val apiService: EngageApiService by inject()
        private val pref: EngagePref by inject()
        private var _isInitialized = false
        private var isKoinInitialized = false

        /**
         * Determines whether the singleton instance of [Engage] class is initialized or not
         */
        @JvmStatic
        val isInitialized: Boolean
            get() {
                return mInstance != null && _isInitialized && pref().apiKey.isNotBlank()
            }

        /**
         * Verifies whether the sdk has a api key or not
         * @param context Context is the Android context
         * @return Boolean true, if the sdk has the api key, false otherwise
         */
        fun hasApiKey(context: Context) = EngagePref(context).data.apiKey.isNotBlank()

        /**
         * Provides the api key that the sdk uses
         * @param context Context is the Android context
         * @return String is the actual api key that is being used
         */
        fun getApiKey(context: Context) = EngagePref(context).data.apiKey

        /**
         * Initializes the singleton instance of [Engage] class.
         *
         * If the API key has been verified before than it will instantiate the singleton of [Engage].
         * Otherwise verifies that the provided API key is valid and then instantiate the singleton of [Engage].
         *
         * @param application Application is the application instance¬
         * @param request InitializationRequest contains all the params that are needed on the time of initialization
         * @param callback InitializationCallback is the listener on which ¬the methods will be called on initialization events
         */
        @JvmStatic
        @JvmOverloads
        fun initialize(
            application: Application,
            request: InitializationRequest,
            callback: InitializationCallback = object : InitializationCallback() {}
        ) {
            if (!isKoinInitialized) init(application)
            pref.updateSync {
                appName = request.appName
                clientId = request.clientId
                regionId = request.regionId
            }
            if (pref().apiKey.isNotBlank() && pref().apiKey == request.apiKey && !pref().isApiKeyChanged) {
                createInstance(request.apiKey, application)
                Timber.e("SDK initialized")
                callback.onSuccess()
                scheduleSyncWorkers(application)
            } else {
                if (!application.hasInternet()) {
                    callback.onError(Throwable("No Internet connection"))
                    return
                }
                verifyApiKey(request.apiKey, {
                    createInstance(request.apiKey, application)
                    callback.onSuccess()
                    scheduleSyncWorkers(application)
                }, {
                    Timber.e(it)
                    callback.onError(it)
                })
            }
        }

        private fun scheduleSyncWorkers(context: Context) {
            Timber.e("Cancelling all the previous workers")
            WorkManager.getInstance(context).cancelAllWork()
            val databaseSyncRequest =
                PeriodicWorkRequestBuilder<DatabaseSyncWorker>(
                    PERIODIC_SYNC_INTERVAL,
                    TimeUnit.HOURS
                )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInitialDelay(0L, TimeUnit.SECONDS)
                    .build()
            Timber.e("Enqueuing db sync work")
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    DatabaseSyncWorker::class.java.simpleName,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    databaseSyncRequest
                )
            val analyticsSyncRequest =
                PeriodicWorkRequestBuilder<AnalyticsLogWorker>(
                    PERIODIC_SYNC_INTERVAL,
                    TimeUnit.HOURS
                )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInitialDelay(0L, TimeUnit.SECONDS)
                    .build()
            Timber.e("Enqueuing analytics sync work")
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                AnalyticsLogWorker::class.java.simpleName,
                ExistingPeriodicWorkPolicy.REPLACE,
                analyticsSyncRequest
            )
        }

        /**
         * Creates instance of [Engage] and sets necessary flags that indicates that the sdk is initialized
         * @param apiKey String
         * @param application Application
         */
        private fun createInstance(apiKey: String, application: Application) {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = Engage(application)
                }
            }
            pref.updateSync {
                this.apiKey = apiKey
                this.isApiKeyVerified = true
            }
            _isInitialized = true
            pref.update {
                this.isApiKeyChanged = false
            }
            val eventInfo: HashMap<String, String> = hashMapOf(
                Analytics.MAP_KEY_REGION_ID to pref().regionId
            )
            mInstance?.analyticsManager?.logLoginEvent(Gson().toJson(eventInfo))
        }

        /**
         * Verifies that the provided api key is valid or not by making a network call
         *
         * Initializes [Engage] static instance on successful verification of the api key
         * @param apiKey String is the api key for the sdk
         * @param success is the lambda that will be executed on successful verification of the api key
         * @param failure is the lambda that will be executed on failure of api verification
         */
        @SuppressLint("ApplySharedPref")
        private fun verifyApiKey(
            apiKey: String,
            success: (ApiKeyVerificationResponse) -> Unit = {},
            failure: (Throwable) -> Unit = {}
        ) {
            val request = ApiKeyVerificationRequest(apiKey = apiKey, key = EngageApi.STATIC_KEY)
            scope.launch {
                runCatching {
                    apiService.checkClientKeyAsync(request)
                }.fold({ response ->
                    if (response.response == ACCESS_GRANTED) {
                        success(response)
                    } else {
                        failure(InvalidApiKeyException())
                    }
                }, failure)
            }
        }

        /**
         * Initializes all the dependencies for this SDK
         * @param application Application is the instance of Android [Application]
         */
        private fun init(application: Application) {
            stopKoin()
            loadKoinModules(coreModule, sdkModule, networkModule, databaseModule).with(application)
            isKoinInitialized = true
        }

        /**
         * Returns the singleton instance of [Engage] class
         * @return Engage which is the singleton object of Engage sdk
         */
        @JvmStatic
        fun getInstance(): Engage? {
            if (isInitialized) {
                return mInstance
            }
            return null
        }

        /**
         * Removes [Engage] instance and resets sdk preference. This is helpful for the actions like logout.
         */
        private fun destroyEngage() {
            pref.reset()
            mInstance = null
            _isInitialized = false
        }
    }

    /**
     * Sets region params like uuid and region identifier which is used to identify region
     * @param uuid String is the beacon uuid that will be used for beacon scanning
     * @param regionIdentifier String will be used for logging region events
     */
    fun setRegionParams(uuid: String, regionIdentifier: String) {
        runCatching {
            pref.update {
                regionId = regionIdentifier
            }
            pref.update {
                beaconUUID = UUID.fromString(uuid).toString()
            }
        }.onFailure(Timber::e)
    }

    /**
     * This method is supposed to be called when the scan results needs to be observed in foreground
     * If the scan is ongoing,  it only registers the listener
     * If no scan is started, it registers the listener and then starts background scan
     *
     * @param listener BluetoothScanListener is the interface which will be used to pass events
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun startScan(owner: LifecycleOwner, listener: BeaconScanResultListener) {
        observeScanResult(owner, listener)
        if (!manager.isScanOngoing()) {
            startLocationSettingsResolution()
            startBackgroundScan()
        }
    }

    private fun startLocationSettingsResolution() {

        if (!isRequestingPermission.getAndSet(true)) {
            LocalBroadcastManager
                .getInstance(application)
                .registerReceiver(
                    permissionBroadcastReceiver,
                    IntentFilter(application.packageName)
                )
            application.startActivity(
                Intent(
                    application,
                    LocationSettingsActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
        } else {
            Timber.d("A request is already ongoing")
        }
    }

    /**
     * Stops the ongoing beacon scan
     */
    fun stopScan() {
        manager.stopScan()
        disposable?.dispose()
        job.cancel()
        job = SupervisorJob()
        scope = CoroutineScope(Dispatchers.Main + job)
        locationProvider.stopLocationUpdates()
    }

    /**
     * Sets a lifecycle observer on Process.
     *
     * used to get application start and stop events
     */
    private fun initApplicationLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                Timber.e("Application stopped.")
                onApplicationStopped()
            }

        })
    }

    /**
     * Called when the application is stopped or goes in background.
     *
     * This method is invoked by process lifecycle observer when [Lifecycle.Event.ON_PAUSE] is triggered.
     */
    private fun onApplicationStopped() {
        Timber.e("Stopping Engage Service")
        if (pref.data.isBackground && pref.data.isNotificationEnabled) {
            Timber.e("Starting Foreground Service")
            if (!manager.isScanOngoing()) {
                locationProvider.startLocationUpdates(application)
                startBackgroundScan()
            }
        } else {
            stopBackgroundScan()
        }
    }

    /**
     * Starts background scan
     */
    private fun startBackgroundScan() {
        EngageManager(application).startBackgroundScan(application, backgroundScanListener)
    }

    /**
     * Stops background scan process by cancelling scan and unregistering listeners
     */
    private fun stopBackgroundScan() {
        manager.stopScan()
    }

    /**
     * Provides a configuration block in which sdk can be configured
     * @param func EngageInfo.() -> Unit is the lambda block that provides access of [EngageInfo] instance
     */
    @JvmSynthetic
    fun configure(func: EngageInfo.() -> Unit) = pref.update(func)

    /**
     * Provides a configuration block in which notification can be configured
     *
     * This notification configuration will be used to display when app goes in background
     * and background mode is enabled to scan in background. It is notification for foreground service
     * that will be used to perform beacon scan in background
     * @param func EngageInfo.() -> Unit is the lambda block that provides access of  [NotificationInfo] instance¬
     */
    @JvmSynthetic
    fun configureNotification(func: NotificationInfo.() -> Unit) = pref.updateNotificationInfo(func)

    /**
     * Provides configuration object that the sdk uses for internal configuration
     * @return EngageConfig which can be used to configure sdk
     */
    fun config(): EngagePref = pref

    /**
     * Registers new user by making api call to server.
     *
     * @param apiKey String is the API_KEY used for the sdk (Required)
     * @param birthDate Date? is the birthrate of the user (optional)
     * @param gender Gender? is the gender of the user (optional)
     * @param feature1 String is extra feature data (optional)
     * @param feature2 String is extra feature data (optional)
     * @param feature3 String is extra feature data (optional)
     * @return Box<RegisterUserResponse> that contains appId for the registered user.
     */
    @JvmOverloads
    fun registerUser(
        apiKey: String = pref().apiKey,
        birthDate: Date? = null,
        gender: Gender? = null,
        feature1: String? = null,
        feature2: String? = null,
        feature3: String? = null
    ): Box<RegisterUserResponse> {
        val box = Box<RegisterUserResponse>()
        scope.launch {
            runCatching {
                apiService.registerOrUpdateUserAsync(
                    key = apiKey,
                    appId = pref().appId,
                    birthDate = birthDate,
                    gender = if (gender is Gender.Male) "male" else "female",
                    feature1 = feature1,
                    feature2 = feature2,
                    feature3 = feature3
                )
            }.fold({ response ->
                response.appid
                pref.update {
                    appId = response.appid
                }
                box.success(response)
            }, box.failure)
        }
        return box
    }

    /**
     * Determines whether the scan is ongoing or not
     * @return Boolean true if the scan is ongoing, false otherwise
     */
    fun isScanOnGoing(): Boolean = manager.isScanOngoing()

    /**
     * Logs out the user from the sdk and removes the related instances and flags
     */
    fun logout() {
        stopScan()
        destroyEngage()
        stopKoin()
        isKoinInitialized = false
    }

    /**
     * Updates the api key by verifying it before setting it
     * @param key String is the api key that will be used
     */
    fun updateApiKey(key: String) {
        pref.update {
            this.apiKey = key
            this.isApiKeyChanged = true
        }
    }

    /**
     * Receives local broadcasts related to permission model
     */
    inner class PermissionBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            Timber.d("Received Permission broadcast")
            val status = intent?.getStringExtra(Constants.INTENT_EXTRA_PERMISSION_RESULT) ?: return
            isRequestingPermission.set(false)
            when (status) {
                Constants.GRANTED -> {
                    locationProvider.startLocationUpdates(application)
                    Timber.d("Settings granted")
                }
                else -> {
                    locationProvider.startLocationUpdates(application)
                    Timber.d("Settings not granted")
                    Timber.d(status)
                }
            }
            LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(permissionBroadcastReceiver)
        }
    }
}