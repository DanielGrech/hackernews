package com.dgsd.hackernews.network

import com.dgsd.hackernews.network.model.HnItem
import com.dgsd.hackernews.network.model.HnUpdate
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

internal interface ApiService {

    @GET("item/{itemId}.json")
    fun getItem(@Path("itemId") itemId: Long): Observable<HnItem>;

    @GET("maxitem.json")
    fun getMaxItem(): Observable<Long>;

    @GET("topstories.json")
    fun getTopStories(): Observable<Array<Long>>;

    @GET("newstories.json")
    fun getNewStories(): Observable<Array<Long>>;

    @GET("askstories.json")
    fun getAskStories(): Observable<Array<Long>>;

    @GET("jobstories.json")
    fun getJobStories(): Observable<Array<Long>>;

    @GET("showstories.json")
    fun getShowStories(): Observable<Array<Long>>;

    @GET("updates.json")
    fun getUpdates(): Observable<HnUpdate>;
}
