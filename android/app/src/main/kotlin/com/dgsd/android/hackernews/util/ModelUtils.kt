package com.dgsd.android.hackernews.util

import android.support.annotation.StringRes
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.model.PageType

@StringRes
public fun PageType.getTitleRes(): Int {
    return when (this) {
        PageType.TOP -> R.string.tab_title_top
        PageType.NEW -> R.string.tab_title_new
        PageType.ASK_HN -> R.string.tab_title_ask_hn
        PageType.SHOW_HN -> R.string.tab_title_show_hn
        PageType.JOBS -> R.string.tab_title_jobs
    }
}
