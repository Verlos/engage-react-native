package com.proximipro.engage.android.model

import android.os.Parcelable
import com.proximipro.engage.android.model.ProBeacon.Companion.MAX_DISTANCE
import kotlinx.android.parcel.Parcelize
import org.altbeacon.beacon.Beacon

/*
 * Created by Birju Vachhani on 13 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * SDK Beacon Wrapper class that holds all the information related to a beacon
 * @property baseBeacon Beacon is the underlying [Beacon] instance
 * @property ratio Double is a constant which will be used to determine the distance
 * @property MAX_DISTANCE Double is the distance that is allowed, values greater than this will be considered out of range.
 * @property distanceRange ClosedFloatingPointRange<Double> is the range in which a beacon is considered to be in the range.
 */
@Parcelize
class ProBeacon internal constructor(private val baseBeacon: Beacon) : Comparable<ProBeacon>, Parcelable {

    companion object {
        private const val DISTANCE_ADDITION_CONSTANT = 0.54992
        private const val DISTANCE_MULTIPLICATION_CONSTANT = 0.42093
        private const val DISTANCE_POWER_CONSTANT = 6.9476
        private const val RATION_COMPARE_CONSTANT = 1.0
        private const val RATION_POWER_EXPONENT = 10.0

        // Max distance that is used to determine whether the beacon is out of range or not
        private const val MAX_DISTANCE = 10.0
    }

    // Used as constant to determine beacon distance
    private val ratio: Double = (baseBeacon.rssi * 1.0) / baseBeacon.txPower

    // Max distance that is used to determine whether the beacon is in range or not
    private val distanceRange = 0.0..2.0

    val uuid: String = baseBeacon.id1.toString()
    val major: Int = baseBeacon.id2.toString().toIntOrNull() ?: -1
    val minor: Int = baseBeacon.id3.toString().toIntOrNull() ?: -1
    val rssi: Int = baseBeacon.rssi
    val txPower: Int = baseBeacon.txPower
    val bluetoothAddress: String = baseBeacon.bluetoothAddress

    /**
     * Calculates the distance for the beacon using [ratio], [rssi] and [txPower]
     * @return Double which is the distance of the beacon from the device
     */
    val distance = when {
        baseBeacon.rssi == 0 -> Double.MAX_VALUE
        ratio < RATION_COMPARE_CONSTANT -> Math.pow(ratio, RATION_POWER_EXPONENT)
        else -> ((DISTANCE_MULTIPLICATION_CONSTANT) * Math.pow(
            ratio,
            DISTANCE_POWER_CONSTANT
        )) + DISTANCE_ADDITION_CONSTANT
    }

    /**
     * Compares two beacon by their distance to determine which beacon is close
     * @param other ProBeacon is the other object that will be compared to this object based on the [distance]
     * @return Int 0    -> both are same
     *             1    -> this is greater then the other
     *            -1    -> other is greater than this
     */
    override fun compareTo(other: ProBeacon): Int {
        return when {
            distance == other.distance -> 0
            distance > other.distance -> 1
            else -> -1
        }
    }

    /**
     * Checks whether the two beacons are same or not by comparing bluetooth address
     * @param other Any? is the object that will be compared to this based on the [bluetoothAddress]
     * @return Boolean true if both are same,false otherwise
     */
    override fun equals(other: Any?): Boolean = if (other is ProBeacon) {
        baseBeacon.bluetoothAddress.toString() == other.baseBeacon.bluetoothAddress
    } else false

    /*
    * Converts the beacon object into a String
    * */
    override fun toString(): String {
        return "bluetoothAddress: $bluetoothAddress\tRSSI: $rssi\tDistance: $distance\ttxPower: $txPower\t uuid:$uuid\tmajor:$major\tminor:$minor"
    }

    /**
     * Provides access to the instance of [Beacon] which is [baseBeacon]
     * @return Beacon instance of [Beacon]
     */
    operator fun invoke(): Beacon = baseBeacon

    /**
     * Determines whether the beacon is in the allowed range or not.
     *
     * [distanceRange] defines the allowed range
     * @return Boolean true if the beacon is in range, false otherwise
     */
    internal fun isInRange(): Boolean = distance in distanceRange

    /**
     * Determines whether the beacon is reachable or not.
     *
     * [MAX_DISTANCE] defines max reachable distance
     * @return Boolean true if the beacon is not reachable, false otherwise
     */
    internal fun isNotReachable(): Boolean = distance > MAX_DISTANCE

    override fun hashCode(): Int {
        var result = baseBeacon.hashCode()
        result = 31 * result + ratio.hashCode()
        result = 31 * result + MAX_DISTANCE.hashCode()
        result = 31 * result + distanceRange.hashCode()
        return result
    }

    internal fun getBeaconKey(): String = "$uuid:$major:$minor"
}