package com.proximipro.engage.android.util

/*
 * Created by Birju Vachhani on 23 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * This class provides a way to implement callbacks in kotlin DSL manner. It is similar to Promise in javascript.
 * @param T
 * @property success [@kotlin.ExtensionFunctionType] Function1<T, Unit>
 * @property failure Function1<Throwable, Unit>
 */
class Box<T> internal constructor() {

    internal var success: T.() -> Unit = {}
    internal var failure: (Throwable) -> Unit = {}

    /**
     * Provides a lambda block that will be invoked when the execution is successful
     * @param func T.() -> Unit is the lambda block that will be invoke which will contain output/result of the execution.
     * @return Box<T> is this instance of [Box]
     */
    infix fun onSuccess(func: T.() -> Unit) = this.apply {
        success = func
    }

    /**
     * Provides a lambda block that will be invoked when the execution is unsuccessful
     * @param func T.() -> Unit is the lambda block that will be invoke which will contains errors occurred during the execution
     * @return Box<T> is this instance of [Box]
     */
    infix fun onFailure(func: (Throwable) -> Unit) = this.apply {
        failure = func
    }
}

