package com.proximipro.engage.android.model.remote.response

import com.google.gson.annotations.SerializedName

/*
 * Created by Birju Vachhani on 05 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Response object model for Api verification network call
 * @property access Boolean true if access is granted, false otherwise
 * @property response String - There's no documentation of what does this contain
 * @property plan Any? - No idea what does this mean
 */
internal data class ApiKeyVerificationResponse(
    @SerializedName("access")
    val access: Boolean = false,
    @SerializedName("response")
    val response: String = "",
    @SerializedName("plan")
    val plan: Any? = null
)


