package com.proximipro.engage.android.model.remote.request

import com.google.gson.annotations.SerializedName

/**
 * Request object model for Api verification network call
 * @property apiKey String is the api key for the sdk
 * @property key String is the secret static key of the sdk¬
 */
internal data class ApiKeyVerificationRequest(
    @SerializedName("apikey")
    val apiKey: String,
    @SerializedName("key")
    val key: String
)
