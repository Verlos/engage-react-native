package com.proximipro.engage.android.exception

/*
 * Created by Birju Vachhani on 07 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Base Exception class for handling library specific custom errors
 * @param message is the message that describes the actual error and what caused it
 * @constructor
 */
open class EngageException(message: String) : Throwable(message)

/**
 * This exception is passed to the initialization callback methods when the provided api key is not valid
 */
class InvalidApiKeyException : EngageException("Provided API key is not valid")

/**
 * This exception is thrown when the sdk is not initialized and user tries to access the singleton of [Engage] class
 */
class SdkNotInitializedException :
    EngageException("Engage SDK is not initialized yet. Make sure that the sdk is initialized before using it.")

/**
 * This exception is thrown when the location permission hasn't been granted
 */
class NoLocationPermissionException :
    EngageException("Cannot access device location. Location permission is not granted.")

