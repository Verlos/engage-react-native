package com.proximipro.engage.android.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proximipro.engage.android.model.remote.Content

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * Base [RecyclerView.ViewHolder] class for creating ViewHolders for RecyclerView
 */
internal abstract class BaseContentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Binds the model instance to the view for that item
     * @param content Content is the model instance
     */
    abstract fun bindTo(content: Content)

    /**
     * Sets click listener on the preferred view
     * @param func (View) -> Unit is the lambda that will be called on click events
     */
    abstract fun setClickListener(func: (View) -> Unit)
}