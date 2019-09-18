package com.proximipro.engage.android.model.remote.response

import com.google.gson.annotations.SerializedName

/*
 * Created by Birju Vachhani on 23 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Response object model for register user network call
 * @property lastAccess String indicates the last time of access
 * @property feature2 String? contains feature string 2
 * @property feature3 String? contains feature string 3
 * @property gender String? indicates gender of the registered user
 * @property apiKey String is the api key of the sdk
 * @property appid String indicates unique id of the registered user
 * @property birthDate String? indicates birth date of the registered user
 * @property feature1 String? contains feature string 1
 * @property createdAt String indicates creation time of the registered user
 * @property id String? indicates user id of the registered user for the server
 */
data class RegisterUserResponse internal constructor(
    @SerializedName("last_access")
    internal val lastAccess: String = "",
    @SerializedName("feature2")
    internal val feature2: String? = "",
    @SerializedName("feature3")
    internal val feature3: String? = "",
    @SerializedName("gender")
    internal val gender: String? = "",
    @SerializedName("api_key")
    val apiKey: String = "",
    @SerializedName("appid")
    val appid: String = "",
    @SerializedName("birth_date")
    internal val birthDate: String? = "",
    @SerializedName("feature1")
    internal val feature1: String? = "",
    @SerializedName("created_at")
    internal val createdAt: String = "",
    @SerializedName("id")
    internal val id: String? = ""
)


