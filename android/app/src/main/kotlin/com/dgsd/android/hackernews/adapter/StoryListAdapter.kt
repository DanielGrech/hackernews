package com.dgsd.android.hackernews.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dgsd.hackernews.model.Story

public class StoryListAdapter : RecyclerView.Adapter<StoryViewHolder>() {

    private val stories = arrayListOf<Story>()

    override fun getItemCount(): Int {
        return stories.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = TextView(parent.context)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        (holder.itemView as TextView).text = stories[position].toString()
    }

    public fun setStories(newStories: List<Story>) {
        stories.clear()
        stories.addAll(newStories)
        notifyDataSetChanged()
    }
}

class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}