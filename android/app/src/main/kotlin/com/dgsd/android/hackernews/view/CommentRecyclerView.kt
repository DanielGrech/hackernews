package com.dgsd.android.hackernews.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.adapter.CommentListAdapter
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.dimen

public class CommentRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView (context, attrs, defStyle) {

    private var onClickListener:  (Comment, View) -> Unit = {s, v -> }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = CommentListAdapter()

        addItemDecoration(IndentItemDecoration(context.dimen(R.dimen.padding_default), context.dimen(R.dimen.padding_default)))

        (adapter as CommentListAdapter).setOnCommentClickListener { Comment, view ->
            onClickListener(Comment, view)
        }
    }

    public fun setStory(story: Story) {
        (adapter as CommentListAdapter).setStory(story)
    }

    public fun setOnCommentClickListener(listener: (Comment, View) -> Unit) {
        (adapter as CommentListAdapter).setOnCommentClickListener(listener)
    }

    public fun setOnCommentIdClickListener(listener: (List<Long>, View) -> Unit) {
        (adapter as CommentListAdapter).setOnCommentIdClickListener(listener)
    }

    public class IndentItemDecoration(val indentSize: Int, val verticalPadding: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val vh = parent.getChildViewHolder(view) as CommentListAdapter.CommentViewHolder
            outRect.left = indentSize * vh.getIndentationLevel()

            val isFirstItem = vh.position == 0
            val isLastItem = vh.position == parent.adapter.itemCount - 1
            val isCommentPlaceholder = vh.itemViewType == CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER

            if (isLastItem) {
                outRect.bottom = verticalPadding

                if (isCommentPlaceholder) {
                    outRect.top = verticalPadding
                }
            }

            if (isCommentPlaceholder) {
                outRect.bottom = verticalPadding
            }
        }
    }
}
