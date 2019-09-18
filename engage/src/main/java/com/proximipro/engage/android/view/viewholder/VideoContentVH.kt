package com.proximipro.engage.android.view.viewholder

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.proximipro.engage.android.model.remote.Content
import kotlinx.android.synthetic.main.item_video.view.progressBarVideo
import kotlinx.android.synthetic.main.item_video.view.touchableArea
import kotlinx.android.synthetic.main.item_video.view.wvVideo

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * ViewHolder for video type of content in the list
 */
internal class VideoContentVH(itemView: View) : BaseContentVH(itemView) {

    override fun bindTo(content: Content) {
        itemView.wvVideo.run {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    itemView.progressBarVideo.visibility = View.GONE
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(content.url)
        }
    }

    override fun setClickListener(func: (View) -> Unit) = itemView.touchableArea.setOnClickListener(func)
}