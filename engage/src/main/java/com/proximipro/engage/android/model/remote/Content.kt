package com.proximipro.engage.android.model.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * POJO class for holding content loaded from the server
 * @property image String is the url of the image when [type] is image
 * @property description String is the custom html when [type] is html
 * @property title String is the title of the content
 * @property type String is the type string of the content
 * @property url String is the web url to load the web page when [type] is webcontent
 */
@Parcelize
data class Content(
    @SerializedName("image")
    val image: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String = ""
) : Parcelable {
    companion object {
        const val TYPE_HTML = "html"
        const val TYPE_IMAGE = "image"
        const val TYPE_WEB = "webcontent"
        const val TYPE_VIDEO = "video"
    }
}


