package com.dgsd.android.hackernews.view

import android.content.Context
import android.support.v7.widget.CardView
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import java.text.MessageFormat
import java.util.concurrent.TimeUnit

public class StoryListItemView(context: Context, attrs: AttributeSet?, defStyle: Int) : CardView (context, attrs, defStyle) {

    private lateinit var storyTitle: TextView
    private lateinit var storyCommentCount: TextView
    private lateinit var storyDate: TextView
    private lateinit var storyAuthor: TextView

    companion object {

        public fun inflate(parent: ViewGroup): StoryListItemView {
            return LayoutInflater.from(parent.context).inflate(R.layout.li_story, parent, false) as StoryListItemView
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()

        storyTitle = find(R.id.storyTitle)
        storyCommentCount = find(R.id.storyCommentCount)
        storyDate = find(R.id.storyDate)
        storyAuthor = find(R.id.storyAuthor)
    }

    fun populate(story: Story) {
        storyTitle.text = story.title
        storyCommentCount.text = MessageFormat.format(
                context.getString(R.string.story_list_item_comments_template), story.commentCount)
        storyAuthor.text = context.getString(R.string.story_list_item_author_template, story.author)

        val dateFlags = DateUtils.FORMAT_ABBREV_ALL.or(DateUtils.FORMAT_SHOW_TIME).or(DateUtils.FORMAT_SHOW_DATE)
        val dateText = DateUtils.formatDateTime(context, TimeUnit.SECONDS.toMillis(story.time), dateFlags)
        storyDate.text = context.getString(R.string.story_list_item_time_template, dateText)
    }
}