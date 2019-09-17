package com.reactlibrary

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
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

    private val engageInstance : Engage? by lazy{
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
    fun initialize(apiKey: String, appName: String, regionId: String, clientId: String, uuid: String, callback: Callback) {
        try {
            var initRequest = InitializationRequest(apiKey, appName, regionId, clientId, uuid)
            Engage.initialize(reactContext.applicationContext as Application, request = initRequest, callback = object : InitializationCallback() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    callback.invoke(false)
                }

                override fun onSuccess() {
                    super.onSuccess()
                    callback.invoke(true)
                }
            })
        }catch (e: Exception){

        }
        Engage.getInstance()?.config()

    }

    @ReactMethod
    fun isInitialized(callback: Callback){
        try {
            callback.invoke(Engage.isInitialized)
        }catch (e: Exception){
            callback.invoke(false)
        }

    }

    @ReactMethod
    fun isScanOnGoing(callback: Callback){
        try {
            callback.invoke(engageInstance?.isScanOnGoing())
        }catch (e: Exception){
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun startScan(onBeaconCamped: Callback, onBeaconExit: Callback, onRuleTriggered: Callback) {
        try {
            engageInstance?.startScan(currentActivity as AppCompatActivity, object : BeaconScanResultListener() {
                override fun onBeaconCamped(beacon: ProBeacon) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onBeaconCamped.invoke(true)
                }

                override fun onBeaconExit(beacon: ProBeacon) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onBeaconExit.invoke(true)
                }

                override fun onRuleTriggered(rule: Rule) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onRuleTriggered.invoke(true)
                }
            })
        } catch (e: Exception) {

        }
    }

    @ReactMethod
    fun stopScan(callback: Callback) {
        try {
            engageInstance?.stopScan()
            callback.invoke(true)
        } catch (e: Exception) {
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun updateApiKey(apiKey: String, callback: Callback) {
        try {
            engageInstance?.updateApiKey(apiKey){
                callback.invoke(it)
            }
        } catch (e: Exception) {
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun setRegionParams(uuid: String, regionIdentifier: String,callback: Callback) {
        try {
            engageInstance?.setRegionParams(uuid, regionIdentifier)
            callback.invoke(true)
        } catch (e: Exception) {
            callback.invoke(false)
            e.printStackTrace()
        }

    }

    @ReactMethod
    fun updateBeaconUUID(uuidString: String, callback: Callback) {
        try {
            engageInstance?.updateBeaconUUID(uuidString)
            callback.invoke(true)
        } catch (e: Exception) {
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun logout(callback: Callback) {
        try {
            engageInstance?.logout()
            callback.invoke(true)
        } catch (e: Exception) {
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun registerUser(apiKey: String,
                     birthDate: String,
                     gender: String? = null,
                     callback: Callback) {
        val date = Date(birthDate.toLong())
        val genderType = if (gender?.toLowerCase().equals("male")) Gender.Male else Gender.Female
        engageInstance?.registerUser(apiKey, date, genderType)?.onSuccess {
            callback.invoke(it)
        }?.onFailure {
            callback.invoke(it)
        }

    }

    @ReactMethod
    fun config(callback: Callback) {
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
            callback.invoke(result)
        } catch (e: Exception) {

        }
    }
}
