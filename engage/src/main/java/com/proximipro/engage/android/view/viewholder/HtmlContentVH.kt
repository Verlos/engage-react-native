package com.proximipro.engage.android.view.viewholder

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.proximipro.engage.android.model.remote.Content
import kotlinx.android.synthetic.main.item_html.view.progressBarHtml
import kotlinx.android.synthetic.main.item_html.view.touchableArea
import kotlinx.android.synthetic.main.item_html.view.wvHtml

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * ViewHolder for html type of content in the list
 */
internal class HtmlContentVH(itemView: View) : BaseContentVH(itemView) {

    override fun bindTo(content: Content) {
        itemView.wvHtml.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    itemView.progressBarHtml.visibility = View.GONE
                }
            }
            settings.javaScriptEnabled = true
            loadData(content.description, "text/html;charset=UTF-8", "UTF-8")
        }
    }

    override fun setClickListener(func: (View) -> Unit) = itemView.touchableArea.setOnClickListener(func)
}