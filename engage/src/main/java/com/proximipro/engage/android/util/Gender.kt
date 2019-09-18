package com.proximipro.engage.android.util

/*
 * Created by Birju Vachhani on 23 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Provides gender type for the user registration process
 */
sealed class Gender {

    /**
     * Represents Male type of Gender
     */
    object Male : Gender()

    /**
     * Represents Female type of Gender
     */
    object Female : Gender()
}