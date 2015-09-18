package com.dgsd.android.hackernews.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dgsd.android.hackernews.view.StoryListItemView
import com.dgsd.hackernews.model.Story

public class StoryListAdapter : RecyclerView.Adapter<StoryViewHolder>() {

    private val stories = arrayListOf<Story>()

    override fun getItemCount(): Int {
        return stories.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(StoryListItemView.inflate(parent))
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.populate(stories[position])
    }

    public fun setStories(newStories: List<Story>) {
        stories.clear()
        stories.addAll(newStories)
        notifyDataSetChanged()
    }
}

class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    public fun populate(story: Story) {
        (itemView as StoryListItemView).populate(story)
    }
}