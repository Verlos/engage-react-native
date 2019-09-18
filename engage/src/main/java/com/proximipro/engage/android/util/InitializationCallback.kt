package com.proximipro.engage.android.util

/*
 * Created by Birju Vachhani on 06 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Provides callback methods for sdk initialization
 */
abstract class InitializationCallback {

    /**
     * Called upon successful initialization of the sdk.
     *
     * This method will be called on main thread when the the api key is verified and an instance of class [Engage]
     * is instantiated
     */
    open fun onSuccess() {}

    /**
     * Called upon unsuccessful initialization of the sdk
     * @param e Exception is the actual cause of the unsuccessful initialization
     */
    open fun onError(e: Throwable) {}
}