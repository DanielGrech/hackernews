package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import rx.Observable

public interface DbDataSource {

    public fun getTopStories(): Observable<List<Story>>

    public fun saveTopStories(stories: List<Story>)

    public fun getStory(storyId: Long): Observable<Story>

    public fun getComments(parentId: Long): Observable<List<Comment>>

    public fun saveStory(story: Story)
}