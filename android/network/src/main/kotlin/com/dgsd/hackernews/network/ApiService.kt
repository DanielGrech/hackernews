package com.dgsd.hackernews.network

import hackernews.PbCommentList
import hackernews.PbStory
import hackernews.PbStoryList
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable

private val FORMAT = "proto"

internal interface ApiService {

    @GET("top?format=$FORMAT")
    fun getTopStories(): Observable<PbStoryList>

    @GET("new?format=$FORMAT")
    fun getNewStories(): Observable<PbStoryList>

    @GET("ask?format=$FORMAT")
    fun getAskStories(): Observable<PbStoryList>

    @GET("show?format=$FORMAT")
    fun getShowStories(): Observable<PbStoryList>

    @GET("job?format=$FORMAT")
    fun getJobStories(): Observable<PbStoryList>

    @GET("comments?format=$FORMAT")
    fun getComments(@Query("story") storyId: Long, @Query("id") ids: LongArray): Observable<PbCommentList>

    @GET("story/{story_id}?format=$FORMAT")
    fun getStory(@Path("story_id") storyId: Long): Observable<PbStory>
}
