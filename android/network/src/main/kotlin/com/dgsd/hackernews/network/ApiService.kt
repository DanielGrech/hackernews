package com.dgsd.hackernews.network

import com.dgsd.hackernews.network.model.HnStory
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

internal interface ApiService {

    @GET("top")
    fun getTopStories(): Observable<Array<HnStory>>

    @GET("new")
    fun getNewStories(): Observable<Array<HnStory>>

    @GET("ask")
    fun getAskStories(): Observable<Array<HnStory>>

    @GET("show")
    fun getShowStories(): Observable<Array<HnStory>>

    @GET("job")
    fun getJobStories(): Observable<Array<HnStory>>

    @GET("story/{story_id}")
    fun getStory(@Path("story_id") storyId: Long): Observable<HnStory>
}
