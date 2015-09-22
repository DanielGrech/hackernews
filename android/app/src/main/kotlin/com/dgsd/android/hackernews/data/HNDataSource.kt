package com.dgsd.android.hackernews.data

import android.support.v4.util.LongSparseArray
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.DbDataSource
import com.dgsd.hackernews.network.utils.filterNulls
import rx.Observable

public class HNDataSource(private val apiDataSource: DataSource, private val dbDataSource: DbDataSource) : DataSource {

    private var topStoriesCache: List<Story>? = null
    private var newStoriesCache: List<Story>? = null

    private var storyCache: LongSparseArray<Story> = LongSparseArray()

    public fun getTopStories(skipCache: Boolean): Observable<List<Story>> {
        val apiObservable = apiDataSource.getTopStories()
                .doOnNext {
                    dbDataSource.saveTopStories(it)
                    topStoriesCache = it
                    it.forEach { story ->
                        storyCache.put(story.id, story)
                    }
                }

        if (skipCache) {
            return apiObservable
        } else {
            val dbObservable = dbDataSource.getTopStories().filter { it.isNotEmpty() }

            val memoryCacheObservable = if (topStoriesCache.orEmpty().isEmpty()) {
                Observable.empty<List<Story>>()
            } else {
                Observable.just(topStoriesCache)
            }

            return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
        }
    }

    public fun getNewStories(skipCache: Boolean): Observable<List<Story>> {
        val apiObservable = apiDataSource.getNewStories()
                .doOnNext {
                    dbDataSource.saveNewStories(it)
                    newStoriesCache = it
                    it.forEach { story ->
                        storyCache.put(story.id, story)
                    }
                }

        if (skipCache) {
            return apiObservable
        } else {
            val dbObservable = dbDataSource.getNewStories().filter { it.isNotEmpty() }

            val memoryCacheObservable = if (newStoriesCache.orEmpty().isEmpty()) {
                Observable.empty<List<Story>>()
            } else {
                Observable.just(newStoriesCache)
            }

            return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
        }
    }

    override fun getNewStories(): Observable<List<Story>> {
        return getNewStories(false)
    }

    override fun getTopStories(): Observable<List<Story>> {
        return getTopStories(false)
    }

    override fun getStory(storyId: Long): Observable<Story> {
        val apiObservable = apiDataSource.getStory(storyId)
                .doOnNext {
                    dbDataSource.saveStory(it)
                    storyCache.put(it.id, it)
                }
        return dbDataSource.getStory(storyId)
                .startWith(storyCache.get(storyId))
                .filterNulls()
                .switchIfEmpty(apiObservable)
    }
}
