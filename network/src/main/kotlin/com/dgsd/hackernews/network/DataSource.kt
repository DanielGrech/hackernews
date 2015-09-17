package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Item
import rx.Observable

public interface DataSource {

    public fun getItem(itemId: Long): Observable<Item>;

    public fun getTopStories(): Observable<List<Item>>;
}