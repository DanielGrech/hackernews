package com.dgsd.android.hackernews.util

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Runs the given action in an {@link OnPreDrawListener}
 *
 * @param view   The view from which to extract the [ViewTreeObserver.OnPreDrawListener]
 * @param action The action to perform
 */
public fun View.onPreDraw(action: () -> Unit) {
    getViewTreeObserver().addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            getViewTreeObserver().removeOnPreDrawListener(this)

            val shouldExecute: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                shouldExecute = isAttachedToWindow();
            } else {
                shouldExecute = isShown();
            }

            if (shouldExecute) {
                action()
            }

            return true
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