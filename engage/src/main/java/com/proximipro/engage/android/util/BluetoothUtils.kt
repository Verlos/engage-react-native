package com.proximipro.engage.android.util

import android.bluetooth.BluetoothAdapter

/*
 * Created by Birju Vachhani on 08 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Determines whether the device Bluetooth is on/off
 * @return Boolean true if device bluetooth is on, false otherwise
 */
fun isBluetoothOn() = BluetoothAdapter.getDefaultAdapter()?.isEnabled ?: false
