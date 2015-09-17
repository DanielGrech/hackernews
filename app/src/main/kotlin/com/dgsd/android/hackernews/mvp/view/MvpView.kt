package com.dgsd.android.hackernews.mvp.view

import android.content.Context

/**
 * Base interface for representing a view in the Model-View-Presenter architecture
 */
public interface MvpView {

    /**
     * @return The Android [Context] of the view
     */
    public fun getContext(): Context
}
