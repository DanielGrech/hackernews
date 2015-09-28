package com.dgsd.android.hackernews.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent

public class FabricAgent : Agent {

    override fun trackScreenView(name: String) {
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName(name)
                .putContentType("screen_view"))
    }

    override fun trackClick(item: String) {
        logUiAction(item, "click")
    }

    override fun trackSwipeRefresh(item: String) {
        logUiAction(item, "swipe_refresh")
    }

    private fun logUiAction(action: String, type: String) {
        Answers.getInstance().logCustom(CustomEvent(action)
                .putCustomAttribute("action_type", type))
    }
}
