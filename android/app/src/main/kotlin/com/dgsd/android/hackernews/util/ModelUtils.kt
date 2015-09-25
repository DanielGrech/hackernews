package com.dgsd.android.hackernews.util

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.util.LruCache
import android.text.Html
import android.text.format.DateUtils
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import java.util.concurrent.TimeUnit

private val storyHtmlContentCache = LruCache<Long, CharSequence>(20)
private val commentHtmlContentCache = LruCache<Long, CharSequence>(20)

public fun clearHtmlContentCache() {
    storyHtmlContentCache.evictAll()
    commentHtmlContentCache.evictAll()
}

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
    return context.getString(R.string.story_summary_string_template,
            author, if (score <= 1) "No" else score.toString())
}

public fun Comment.getSummaryString(context: Context): String {
    return if (deadOrDeleted()) {
        context.getString(R.string.comment_list_item_header_template_removed)
    } else {
        context.getString(R.string.comment_list_item_header_template,
                author, getDateTimeString())
    }
}

public fun List<Comment>.groupById(): Map<Long, Comment> {
    return groupBy { it.id }.mapValues { it.value.first() }
}

public fun Story.getDateTimeString(): CharSequence {
    return getDateTimeString(time)
}

public fun Comment.getDateTimeString(): CharSequence {
    return getDateTimeString(time)
}

public fun Story.getHtmlContent(): CharSequence? {
    return getHtmlTextFromCache(id, text, storyHtmlContentCache)
}

public fun Comment?.getHtmlContent(): CharSequence? {
    if (this == null) {
        return null
    } else {
        return getHtmlTextFromCache(id, text, commentHtmlContentCache)
    }
}
private fun getDateTimeString(time: Long): CharSequence {
    val dateFlags = DateUtils.FORMAT_SHOW_TIME.or(DateUtils.FORMAT_SHOW_DATE)
    return DateUtils.getRelativeTimeSpanString(TimeUnit.SECONDS.toMillis(time),
            System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, dateFlags)
}

private fun getHtmlTextFromCache(key: Long, text: String?, cache: LruCache<Long, CharSequence>): CharSequence?{
    if (text.isNullOrEmpty()) {
        return null
    }

    var htmlContent = cache.get(key)
    if (htmlContent == null) {
        htmlContent = Html.fromHtml(text)
        cache.put(key, htmlContent)
    }

    return htmlContent
}