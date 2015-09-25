package com.dgsd.android.hackernews.data

import android.support.v4.util.LongSparseArray
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.DbDataSource
import com.dgsd.hackernews.network.utils.filterNulls
import rx.Observable
import rx.lang.kotlin.firstOrNull
import rx.lang.kotlin.toSingletonObservable
import java.util.*
import java.util.concurrent.TimeUnit

public class HNDataSource(private val apiDataSource: DataSource, private val dbDataSource: DbDataSource) : DataSource {

    private val storyListCache = HashMap<PageType, List<Story>>().withDefault({ emptyList() })

    private var storyCache: LongSparseArray<Story> = LongSparseArray()

    public fun clearMemoryCache() {
        storyListCache.clear()
        storyListCache.clear()
    }

    public fun getTopStories(skipCache: Boolean): Observable<List<Story>> {
        return getStories(PageType.TOP, skipCache,
                { apiDataSource.getTopStories() },
                { dbDataSource.getTopStories() },
                { stories -> dbDataSource.saveTopStories(stories) })
    }

    public fun getNewStories(skipCache: Boolean): Observable<List<Story>> {
        return getStories(PageType.NEW, skipCache,
                { apiDataSource.getNewStories() },
                { dbDataSource.getNewStories() },
                { stories -> dbDataSource.saveNewStories(stories) })
    }

    public fun getAskStories(skipCache: Boolean): Observable<List<Story>> {
        return getStories(PageType.ASK_HN, skipCache,
                { apiDataSource.getAskStories() },
                { dbDataSource.getAskStories() },
                { stories -> dbDataSource.saveAskStories(stories) })
    }

    public fun getShowStories(skipCache: Boolean): Observable<List<Story>> {
        return getStories(PageType.SHOW_HN, skipCache,
                { apiDataSource.getShowStories() },
                { dbDataSource.getShowStories() },
                { stories -> dbDataSource.saveShowStories(stories) })
    }

    public fun getJobStories(skipCache: Boolean): Observable<List<Story>> {
        return getStories(PageType.JOBS, skipCache,
                { apiDataSource.getJobStories() },
                { dbDataSource.getJobStories() },
                { stories -> dbDataSource.saveJobStories(stories) })
    }

    public fun getComments(storyId: Long, commentIds: LongArray): Observable<Story> {
        return getStory(storyId).delay(2, TimeUnit.SECONDS)
    }

    override fun getTopStories(): Observable<List<Story>> {
        return getTopStories(false)
    }

    override fun getNewStories(): Observable<List<Story>> {
        return getNewStories(false)
    }

    override fun getAskStories(): Observable<List<Story>> {
        return getAskStories(false)
    }

    override fun getShowStories(): Observable<List<Story>> {
        return getShowStories(false)
    }

    override fun getJobStories(): Observable<List<Story>> {
        return getJobStories(false)
    }

    override fun getStory(storyId: Long): Observable<Story> {
        val apiObservable = apiDataSource.getStory(storyId)
                .doOnNext {
                    dbDataSource.saveStory(it)
                    storyCache.put(it.id, it)
                }

        val dbObservable = dbDataSource.getStory(storyId).firstOrNull().filterNulls()

        val cachedData = storyCache.get(storyId)
        val memoryCacheObservable = if (cachedData == null) {
            Observable.empty()
        } else {
            cachedData.toSingletonObservable()
        }

        return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
    }

    private fun getStories(pageType: PageType, skipCache: Boolean,
                           apiFn: () -> Observable<List<Story>>,
                           dbFn: () -> Observable<List<Story>>,
                           saveFn: (List<Story>) -> Unit): Observable<List<Story>> {
        val apiObservable = apiFn().doOnNext {
            saveFn(it)
            storyListCache[pageType] = it
            it.forEach { story ->
                storyCache.put(story.id, story)
            }
        }

        if (skipCache) {
            return apiObservable
        } else {
            val dbObservable = dbFn().firstOrDefault(emptyList()).filter { it.isNotEmpty() }

            val cachedData = storyListCache.getOrImplicitDefault(pageType)
            val memoryCacheObservable = if (cachedData.isEmpty()) {
                Observable.empty<List<Story>>()
            } else {
                cachedData.toSingletonObservable()
            }

            return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
        }
    }

}
