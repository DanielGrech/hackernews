package com.dgsd.android.hackernews.util

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.annotation.ColorInt
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import com.dgsd.android.hackernews.R
import java.util.*

var indentationColors: IntArray? = null

/**
 * Runs the given action in an {@link OnPreDrawListener}
 *
 * @param action The action to perform
 */
public fun View.onPreDraw(action: () -> Boolean) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            return if (isAttachedToWindow) action() else true
        }
    })
}

public fun View.onLayout(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            if (isAttachedToWindow) {
                action()
            }
        }
    })
}

/**
 * Set the visibility of all the given views to [View.GONE]
 *
 * @param views The views to hide
 */
public fun View.hide() {
    setVisibility(View.GONE)
}

/**
 * Set the visibility of all the given views to [View.INVISIBLE]
 *
 * @param views The views to
 */
public fun View.hideInvisible() {
    setVisibility(View.INVISIBLE)
}

/**
 * Set the visibility of all the given views to [View.VISIBLE]
 *
 * @param views The views to show
 */
public fun View.show() {
    setVisibility(View.VISIBLE)
}

public fun View.showWhen(condition : Boolean) {
    if (condition) {
        this.show()
    } else {
        this.hide()
    }
}


public fun View.hideWhen(condition : Boolean) {
    showWhen(!condition)
}

public fun View.isGone(): Boolean {
    return this.getVisibility() == View.GONE
}

public fun View.isVisible(): Boolean {
    return this.getVisibility() == View.VISIBLE
}

public fun ViewGroup.children(): List<View> {
    val retval : LinkedList<View> = LinkedList()
    for (i in 0..childCount - 1) {
        retval.add(getChildAt(i))
    }
    return retval
}

public inline fun <reified T> View.findFirstChild(): T? {
    if (this is ViewGroup) {
        val list = LinkedList(children())
        while (list.isNotEmpty()) {
            val view = list.pop()
            if (view is T) {
                return view
            } else if (view is ViewGroup) {
                list += view.children()
            }
        }
    }

    return null
}

public fun View.startActivity(intent: Intent) {
    val activityOpts: ActivityOptions
    if (Api.isMin(Api.MARSHMALLOW)) {
        activityOpts = ActivityOptions.makeClipRevealAnimation(this, width / 2, height / 2, width, height)
    } else {
        activityOpts = ActivityOptions.makeScaleUpAnimation(this, width / 2, height / 2, width, height)
    }
    context.startActivity(intent, activityOpts.toBundle())
}

public fun Toolbar.getTitleView(): TextView? {
    val field = javaClass.getDeclaredField("mTitleTextView")
    field?.isAccessible = true
    return field?.get(this) as TextView?
}

public fun Toolbar.getSubtitleView(): TextView? {
    val field = javaClass.getDeclaredField("mSubtitleTextView")
    field?.isAccessible = true
    return field?.get(this) as TextView?
}

@ColorInt
public fun getCommentColorForIndentation(context: Context, indentation: Int): Int {
    if (indentationColors == null) {
        indentationColors = context.resources.getIntArray(R.array.comment_indentation_colors)
    }

    val size = indentationColors!!.size()
    return indentationColors!![indentation % size]
}