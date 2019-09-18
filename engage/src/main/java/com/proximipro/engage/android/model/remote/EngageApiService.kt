package com.proximipro.engage.android.model.remote

import com.proximipro.engage.android.analytics.model.remote.AnalyticLogRequest
import com.proximipro.engage.android.model.common.Rule
import com.proximipro.engage.android.model.common.Zone
import com.proximipro.engage.android.model.remote.request.ApiKeyVerificationRequest
import com.proximipro.engage.android.model.remote.response.ApiKeyVerificationResponse
import com.proximipro.engage.android.model.remote.response.RegisterUserResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.Date

/*
 * Created by Birju Vachhani on 06 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Retrofit interface for Engage api
 */
internal interface EngageApiService {

    /**
     * Makes an api call to verify whether the provided api key is valid or not. Returns success in case of
     * valid api key.
     * @param request ApiKeyVerificationRequest is the request that contains parameters to be passed in the api call
     * @return ApiKeyVerificationResponse which will contain api response of type [ApiKeyVerificationResponse]
     */
    @POST(EngageApi.Paths.CHECK_API_KEY)
    suspend fun checkClientKeyAsync(@Body request: ApiKeyVerificationRequest): ApiKeyVerificationResponse

    /**
     * Makes an api call the register a new user or update existing one with provided information.
     * @param key String is the api key used to initialize the sdk (required)
     * @param appId String is the unique user id (required)
     * @param birthDate Date? is the birth date of the user (optional)
     * @param gender String is the gender of the user (Optional)
     * @param feature1 String is the extra feature string (optional)
     * @param feature2 String is the extra feature string (optional)
     * @param feature3 String is the extra feature string (optional)
     * @return RegisterUserResponse which will be containing api response of type [RegisterUserResponse]
     */
    @GET(EngageApi.Paths.REGISTER_USER)
    suspend fun registerOrUpdateUserAsync(
        @Query("key") key: String,
        @Query("appid") appId: String,
        @Query("birth_date") birthDate: Date? = null,
        @Query("gender") gender: String = "",
        @Query("feature1") feature1: String? = null,
        @Query("feature2") feature2: String? = null,
        @Query("feature3") feature3: String? = null
    ): RegisterUserResponse

    /**
     * Retrieves content related to the given beacon
     * @param params contains all the required query parameters for this get request
     * @return List<Content> which will be containing the response of the api call which is list of [Content]
     */
    @GET("list/{path}")
    suspend fun getContentAsync(@Path("path") path: String, @QueryMap params: Map<String, String>): List<Content>

    /**
     * indicates server to send a push notification related to the given beacon
     * @param params Map<String,String> contains all the query parameters required by this api call
     * @return List<Content> which will be containing the response of the api call which is list of [Content]
     */
    @GET("pushlist/{path}")
    suspend fun sendPushAsync(@Path("path") path: String, @QueryMap params: Map<String, String>): ResponseBody

    /**
     * Fetches rules from the server matching the provides api key
     * @param apiKey String is the api key for the sdk
     * @return List<Rule> is list of fetched rules from the server
     */
    @GET(EngageApi.Paths.FETCH_RULES_PATH)
    suspend fun fetchRules(@Query("key") apiKey: String): List<Rule>

    /**
     * Fetches zones from the server matching the provided api key
     * @param apiKey String is the api key for the sdk
     * @return List<Zone> is list of fetched zones from the server
     */
    @GET(EngageApi.Paths.FETCH_ZONE_PATH)
    suspend fun fetchZones(@Query("key") apiKey: String): List<Zone>

    /**
     * Sends analytics data to the server
     * @param request AnalyticLogRequest contains all the data that needs to be sent to the server
     * @return String that determines the status of the api call
     */
    @POST(EngageApi.Paths.SEND_ANALYTICS)
    suspend fun sendAnalyticsAsync(@Body request: AnalyticLogRequest): String
}