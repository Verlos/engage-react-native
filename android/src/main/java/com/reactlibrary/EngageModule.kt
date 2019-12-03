package com.reactlibrary

import android.app.Application
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.proximipro.engage.android.Engage
import com.proximipro.engage.android.core.BeaconScanResultListener
import com.proximipro.engage.android.core.EngageConfig
import com.proximipro.engage.android.core.InitializationRequest
import com.proximipro.engage.android.locationCheckLiveData
import com.proximipro.engage.android.model.ProBeacon
import com.proximipro.engage.android.model.Tag
import com.proximipro.engage.android.model.common.Action
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.model.remote.Content
import com.proximipro.engage.android.util.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*



class EngageModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val ErrorMessage: String = "Engage SDK initialization missing or entered invalid API Key, please try again"
    private val IS_INITIALIZED = "isInitialized"
    private val IS_USER_REGISTERED = "isUserRegistered"
    private val gson: Gson by lazy { GsonBuilder().setLenient().setPrettyPrinting().create() }
    private var latestBeacon: ProBeacon? = null
    private var latestRule: Rule? = null

    override fun getName(): String {
        return "EngageModule"
    }

    @ReactMethod
    fun sampleMethod(stringArgument: String, numberArgument: Int, callback: Callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: $numberArgument stringArgument: $stringArgument")
    }

    //SDK intialization
    @ReactMethod
    fun initialize(apiKey: String, appName: String, regionId: String, clientId: String, uuid: String, promise: Promise) {
        Engage.isLoggingEnabled = true
        try {
            var initRequest = InitializationRequest(apiKey, appName, regionId, clientId, uuid)
            Engage.initialize(reactContext.applicationContext as Application, request = initRequest, callback = object : InitializationCallback() {
                override fun onError(e: Throwable) {
                    super.onError(e)
                    promise.resolve(false)
                }

                override fun onSuccess() {
                    super.onSuccess()
                    getEngage().config().pendingIntentClassName =  "${reactContext.packageName}.MainActivity"
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
            promise.resolve(getEngage()?.isScanOnGoing())
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }


    @ReactMethod
    fun startScan() {
        Handler(Looper.getMainLooper()).post {
            try {
                Log.e("startScan", "startScan")
                if (getEngage().config().isLocationBasedContentEnabled) {
                    (reactContext.currentActivity as? AppCompatActivity)?.let { owner ->
                        locationCheckLiveData.observe(owner, androidx.lifecycle.Observer {
                            it ?: return@Observer
                            Log.e("onChangeLocation", "location changes")
                            val json = """
                            {"latitude":${it.latitude}, "longitude": ${it.longitude}}
                        """.trimIndent()
                            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                    .emit("onLocationChange", json)
                        })
                    }
                }
                getEngage()?.startScan(currentActivity as AppCompatActivity, object : BeaconScanResultListener() {
                    override fun onBeaconCamped(beacon: ProBeacon) {
                        if (reactContext != null) {
                            latestBeacon = beacon
                            Log.e("BeaconEnter", beacon.toString())
                            val beaconJson = gson.toJson(beacon)
                            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                    .emit("onBeaconEnter", beaconJson)
                        }
                    }

                    override fun onBeaconExit(beacon: ProBeacon) {
                        latestBeacon = beacon
                        Log.e("BeaconExit", beacon.toString())
                        val beaconJson = gson.toJson(beacon)
                        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit("onBeaconExit", beaconJson)
                    }

                    override fun onLocationZoneEntered(rule: Rule, location: Location) {
                        latestRule = rule
                        val beaconInfo = WritableNativeMap()
                        Log.e("BeaconLocation", beaconInfo.toString())
                        val json = """
                            {"latitude":${location.latitude}, "longitude": ${location.longitude}}
                        """.trimIndent()
                        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit("onBeaconLocation", json)
                    }

                    override fun onRuleTriggered(rule: Rule) {
                        //onRuleTriggered.invoke(true)
                    }

                    override fun onScanStopped() {
                        super.onScanStopped()
                        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                .emit("onStopScan", null)
                    }

                })
            } catch (e: Exception) {
                Log.e("Exception", "Test------------")
                e.printStackTrace()
            }
        }

    }

    @ReactMethod
    fun getContentForActions(notificationInfo: String, promise: Promise) {
        runCatching {
            //        getEngage().getContentForActions()
            Log.e("notificationInfo", notificationInfo)
            val action = gson.fromJson<Action>(notificationInfo, Action::class.java)?: return
            getEngage().getContentForActions(listOf(action), success = {
                val jsonArray = gson.toJson(it)
                promise.resolve(jsonArray)
            }){
                promise.reject("Exception", ErrorMessage)
            }
        }
    }

    @ReactMethod
    fun fetchContentBeacon(beaconJson: String, promise: Promise) {
        runCatching {
            val beacon = gson.fromJson(beaconJson, ProBeacon::class.java)
            getEngage().getContentLoader().fetchContent(beacon) onSuccess {
                val jsonArray = gson.toJson(it)
                promise.resolve(jsonArray)
            } onFailure {
                promise.reject("Exception", ErrorMessage)
            }
        }
    }

    @ReactMethod
    fun fetchContentLocation(locationJson: String, promise: Promise) {
        runCatching {
            val location = Location("").apply {
                val obj = JSONObject(locationJson)
                latitude = obj["latitude"].toString().toDouble()
                longitude = obj["longitude"].toString().toDouble()
            }
            getEngage().getContentLoader().fetchContent(location) onSuccess {
                val jsonArray = gson.toJson(it)
                promise.resolve(jsonArray)
            } onFailure {
                promise.reject("Exception", ErrorMessage)
            }
        }
    }

    @ReactMethod
    fun fetchContentNotification(url: String, promise: Promise){
        try {
            getEngage().getContentLoader().fetchContent(url) onSuccess {
                val jsonArray = gson.toJson(it)
                promise.resolve(jsonArray)
            }onFailure {
                promise.reject("Exception", ErrorMessage)
            }
        }catch (e: Exception){
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun logEvent(logType: String, contentId: String, contentType: String, param2: String){
        try {
            var logContentType: LogEventType = LogEventType.Details
            if(logType == "fav"){
                logContentType = LogEventType.Favourites
            }else if(logType == "social"){
                logContentType = LogEventType.Social
            }
            latestBeacon?.let {
                getEngage().logContentTapEvent(logContentType, Content
                (id = contentId, type = contentType), it )
            } ?: latestRule?.let {
                getEngage().logContentTapEvent(LogEventType.Details, Content(id = contentId, type = contentType), it )
            } ?: Log.e("ERROR", "Both beacon and rule are null, can't log tap event")
        }catch (e: Exception){

        }
    }

    @ReactMethod
    fun logNotificationEvent(notificationId: String, action: String){
        try {
            getEngage().logPushOpenEvent(notificationId)
        }catch (e: Exception){

        }
    }



    @ReactMethod
    fun stopScan(promise: Promise) {
        Handler(Looper.getMainLooper()).post {
            try {
                Log.e("stopScan", "stopScan")
                getEngage()?.stopScan()
                promise.resolve(true)
            } catch (e: Exception) {
                promise.reject("Exception", ErrorMessage)
            }
        }
    }

    @ReactMethod
    fun updateApiKey(apiKey: String, promise: Promise) {
        try {
            getEngage()?.updateApiKey(apiKey) {
                promise.resolve(it)
            }
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun setRegionParams(uuid: String, regionIdentifier: String, promise: Promise) {
        try {
            getEngage()?.setRegionParams(uuid, regionIdentifier)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }

    }

    @ReactMethod
    fun updateBeaconUUID(uuidString: String, promise: Promise) {
        try {
            getEngage()?.updateBeaconUUID(uuidString)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @ReactMethod
    fun logout(promise: Promise) {
        Handler(Looper.getMainLooper()).post {
            kotlin.runCatching {
                getEngage()?.logout()
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
            val date = Date(birthDate.toLong() * 1000)
            val genderType = if (gender?.toLowerCase().equals("male")) Gender.Male else Gender.Female
            getEngage()?.registerUser(birthDate = date, gender = genderType)?.onSuccess {
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
    fun updateUser(birthDate: String,
                   gender: String? = null,
                   tagsMap: ReadableArray,
                   promise: Promise) {
        try {
            val tags: List<Tag> = getEngage()?.config().userConfig.getTags()
            val list = tagsMap?.toArrayList().map {
                gson.fromJson(it.toString(), Tag::class.java)
            }
            list.forEach {
                tags.firstOrNull { tag -> tag.name == it.name }?.isSelected = it.isSelected
            }
            val date = Date(birthDate.toLong() * 1000)
            val genderType = if (gender?.toLowerCase().equals("male")) Gender.Male else Gender.Female
            getEngage()?.updateUser(birthDate = date, gender = genderType, tags = list)?.onSuccess {
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
            var en = getEngage()?.config() as EngageConfig
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
            result.putString("snoozeNotificationTimeInMinutes", en.snoozeNotificationTimeInMinutes.toString())
            result.putString("snoozeContentTimeInHours", en.snoozeContentTimeInHours.toString())
            result.putString("pendingIntentClassName", en.pendingIntentClassName)
            val tags: List<Tag> = getEngage()?.config().userConfig.getTags()
            val arrTag = WritableNativeArray()
            for (tag in tags) {
                val productMap = convertJsonToMap(JSONObject(gson.toJson(tag)))
                arrTag.pushMap(productMap)
            }
            result.putArray("tags", arrTag)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    @Throws(JSONException::class)
    private fun convertJsonToMap(jsonObject: JSONObject): WritableMap {
        val map = WritableNativeMap()

        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject.get(key)
            if (value is JSONObject) {
                map.putMap(key, convertJsonToMap(value as JSONObject))
            } else if (value is JSONArray) {
                map.putArray(key, convertJsonToArray(value as JSONArray))
            } else if (value is Boolean) {
                map.putBoolean(key, value as Boolean)
            } else if (value is Int) {
                map.putInt(key, value as Int)
            } else if (value is Double) {
                map.putDouble(key, value as Double)
            } else if (value is String) {
                map.putString(key, value as String)
            } else {
                map.putString(key, value.toString())
            }
        }
        return map
    }

    @Throws(JSONException::class)
    private fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
        val array = WritableNativeArray()

        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            if (value is JSONObject) {
                array.pushMap(convertJsonToMap(value))
            } else if (value is JSONArray) {
                array.pushArray(convertJsonToArray(value))
            } else if (value is Boolean) {
                array.pushBoolean(value)
            } else if (value is Int) {
                array.pushInt(value)
            } else if (value is Double) {
                array.pushDouble(value)
            } else if (value is String) {
                array.pushString(value)
            } else {
                array.pushString(value.toString())
            }
        }
        return array
    }

    @Throws(JSONException::class)
    private fun convertMapToJson(readableMap: ReadableMap): JSONObject {
        val `object` = JSONObject()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            when (readableMap.getType(key)) {
                ReadableType.Null -> `object`.put(key, JSONObject.NULL)
                ReadableType.Boolean -> `object`.put(key, readableMap.getBoolean(key))
                ReadableType.Number -> `object`.put(key, readableMap.getDouble(key))
                ReadableType.String -> `object`.put(key, readableMap.getString(key))
                ReadableType.Map -> `object`.put(key, convertMapToJson(readableMap.getMap(key)!!))
                ReadableType.Array -> `object`.put(key, convertArrayToJson(readableMap.getArray(key)!!))
            }
        }
        return `object`
    }

    @Throws(JSONException::class)
    private fun convertArrayToJson(readableArray: ReadableArray): JSONArray {
        val array = JSONArray()
        for (i in 0 until readableArray.size()) {
            when (readableArray.getType(i)) {
                ReadableType.Null -> {
                }
                ReadableType.Boolean -> array.put(readableArray.getBoolean(i))
                ReadableType.Number -> array.put(readableArray.getDouble(i))
                ReadableType.String -> array.put(readableArray.getString(i))
                ReadableType.Map -> array.put(convertMapToJson(readableArray.getMap(i)!!))
                ReadableType.Array -> array.put(convertArrayToJson(readableArray.getArray(i)!!))
            }
        }
        return array
    }

    @ReactMethod
    fun setBackgroundMode(enable: Boolean) {
        try {
            Log.e("ChangeBackGroundMode", "${enable}")
            Engage.getInstance()?.config()?.isBackgroundModeEnabled = enable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun setNotificationMode(enable: Boolean) {
        try {
            Log.e("ChangeNotificationMode", "${enable}")
            Engage.getInstance()?.config()?.isNotificationEnabled = enable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun setGeoLocationMode(enable: Boolean) {
        try {
            Log.e("ChangeGeoLocationMode", "${enable}")
            Engage.getInstance()?.config()?.isLocationBasedContentEnabled = enable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun setSnoozeContent(snoozeValue: String){
        try {
            Log.e("ChangeSnoozeValue", "${snoozeValue}")
            Engage.getInstance()?.config()?.snoozeContent(snoozeValue.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun setSnoozeNotifications(snoozeValue: String){
        try {
            Log.e("ChangeSnoozeValue", "${snoozeValue}")
            Engage.getInstance()?.config()?.snoozeNotifications(snoozeValue.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun getInitialNotification
            (promise: Promise){
        try {
            Log.e("getInitialNotification", "${notificationData}")
            promise.resolve(Gson().toJson(notificationData).toString())
            notificationData = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @ReactMethod
    fun callPushNotificationRegister(fcmToken: String, promise: Promise){
        try {
            getEngage().registerPushToken(fcmToken)
        } catch (e: Exception) {
            promise.reject("Exception", ErrorMessage)
        }
    }

    override fun getConstants(): Map<String, Any>? {
        val constants = HashMap<String, Any>()
        constants.put(IS_INITIALIZED, Engage.isInitialized)
        constants.put(IS_USER_REGISTERED, Engage.getInstance()?.isUserRegistered == true)
        return constants
    }

    companion object {
        private var notificationData: Action? = null
        @JvmStatic
        fun onNotificationTapped(reactContext: ReactContext?, intent: Intent, isFromNew: Boolean) {
            val data = NotificationData.from(intent)?: return
            getEngage().recordNotificationTapEvent(data.rule)
            if(isFromNew){
                notificationData =  data.action
            }else{
                notificationData = null
            }
            reactContext ?: run {
                Log.e("ERROR", "React context is null, cannot process notifications")
                return
            }
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit("onNotificationClicked", Gson().toJson(data.action).toString())
        }
    }
}
