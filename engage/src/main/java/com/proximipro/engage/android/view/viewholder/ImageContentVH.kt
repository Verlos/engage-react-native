package com.proximipro.engage.android.view.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.proximipro.engage.android.R
import com.proximipro.engage.android.model.remote.Content
import com.proximipro.engage.android.model.remote.EngageApi
import kotlinx.android.synthetic.main.item_image.view.ivContentImage
import timber.log.Timber

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * ViewHolder for image type of content in the list
 */
internal class ImageContentVH(itemView: View) : BaseContentVH(itemView) {
    override fun setClickListener(func: (View) -> Unit) = itemView.setOnClickListener(func)

    override fun bindTo(content: Content) {
        Timber.e("Loading image from: ${EngageApi.IMAGE_BASE_URL}${content.image}")
        Glide.with(itemView.context)
            .load("${EngageApi.IMAGE_BASE_URL}${content.image}")
            .placeholder(R.drawable.placeholder)
            .into(itemView.ivContentImage)
    }
}