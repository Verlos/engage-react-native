package com.proximipro.engage.android.model.common

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.proximipro.engage.android.model.local.DatabaseInfo
import kotlinx.android.parcel.Parcelize

/**
 * Entity class for Zones table
 * @property id String is the id of the zone
 * @property major String is the major value of the zone
 * @property minor String is the minor value of the zone
 * @property latitude String? is the location latitude
 * @property description String? is the description related to the zone
 * @property uuid String is the unique uuid for the zone
 * @property clientId String? is the id of the client
 * @property longitude String? is the location longitude
 * @property radio String? Don't know hwo this is useful
 */
@Entity(tableName = DatabaseInfo.ZONES_TABLE)
@Parcelize
data class Zone(
    @SerializedName("id")
    @ColumnInfo(name = "zone_id")
    @PrimaryKey
    val id: String = "",
    @SerializedName("major")
    val major: String = "",
    @SerializedName("minor")
    val minor: String = "",
    @SerializedName("latitude")
    val latitude: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("uuid")
    val uuid: String = "",
    @SerializedName("client_id")
    @ColumnInfo(name = "zone_client_id")
    val clientId: String? = "",
    @SerializedName("longitude")
    val longitude: String? = "",
    @SerializedName("radio")
    val radio: String? = ""
) : Parcelable


