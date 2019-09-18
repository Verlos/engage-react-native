package com.proximipro.engage.android.util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Created by Birju Vachhani on 23 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Executes deferred on [Dispatchers.IO]
 * @receiver Deferred<T> is the deferred that will be executed
 * @return T result of the execution
 */
suspend fun <T> Deferred<T>.awaitOnIO(): T = withContext(Dispatchers.IO) {
    await()
}