package com.dgsd.android.hackernews.util

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.act_story.toolbar
import org.jetbrains.anko.dimen
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.verticalPadding
import java.util.*

/**
 * Runs the given action in an {@link OnPreDrawListener}
 *
 * @param view   The view from which to extract the [ViewTreeObserver.OnPreDrawListener]
 * @param action The action to perform
 */
public fun View.onPreDraw(action: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            if (isAttachedToWindow) {
                action()
            }

            return true
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

public fun View.isGone(): Boolean {
    return this.getVisibility() == View.GONE
}

public fun View.isVisible(): Boolean {
    return this.getVisibility() == View.VISIBLE
}

public fun ImageView.setImageUrl(url: String) {
    Picasso.with(this.getContext())
            .load(url)
            .fit()
            .into(this)
}

public fun ViewGroup.children(): List<View> {
    val retval : LinkedList<View> = LinkedList()
    for (i in 0..childCount - 1) {
        retval.add(getChildAt(i))
    }
    return retval
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