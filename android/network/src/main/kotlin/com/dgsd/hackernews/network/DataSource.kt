package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Story
import rx.Observable

public interface DataSource {

    public fun getTopStories(): Observable<List<Story>>;
}