package com.proximipro.engage.android.model.remote

/*
 * Created by Birju Vachhani on 06 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Holds the information about the Engage API like base url, endpoints, keys, etc.
 */
internal object EngageApi {
    const val IMAGE_BASE_URL = "https://engage.proximipro.com"
    const val BASE_URL = "https://engage.proximipro.com/api/"
    const val STATIC_KEY = "bXVe4J_A4F@Vc-YdSZ8¿0EqB.Ku7MYFRL,chy.IvWOyPS@hYpco_xB2?2RH8y-ECADcip"

    /**
     * Holds all the endpoints of the api
     */
    object Paths {
        const val CHECK_API_KEY = "checkclientkey"
        const val REGISTER_USER = "user"
        const val CONTENT_PATH = "list/ibeacon"
        const val LOCATION_BASED_CONTENT_PATH = "list/location"
        const val PUSH_LIST_PATH = "pushlist/ibeacon"
        const val FETCH_RULES_PATH = "v1/rules"
        const val FETCH_ZONE_PATH = "v1/zones"
        const val SEND_ANALYTICS = "v1/logevents"
    }
}