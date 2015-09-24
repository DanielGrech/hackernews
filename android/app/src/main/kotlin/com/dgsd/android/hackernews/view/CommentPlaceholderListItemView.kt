package com.dgsd.android.hackernews.view

import android.content.Context
import android.support.v7.widget.CardView
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.getDateTimeString
import com.dgsd.android.hackernews.util.getHtmlContent
import com.dgsd.android.hackernews.util.getSummaryString
import com.dgsd.android.hackernews.util.onPreDraw
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

public class CommentPlaceholderListItemView(context: Context, attrs: AttributeSet?, defStyle: Int) : LinearLayout (context, attrs, defStyle) {

    private lateinit var placeholderText: TextView

    companion object {

        public fun inflate(parent: ViewGroup): CommentPlaceholderListItemView {
            return LayoutInflater.from(parent.context).inflate(R.layout.li_comment_placeholder, parent, false) as CommentPlaceholderListItemView
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        placeholderText = find(R.id.placeholderText)
    }

    fun populate(commentIds: List<Long>) {
        placeholderText.text = "${commentIds.size()} more comments to load"
    }
}