package com.dgsd.android.hackernews.view

import android.content.Context
import android.support.v7.widget.CardView
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.getDateTimeString
import com.dgsd.android.hackernews.util.onPreDraw
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

        onPreDraw {
            val lps = storyTitle.layoutParams as ViewGroup.MarginLayoutParams
            lps.topMargin += storyDate.height
            lps.bottomMargin += storyDate.height + ((storyCommentCount.height - storyDate.height) / 2)

            storyTitle.layoutParams = lps

            false
        }
    }

    fun populate(story: Story) {
        storyTitle.text = story.title
        storyCommentCount.text = MessageFormat.format(context.getString(R.string.story_list_item_comments_template),
                story.commentCount, if (story.score <= 1) "No" else story.score.toString())
        storyAuthor.text = context.getString(R.string.story_list_item_author_template, story.author)

        storyDate.text = context.getString(R.string.story_list_item_time_template, story.getDateTimeString())
    }
}