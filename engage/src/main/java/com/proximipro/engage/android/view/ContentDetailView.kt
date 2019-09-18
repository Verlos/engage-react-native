package com.proximipro.engage.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.proximipro.engage.android.R
import com.proximipro.engage.android.model.remote.Content
import com.proximipro.engage.android.model.remote.EngageApi
import kotlinx.android.synthetic.main.item_html.view.progressBarHtml
import kotlinx.android.synthetic.main.item_html.view.wvHtml
import kotlinx.android.synthetic.main.item_image.view.ivContentImage
import kotlinx.android.synthetic.main.item_video.view.progressBarVideo
import kotlinx.android.synthetic.main.item_video.view.touchableArea
import kotlinx.android.synthetic.main.item_video.view.wvVideo
import kotlinx.android.synthetic.main.item_web_content.view.progressBarWebContent
import kotlinx.android.synthetic.main.item_web_content.view.wvWebContent

/*
 * Created by Birju Vachhani on 28 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * A custom view to display detailed view of the selected content.
 */
class ContentDetailView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId)

    /**
     * Allows to set new content for this view.
     * @param content Content is the new content that will be set
     */
    fun setContent(content: Content) {
        when (content.type) {
            "image" -> showImage(content)
            "video" -> showVideo(content)
            "webcontent" -> showWebContent(content)
            else -> showHtml(content)
        }
    }

    /**
     * Shows Html type of content
     * @param content Content is the content that will be used to display information
     */
    private fun showHtml(content: Content) {
        removeAllViews()
        val view = View.inflate(context, R.layout.item_html, this)
        view.wvHtml.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    super.onPageFinished(webView, url)
                    view.progressBarHtml.visibility = View.GONE
                }
            }
            settings.javaScriptEnabled = true
            loadData(content.description, "text/html;charset=UTF-8", "UTF-8")
        }
        view.touchableArea.visibility = View.GONE
    }

    /**
     * Shows Web type of content which is generally website link
     * @param content Content is the content that will be used to display information
     */
    private fun showWebContent(content: Content) {
        removeAllViews()
        val view = View.inflate(context, R.layout.item_web_content, this)
        view.wvWebContent.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    super.onPageFinished(webView, url)
                    view.progressBarWebContent.visibility = View.GONE
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(content.url)
        }
        view.touchableArea.visibility = View.GONE
    }

    /**
     * Shows video type of content which is a video link
     * @param content Content is the content that will be used to display information
     */
    private fun showVideo(content: Content) {
        removeAllViews()
        val view = View.inflate(context, R.layout.item_video, this)
        view.wvVideo.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(webView: WebView, url: String) {
                    super.onPageFinished(webView, url)
                    view.progressBarVideo.visibility = View.GONE
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(content.url)
        }
        view.touchableArea.visibility = View.GONE
    }

    /**
     * Shows image type of content.
     *
     * Internally it uses [Glide] to show placeholder and load image
     * @param content Content is the content that will be used to display image
     */
    private fun showImage(content: Content) {
        removeAllViews()
        val view = View.inflate(context, R.layout.item_image, this)
        Glide.with(context)
            .load("${EngageApi.IMAGE_BASE_URL}${content.image}")
            .into(view.ivContentImage)
    }
}