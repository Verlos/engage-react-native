package com.proximipro.engage.android.view.viewholder

import android.view.View
import com.proximipro.engage.android.R
import com.proximipro.engage.android.model.remote.Content
import kotlinx.android.synthetic.main.item_empty.view.tvEmptyMessage

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * ViewHolder for empty view which will be displayed when the list is empty
 */
internal class EmptyVH(itemView: View) : BaseContentVH(itemView) {

    override fun bindTo(content: Content) {}

    override fun setClickListener(func: (View) -> Unit) {}

    /**
     * Sets the empty view message
     * @param msg String is the message that will be set and displayed
     */
    fun setMessage(msg: String) {
        if (msg.isNotBlank()) {
            itemView.tvEmptyMessage.text = msg
        } else {
            itemView.tvEmptyMessage.text = itemView.context.getString(R.string.no_content_found)
        }
    }
}