package com.proximipro.engage.android.core

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import org.altbeacon.beacon.BeaconConsumer
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

/*
 * Created by Birju Vachhani on 06 June 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * A [BeaconConsumer] instance that is used to bind beacon related service
 */
object EngageBeaconConsumer : BeaconConsumer, KoinComponent {

    private var connectionCallback: () -> Unit = {}

    override fun getApplicationContext(): Context = get()

    override fun unbindService(connection: ServiceConnection) =
        applicationContext.unbindService(connection)

    override fun bindService(intent: Intent?, connection: ServiceConnection, flags: Int) =
        applicationContext.bindService(intent, connection, flags)

    override fun onBeaconServiceConnect() = connectionCallback()

    /**
     * Allows to set connection callback that will be called when the service is connected
     * @param func () -> Unit will invoked upon successful connection of service
     */
    fun setConnectionCallback(func: () -> Unit) {
        connectionCallback = func
    }
}