package com.dgsd.hackernews.network

import com.dgsd.hackernews.network.model.HnStory
import com.dgsd.hackernews.network.model.HnUpdate
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

internal interface ApiService {

    @GET("top")
    fun getTopStories(): Observable<Array<HnStory>>;
}
