package com.dgsd.android.hackernews.view

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.getDateTimeString
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import java.text.MessageFormat

public class StoryListItemView(context: Context, attrs: AttributeSet?, defStyle: Int) : CardView (context, attrs, defStyle) {

    private lateinit var storyTitle: TextView
    private lateinit var storySubtitle: TextView

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
        storySubtitle = find(R.id.storySubtitle)
    }

    fun populate(story: Story) {
        storyTitle.text = story.title
        storySubtitle.text = MessageFormat.format(context.getString(R.string.story_list_item_comments_template),
                story.commentCount,
                if (story.score <= 1) "No" else story.score.toString(),
                story.author,
                story.getDateTimeString()
        )
    }
}