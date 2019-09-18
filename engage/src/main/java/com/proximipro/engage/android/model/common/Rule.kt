package com.proximipro.engage.android.model.common

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.proximipro.engage.android.model.local.DatabaseInfo
import kotlinx.android.parcel.Parcelize

/**
 * Entity class for Rules table
 * @property id String is the id of the rule
 * @property triggerOn String? specifies on which even the rule is to be triggered
 * @property delay String? is the delay for the given rule
 * @property isActive String? determines whether the rule is active or not
 * @property zone String is the zone id of the given rule
 * @property name String? is the name of the rule
 * @property actions String contains actions of the rule
 * @property clientId String? is the id of the client
 */
@Entity(tableName = DatabaseInfo.RULES_TABLE)
@Parcelize
data class Rule(
    @SerializedName("id")
    @ColumnInfo(name = "rule_id")
    @PrimaryKey
    val id: String = "",
    @SerializedName("trigger_on")
    val triggerOn: String? = "",
    @SerializedName("delay")
    val delay: String? = "",
    @SerializedName("is_active")
    val isActive: String? = "",
    @SerializedName("zone")
    val zone: String = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("actions")
    val actions: String = "",
    @SerializedName("client_id")
    @ColumnInfo(name = "rule_client_id")
    val clientId: String? = ""
) : Parcelable {

    /**
     * parses the rule actions json and creates a list of [Action]
     * @return List<Action> is the parsed [Action] instances
     */
    fun getActionList(): List<Action> {
        return try {
            Gson().fromJson(actions, Array<Action>::class.java).asList()
        } catch (e: JsonParseException) {
            arrayListOf()
        } catch (e: JsonSyntaxException) {
            arrayListOf()
        }
    }
}
