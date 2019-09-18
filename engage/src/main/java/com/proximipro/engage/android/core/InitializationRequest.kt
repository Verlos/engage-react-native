package com.proximipro.engage.android.core

/*
 * Created by Birju Vachhani on 20 August 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Holder for values that needs to be passed at the time of the sdk initialization
 * @property apiKey String is the SDK api key
 * @property appName String is the name of the android app
 * @property regionId String is the custom region identifier that will be used for rule triggers
 * @property clientId String is the unique client id provided with sdk
 */
data class InitializationRequest(
    val apiKey: String,
    val appName: String,
    val regionId: String,
    val clientId: String
)