package com.dgsd.android.hackernews.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.PopupMenu
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.adapter.StoryListAdapter
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.dimen

public class StoryRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView (context, attrs, defStyle) {

    private var onClickListener: (Story, View) -> Unit = { s, v -> }

    private var onShareStoryCommentLinkListener: (Story) -> Unit = { s -> }

    private var onShareStoryLinkListener: (Story) -> Unit = { s -> }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = StoryListAdapter()

        addItemDecoration(VerticalSpaceItemDecoration(context.dimen(R.dimen.padding_small)))

        with (adapter as StoryListAdapter) {
            setOnStoryClickListener { story, view ->
                onClickListener(story, view)
            }

            setOnStoryLongClickListener { story, view ->
                with(PopupMenu(context, view)) {
                    inflate(R.menu.cm_share_story)

                    with (menu.findItem(R.id.share_link)) {
                        setEnabled(!story.url.isNullOrBlank())
                        setVisible(!story.url.isNullOrBlank())
                    }

                    setOnMenuItemClickListener {
                        if (it.itemId == R.id.share_comments) {
                            onShareStoryCommentLinkListener(story)
                        } else if (it.itemId == R.id.share_link) {
                            onShareStoryLinkListener(story)
                        }
                        true
                    }
                    show()
                }
                true
            }
        }
    }

    public fun setStories(stories: List<Story>) {
        (adapter as StoryListAdapter).setStories(stories)
    }

    public fun setOnStoryClickListener(listener: (Story, View) -> Unit) {
        onClickListener = listener
    }

    public fun setOnShareStoryCommentLinkListener(listener: (Story) -> Unit) {
        onShareStoryCommentLinkListener = listener
    }

    public fun setOnShareStoryLinkListener(listener: (Story) -> Unit) {
        onShareStoryLinkListener = listener
    }

    public class VerticalSpaceItemDecoration(val verticalHeight: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.bottom = verticalHeight
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = verticalHeight
            }
        }
    }
}
