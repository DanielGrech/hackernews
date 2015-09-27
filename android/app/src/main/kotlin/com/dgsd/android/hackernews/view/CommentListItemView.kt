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
import com.dgsd.android.hackernews.util.*
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

public class CommentListItemView(context: Context, attrs: AttributeSet?, defStyle: Int) : LinearLayout (context, attrs, defStyle) {

    private lateinit var headerText: TextView
    private lateinit var commentText: TextView

    companion object {

        public fun inflate(parent: ViewGroup): CommentListItemView {
            return LayoutInflater.from(parent.context).inflate(R.layout.li_comment, parent, false) as CommentListItemView
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        headerText = find(R.id.headerText)
        commentText = find(R.id.commentText)
    }

    fun populate(comment: Comment, indentation: Int) {
        headerText.text = comment.getSummaryString(context)
        commentText.text = comment.getHtmlContent()
        commentText.movementMethod = LinkMovementMethod.getInstance()

        headerText.textColor = getCommentColorForIndentation(context, indentation)

        if (comment.deadOrDeleted()) {
            commentText.text = context.getString(R.string.comment_list_item_removed_message)
        }
    }

    fun getHeaderIndicatorY(): Float {
        return headerText.y + (headerText.height / 2)
    }
}