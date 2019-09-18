package com.proximipro.engage.android.model.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.proximipro.engage.android.model.remote.Content
import kotlinx.android.parcel.Parcelize

/**
 * Holds action parameters
 * @property params Params is the actual action params
 */
@Parcelize
data class Meta(
    @SerializedName("params")
    val params: Params = Params()
) : Parcelable

/**
 * Holds information related to the rule action
 * @property meta Meta holds parameter information for a particular beacon
 * @property name String? is the name of the action
 * @property type String? is the type of the action [type_text, type_promotion, type_web]
 */
@Parcelize
data class Action(
    @SerializedName("meta")
    val meta: Meta = Meta(),
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("type")
    val type: String? = ""
) : Parcelable {
    companion object {
        const val TYPE_TEXT = "text_alert"
        const val TYPE_PROMOTION = "promotion"
        const val TYPE_WEB = "web_page"
    }

    /**
     * Creates [Content] of type web from the data
     * @return Content which if web type of content
     */
    fun mapToWebContent(): Content {
        return Content(
            title = name.toString(),
            type = Content.TYPE_WEB,
            url = meta.params.url.toString()
        )
    }
}

/**
 * Actual holder of action parameters
 * @property id String is the promotion id
 * @property text String? is the is the text alert string
 * @property url String? is the url for showing web content
 */
@Parcelize
data class Params(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("text")
    val text: String? = "",
    @SerializedName("url")
    val url: String? = ""
) : Parcelable



