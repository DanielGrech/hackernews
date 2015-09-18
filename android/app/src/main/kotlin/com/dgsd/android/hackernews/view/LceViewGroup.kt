package com.dgsd.android.hackernews.view

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.children
import com.dgsd.android.hackernews.util.hide
import com.dgsd.android.hackernews.util.show
import org.jetbrains.anko.find
import java.util.*

public class LceViewGroup(context: Context, attrs: AttributeSet?, defStyle: Int) : FrameLayout(context, attrs, defStyle) {

    lateinit var content: ViewGroup

    lateinit var progressBar: ProgressBar

    lateinit var errorMessage: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutTransition = LayoutTransition()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val childrenToAdd = ArrayList(this.children())
        childrenToAdd.forEach {
            removeView(it)
        }

        LayoutInflater.from(getContext()).inflate(R.layout.view_lce, this, true)

        content = find(R.id.content)
        progressBar = find(R.id.progressBar)
        errorMessage = find(R.id.errorMessage)

        childrenToAdd.forEach {
            content.addView(it)
        }
    }

    public fun showContent() {
        content.show()
        progressBar.hide()
        errorMessage.hide()
    }

    public fun showLoading() {
        progressBar.show()
        errorMessage.hide()
        content.hide()
    }

    public fun showError(message: CharSequence) {
        errorMessage.text = message
        errorMessage.show()
        content.hide()
        progressBar.hide()
    }
}
