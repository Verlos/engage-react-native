package com.proximipro.engage.android.util

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Represents type of beacon event
 */
internal sealed class BeaconEvent {

    /**
     * Represents Enter event type of Beacon
     */
    object Enter : BeaconEvent()

    /**
     * Represents Exit event type of Beacon
     */
    object Exit : BeaconEvent()

    /**
     * Represents Camping event type of Beacon
     */
    object Camping : BeaconEvent()
}