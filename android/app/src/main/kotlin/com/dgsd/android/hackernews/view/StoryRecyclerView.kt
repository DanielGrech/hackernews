package com.dgsd.android.hackernews.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.adapter.StoryListAdapter
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.dimen

public class StoryRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView (context, attrs, defStyle) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = StoryListAdapter()

        addItemDecoration(VerticalSpaceItemDecoration(context.dimen(R.dimen.padding_small)))
    }

    public fun setStories(stories: List<Story>) {
        (adapter as StoryListAdapter).setStories(stories)
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
