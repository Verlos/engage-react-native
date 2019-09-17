package com.reactlibrary

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
import com.proximipro.engage.android.Engage
import com.proximipro.engage.android.core.BeaconScanResultListener
import com.proximipro.engage.android.core.EngagePref
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
    fun initialize(apiKey: String, appName: String, regionId: String, clientId: String, promise: Promise) {
        try {
            var initRequest = InitializationRequest(apiKey, appName, regionId, clientId)
            Engage.initialize(application = reactContext.applicationContext as Application, request = initRequest, callback = object : InitializationCallback() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    promise.resolve(false)
                }

                override fun onSuccess() {
                    super.onSuccess()
                    promise.resolve(true)
                }
            })
        }catch (e: Exception){
            promise.reject(e)
        }

    }

    @ReactMethod
    fun isInitialized(promise: Promise){
        try {
            promise.resolve(Engage.isInitialized);
        }catch (e: Exception){
            promise.reject(e)
        }

    }

    @ReactMethod
    fun isScanOnGoing(promise: Promise){
        try {
            promise.resolve(engageInstance?.isScanOnGoing())
        }catch (e: Exception){
            promise.reject(e)
        }
    }

    @ReactMethod
    fun startScan(onBeaconCamped: Promise, onBeaconExit: Promise, onRuleTriggered: Promise) {
        try {
            engageInstance?.startScan(currentActivity as AppCompatActivity, object : BeaconScanResultListener() {
                override fun onBeaconCamped(beacon: ProBeacon) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onBeaconCamped.resolve(true)
                }

                override fun onBeaconExit(beacon: ProBeacon) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onBeaconExit.resolve(true)
                }

                override fun onRuleTriggered(rule: Rule) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    onRuleTriggered.resolve(true)
                }
            })
        } catch (e: Exception) {
            onBeaconCamped.reject(e)

        }
    }

    @ReactMethod
    fun stopScan(promise: Promise) {
        try {
            engageInstance?.stopScan()
            promise.resolve(true)
        } catch (e: Exception) {
            promise.resolve(e)
        }
    }

    @ReactMethod
    fun updateApiKey(apiKey: String, promise: Promise) {
        try {
            engageInstance?.updateApiKey(apiKey)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun setRegionParams(uuid: String, regionIdentifier: String) {
        try {
            engageInstance?.setRegionParams(uuid, regionIdentifier)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @ReactMethod
    fun updateBeaconUUID(uuidString: String, promise: Promise) {
        try {
//            engageInstance?.updateBeaconUUID(uuidString)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun logout(promise: Promise) {
        try {
            engageInstance?.logout()
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun registerUser(apiKey: String,
                     birthDate: String,
                     gender: String? = null,
                     feature1: String? = null,
                     feature2: String? = null,
                     feature3: String? = null,
                     callback: Callback) {
        val date = Date(birthDate.toLong())
        val genderType = if (gender?.toLowerCase().equals("male")) Gender.Male else Gender.Female
        engageInstance?.registerUser(apiKey, date, genderType, feature1, feature2, feature3)?.onSuccess {
            callback.invoke(true)
        }?.onFailure {
            callback.invoke(false)
        }
    }

    @ReactMethod
    fun config(promise: Promise) {
        try {
            var en = engageInstance?.config() as EngagePref
            val result = Arguments.createMap()
            result.putString("apiKey", en.getApiKey())
            result.putString("beaconUUID", en.getBeaconUuid())
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }
}
