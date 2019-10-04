package com.reactlibrary

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.proximipro.engage.android.Engage
import com.proximipro.engage.android.core.BeaconScanResultListener
import com.proximipro.engage.android.core.EngageConfig
import com.proximipro.engage.android.core.InitializationRequest
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.util.Gender
import com.proximipro.engage.android.util.InitializationCallback
import java.util.*

class EngageModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val ErrorMessage: String = "Engage SDK initialization missing or entered invalid API Key, please try again"
    private val IS_INITIALIZED = "isInitialized"
    private val IS_USER_REGISTERED = "isUserRegistered"
    private val engageInstance: Engage? by lazy {
        Engage.getInstance()
    }

    override fun getName(): String {
        return "Engage"
    }

    @ReactMethod
    fun sampleMethod(stringArgument: String, numberArgument: Int, callback: Callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: $numberArgument stringArgument: $stringArgument")
    }

    //SDK intialization
    @ReactMethod
    fun initialize(apiKey: String, appName: String, regionId: String, clientId: String, uuid: String, promise: Promise) {
        try {
            var initRequest = InitializationRequest(apiKey, appName, regionId, clientId, uuid)
            Engage.initialize(reactContext.applicationContext as Application, request = initRequest, callback = object : InitializationCallback() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    promise.resolve(false)
                }

                override fun onSuccess() {
                    super.onSuccess()
                    promise.resolve(true)
                }
            })
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun isScanOnGoing(promise: Promise) {
        try {
            promise.resolve(engageInstance?.isScanOnGoing())
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }


    @ReactMethod
    fun startScan() {
        Log.e("startScan","startScan")
        Handler(Looper.getMainLooper()).post {
            try {
                engageInstance?.startScan(currentActivity as AppCompatActivity, object : BeaconScanResultListener() {
                    override fun onBeaconCamped(beacon: ProBeacon) {
                        if (reactContext != null) {
                            Log.e("BeaconEnter", beacon.toString())
                            val beaconInfo = WritableNativeMap()
                            beaconInfo.putString("uuid", beacon.uuid)
                            beaconInfo.putInt("major", beacon.major)
                            beaconInfo.putInt("minor", beacon.minor)
                            beaconInfo.putInt("rssi", beacon.rssi)
                            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                    .emit("onBeaconEnter", beaconInfo)
                        }
                    }

                    override fun onBeaconExit(beacon: ProBeacon) {
                        // onBeaconExit.invoke(true)
                        Log.e("BeaconExit", beacon.toString())
                        val beaconInfo = WritableNativeMap()
                        beaconInfo.putString("uuid", beacon.uuid)
                        beaconInfo.putInt("major", beacon.major)
                        beaconInfo.putInt("minor", beacon.minor)
                        beaconInfo.putInt("rssi", beacon.rssi)
                        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit("onBeaconExit", beaconInfo)
                    }

                    override fun onRuleTriggered(rule: Rule) {
                        //onRuleTriggered.invoke(true)
                    }
                })
            } catch (e: Exception) {
                Log.e("Exception", "Test------------")
                e.printStackTrace()
            }
        }

    }

    @ReactMethod
    fun stopScan(promise: Promise) {
        try {
            engageInstance?.stopScan()
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun updateApiKey(apiKey: String, promise: Promise) {
        try {
            engageInstance?.updateApiKey(apiKey) {
                promise.resolve(it)
            }
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun setRegionParams(uuid: String, regionIdentifier: String, promise: Promise) {
        try {
            engageInstance?.setRegionParams(uuid, regionIdentifier)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }

    }

    @ReactMethod
    fun updateBeaconUUID(uuidString: String, promise: Promise) {
        try {
            engageInstance?.updateBeaconUUID(uuidString)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun logout(promise: Promise) {

        Handler(Looper.getMainLooper()).post {
            kotlin.runCatching {
                engageInstance?.logout()
                Log.e("Logout", "Logout success")
                promise.resolve(true)
            }.onFailure {
                Log.e("Logout", "${it.message}")
                promise.reject("Exception", ErrorMessage)
            }
        }
    }

    @ReactMethod
    fun registerUser(birthDate: String,
                     gender: String? = null,
                     promise: Promise) {
        try {
            val date = Date(birthDate.toLong())
            val genderType = if (gender?.toLowerCase().equals("male")) Gender.Male else Gender.Female
            engageInstance?.registerUser(birthDate = date, gender = genderType)?.onSuccess {
                val result = Arguments.createMap()
                result.putString("apiKey", it.apiKey)
                result.putString("appid", it.appid)
                promise.resolve(result)
            }?.onFailure {
                promise.reject("Exception", it.message)
            }
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun config(promise: Promise) {
        try {
            var en = engageInstance?.config() as EngageConfig
            val result = Arguments.createMap()
            result.putString("apiKey", en.apiKey)
            result.putString("appName", en.appName)
            result.putString("beaconUUID", en.beaconUUID)
            result.putString("clientId", en.clientId)
            result.putString("regionId", en.regionId)
            result.putBoolean("isBackgroundModeEnabled", en.isBackgroundModeEnabled)
            result.putBoolean("isLocationBasedContentEnabled", en.isLocationBasedContentEnabled)
            result.putBoolean("isNotificationEnabled", en.isNotificationEnabled)
            result.putBoolean("isUserRegistered", en.isUserRegistered)
            result.putString("pendingIntentClassName", en.pendingIntentClassName)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun setBackgroundMode(enable: Boolean){
        try {
            Log.e("ChangeBackGroundMode", "${enable}")
            Engage.getInstance()?.config()?.isBackgroundModeEnabled = enable
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun setNotificationMode(enable: Boolean){
        try {
            Log.e("ChangeNotificationMode", "${enable}")
            Engage.getInstance()?.config()?.isNotificationEnabled = enable
        }catch(e: Exception){
            e.printStackTrace()
        }
    }
    
    override fun getConstants(): Map<String, Any>? {
        val constants = HashMap<String, Any>()
        constants.put(IS_INITIALIZED, Engage.isInitialized)
        constants.put(IS_USER_REGISTERED, engageInstance?.isUserRegistered == true)
        return constants
    }
}
