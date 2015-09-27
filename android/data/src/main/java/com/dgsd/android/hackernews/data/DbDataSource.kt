package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import rx.Observable

public interface DbDataSource {

    public fun getTopStories(): Observable<List<Story>>

    public fun saveTopStories(stories: List<Story>)

    public fun getNewStories(): Observable<List<Story>>

    public fun saveNewStories(stories: List<Story>)

    public fun getAskStories(): Observable<List<Story>>

    public fun saveAskStories(stories: List<Story>)

    public fun getShowStories(): Observable<List<Story>>

    public fun saveShowStories(stories: List<Story>)

    public fun getJobStories(): Observable<List<Story>>

    public fun saveJobStories(stories: List<Story>)

    public fun getStory(storyId: Long): Observable<Story>

    public fun getComments(parentId: Long): Observable<List<Comment>>

    public fun saveComments(comments: List<Comment>)

    public fun saveStory(story: Story)

    public fun saveComment(comment: Comment)

    public fun clearOldData(): Int
}