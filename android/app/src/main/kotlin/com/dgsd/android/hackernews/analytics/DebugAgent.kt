package com.dgsd.android.hackernews.analytics

import timber.log.Timber

public class DebugAgent : Agent {

    override fun trackScreenView(name: String) {
        Timber.d("screen_view_%s", name)
    }

    override fun trackClick(item: String) {
        Timber.d("track_click_%s", item)
    }

    override fun trackSwipeRefresh(item: String) {
        Timber.d("track_swipe_refresh_%s", item)
    }
}
