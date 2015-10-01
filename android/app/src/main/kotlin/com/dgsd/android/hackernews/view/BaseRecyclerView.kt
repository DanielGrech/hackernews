package com.dgsd.android.hackernews.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

abstract class BaseRecyclerView<T>(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView (context, attrs, defStyle) {

    protected var onClickListener: (T, View) -> Unit = { s, v -> }

    protected var onScrollChangedListener: (Int) -> Unit = { dY -> }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = LinearLayoutManager(context)

        addOnScrollListener(ScrollListener())
    }

    public fun setOnStoryClickListener(listener: (T, View) -> Unit) {
        onClickListener = listener
    }

    public fun setOnScrollListener(listener: (Int) -> Unit) {
        onScrollChangedListener = listener
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            onScrollChangedListener(dy)
        }
    }
}