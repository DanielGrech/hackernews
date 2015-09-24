package com.dgsd.android.hackernews.util

import android.content.Context
import android.support.annotation.StringRes
import android.text.format.DateUtils
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.hackernews.model.Story
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

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

public fun Story.getSummaryString(context: Context): String {
    return MessageFormat.format(context.getString(R.string.story_summary_string_template),
            author,
            if (score <= 1) "No" else score.toString()
    )

}

public fun Story.getDateTimeString(): CharSequence {
    val dateFlags = DateUtils.FORMAT_SHOW_TIME.or(DateUtils.FORMAT_SHOW_DATE)
    return DateUtils.getRelativeTimeSpanString(TimeUnit.SECONDS.toMillis(time),
            System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, dateFlags)
}