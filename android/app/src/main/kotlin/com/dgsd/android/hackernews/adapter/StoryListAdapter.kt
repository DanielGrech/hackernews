package com.dgsd.android.hackernews.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dgsd.android.hackernews.view.StoryListItemView
import com.dgsd.hackernews.model.Story

public class StoryListAdapter : RecyclerView.Adapter<StoryListAdapter.StoryViewHolder>() {

    private val stories = arrayListOf<Story>()

    private var onClickListener:  (Story, View) -> Unit = {s, v -> }

    private var onLongClickListener: (Story, View) -> Boolean = {s, v -> true}

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

    public fun setOnStoryClickListener(listener: (Story, View) -> Unit) {
        onClickListener = listener
    }

    public fun setOnStoryLongClickListener(listener: (Story, View) -> Boolean) {
        onLongClickListener = listener
    }

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            onClickListener(stories[position], v)
        }

        override fun onLongClick(v: View): Boolean {
            return onLongClickListener(stories[position], v)
        }

        public fun populate(story: Story) {
            (itemView as StoryListItemView).populate(story)
        }
    }
}