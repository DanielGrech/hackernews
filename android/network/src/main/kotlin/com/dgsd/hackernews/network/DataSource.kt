package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Story
import rx.Observable

public interface DataSource {

    public fun getTopStories(): Observable<List<Story>>

    public fun getNewStories(): Observable<List<Story>>

    public fun getAskStories(): Observable<List<Story>>

    public fun getShowStories(): Observable<List<Story>>

    public fun getJobStories(): Observable<List<Story>>

    public fun getStory(storyId: Long): Observable<Story>
}