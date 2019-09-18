package com.proximipro.engage.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.proximipro.engage.android.R
import com.proximipro.engage.android.model.remote.Content
import kotlinx.android.synthetic.main.layout_content_list.view.rvContentList

/*
 * Created by Birju Vachhani on 27 May 2019
 * Copyright © 2019 engage-sdk. All rights reserved.
 */

/**
 * A custom view to display content list
 *
 * This view handles all the different type of content views that the sdk have. It also handles click events on every
 * item in the list.
 * The list will display empty message which basically indicates that the scan has not been initiated yet or there's no content available.
 * @property root View is the root of all the views
 * @property adapter ContentAdapter is the list adapter for recycle view
 * @property listener Function1<Content, Unit> called on item clicks
 */
class ContentListView : LinearLayout {

    private val root: View
    private val adapter = ContentAdapter(::onItemClicked)
    private var listener: (Content) -> Unit = {}

    constructor(context: Context) : super(context) {
        root = inflateView(context)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        root = inflateView(context)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) {
        root = inflateView(context)
        init(context)
    }

    /**
     * Inflates the custom view and attaches it to this view
     * @param context Context is the android context for this view
     * @return View which the new root of all the inflated view
     */
    private fun inflateView(context: Context): View = inflate(context, R.layout.layout_content_list, this)

    /**
     * Initializes this custom view.
     * @param context Context is the android context for the view
     */
    private fun init(context: Context) {
        root.rvContentList.layoutManager = LinearLayoutManager(context)
        root.rvContentList.adapter = adapter
    }

    /**
     * Sets the list of [Content] to the recycler view.
     * @param contents List<Content> is the [Content] list that will be displayed to the user
     */
    fun setContent(contents: List<Content>) = adapter.submitList(contents)

    /**
     * Allows to set an item click listener.
     * @param func (Content) -> Unit is the lambda which will be invoked whenever the user clicks on an item
     */
    fun setItemClickListener(func: (Content) -> Unit) {
        listener = func
    }

    /**
     * This method will be invoked when an item is clicked
     * @param content Content is the content of the item that was clicked
     */
    private fun onItemClicked(content: Content) = listener(content)
}