package com.dgsd.android.hackernews.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.hideWhen
import com.dgsd.android.hackernews.util.isVisible
import com.dgsd.android.hackernews.util.showWhen
import org.jetbrains.anko.find

public class CommentPlaceholderListItemView(context: Context, attrs: AttributeSet?, defStyle: Int) : FrameLayout (context, attrs, defStyle) {

    private lateinit var placeholderText: TextView

    private lateinit var progressBar: ProgressBar

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
        progressBar = find(R.id.progressBar)
    }

    fun populate(commentIds: List<Long>) {
        placeholderText.text = context.resources.getQuantityString(
                R.plurals.load_more_comments, commentIds.size(), commentIds.size())
    }

    fun setLoading(isLoading: Boolean) {
        progressBar.showWhen(isLoading)
        placeholderText.hideWhen(isLoading)
    }

    fun isLoading(): Boolean {
        return progressBar.isVisible()
    }
}