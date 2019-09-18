package com.proximipro.engage.android.receiver

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.proximipro.engage.android.util.Constants
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

/*
 * Created by Birju Vachhani on 08 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Broadcast Receiver to receive Bluetooth on/off events
 *
 * Triggered when Bluetooth state is changed and sets the changed value to an [statusLiveData]
 * which is an injected object that can be used to receive Bluetooth state related updates
 * @property statusLiveData MutableLiveData<Boolean> is the injected [MutableLiveData] instance which will contain current device bluetooth status.
 */
internal class BluetoothStateBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    internal val statusLiveData: MutableLiveData<Boolean> by inject(Constants.BLUETOOTH_STATE_LIVEDATA)

    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        intent?.action?.let { action ->
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Timber.d("Bluetooth is turned off")
                        statusLiveData.postValue(false)
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Timber.d("Bluetooth is turned on")
                        statusLiveData.postValue(true)
                    }
                }
            }
        }
    }
}