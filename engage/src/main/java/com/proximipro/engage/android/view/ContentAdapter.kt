package com.proximipro.engage.android.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proximipro.engage.android.R
import com.proximipro.engage.android.model.remote.Content
import com.proximipro.engage.android.view.viewholder.BaseContentVH
import com.proximipro.engage.android.view.viewholder.EmptyVH
import com.proximipro.engage.android.view.viewholder.HtmlContentVH
import com.proximipro.engage.android.view.viewholder.ImageContentVH
import com.proximipro.engage.android.view.viewholder.VideoContentVH
import com.proximipro.engage.android.view.viewholder.WebContentVH

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * [RecyclerView.Adapter] for content list view
 * @property clicked Function1<Content, Unit> is the lambda that will be called when an item is clicked from the list
 * @property list ArrayList<Content> is the list containing contents that will be displayed on the list view
 * @property emptyString String is the empty message that will be displayed when there is no item in the list
 */
internal class ContentAdapter(private val clicked: (Content) -> Unit) :
    RecyclerView.Adapter<BaseContentVH>() {

    private val list = ArrayList<Content>()
    private var emptyString: String = ""

    companion object {
        private const val IMAGE = 4
        private const val VIDEO = 5
        private const val WEB_CONTENT = 6
        private const val HTML = 7
        private const val EMPTY = 8
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseContentVH {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            EMPTY -> {
                EmptyVH(inflater.inflate(R.layout.item_empty, parent, false))
            }
            IMAGE -> {
                ImageContentVH(inflater.inflate(R.layout.item_image, parent, false))
            }
            VIDEO -> {
                VideoContentVH(inflater.inflate(R.layout.item_video, parent, false))
            }
            WEB_CONTENT -> {
                WebContentVH(inflater.inflate(R.layout.item_web_content, parent, false))
            }
            else -> {
                HtmlContentVH(inflater.inflate(R.layout.item_html, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseContentVH, position: Int) {
        if (holder is EmptyVH) {
            holder.setMessage(emptyString)
            return
        }
        val content = list[position]
        holder.bindTo(content)
        holder.setClickListener {
            clicked(content)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (list.size == 0) return EMPTY
        return when (list[position].type) {
            "image" -> IMAGE
            "video" -> VIDEO
            "webcontent" -> WEB_CONTENT
            else -> HTML
        }
    }

    override fun getItemCount(): Int {
        return when (list.size) {
            0 -> 1
            else -> list.size
        }
    }

    /**
     * Sets the empty message for the empty view
     * @param message String is the message that will be set for the empty view
     */
    fun setEmptyMessage(message: String) {
        emptyString = message
    }

    /**
     * Allows to submit a new list of content to be displayed into the list
     * @param contents List<Content>
     */
    fun submitList(contents: List<Content>) {
        list.clear()
        list.addAll(contents)
        notifyDataSetChanged()
    }
}

