package com.dgsd.android.hackernews.data

import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.DbDataSource
import rx.Observable

public class HNDataSource(private val apiDataSource: DataSource, private val dbDataSource: DbDataSource) : DataSource {

    private var topStoriesCache: List<Story>? = null

    override fun getTopStories(): Observable<List<Story>> {
        val apiObservable = apiDataSource.getTopStories()
                .doOnNext {
                    dbDataSource.saveTopStories(it)
                    topStoriesCache = it
                }

        val dbObservable = dbDataSource.getTopStories()
                .filter {
                    it.isNotEmpty()
                }

        val memoryCacheObservable = if (topStoriesCache.orEmpty().isEmpty()) {
            Observable.empty<List<Story>>()
        } else {
            Observable.just(topStoriesCache)
        }

        return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
    }

}
