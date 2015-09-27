package com.dgsd.android.hackernews.data

import android.support.v4.util.LongSparseArray
import android.util.SparseLongArray
import com.dgsd.android.hackernews.BuildConfig
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.DbDataSource
import com.dgsd.hackernews.network.utils.filterNulls
import rx.Observable
import rx.lang.kotlin.firstOrNull
import rx.lang.kotlin.toSingletonObservable
import timber.log.Timber
import java.util.*

public class HNDataSource(private val apiDataSource: DataSource, private val dbDataSource: DbDataSource) : DataSource {

    private val storyListCache = HashMap<PageType, List<Story>>().withDefault({ emptyList() })

    private val storyCache: LongSparseArray<Story> = LongSparseArray()

    private val networkCallTimeArray = SparseLongArray()

    public fun clearMemoryCache() {
        storyListCache.clear()
        storyListCache.clear()
    }

    public fun clearOldData(): Int {
        return dbDataSource.clearOldData()
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

    override fun getComments(storyId: Long, commentIds: LongArray): Observable<List<Comment>> {
        return apiDataSource.getComments(storyId, commentIds)
                .doOnNext {
                    dbDataSource.saveComments(it)
                    storyCache.remove(storyId)
                }
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
        return getStory(storyId, false)
    }

    fun getStory(storyId: Long, skipCache: Boolean): Observable<Story> {
        val apiObservable: Observable<Story>

        if (skipCache) {
            apiObservable = apiDataSource.getStory(storyId)
                    .doOnNext {
                        dbDataSource.saveStory(it)
                        storyCache.put(it.id, it)
                    }

            return apiObservable
        } else {
            apiObservable = Observable.empty()
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
        val apiObservable: Observable<List<Story>>;
        if (skipCache || shouldMakeNetworkCall(pageType)) {
            apiObservable = apiFn().doOnNext {
                networkCallTimeArray.put(pageType.ordinal(), System.currentTimeMillis())

                saveFn(it)
                storyListCache[pageType] = it
                it.forEach { story ->
                    if (storyCache.get(story.id) == null) {
                        storyCache.put(story.id, story)
                    }
                }
            }
        } else {
            Timber.d("Already made a recent network call. Skipping...")
            apiObservable = Observable.empty()
        }

        if (skipCache) {
            return apiObservable
        } else {
            val dbObservable = dbFn().firstOrDefault(emptyList())
                    .filter { it.isNotEmpty() }
                    .doOnNext {
                        if (storyListCache.getOrImplicitDefault(pageType).isEmpty()) {
                            storyListCache[pageType] = it
                        }
                    }

            val cachedData = storyListCache.getOrImplicitDefault(pageType)
            val memoryCacheObservable = if (cachedData.isEmpty()) {
                Observable.empty<List<Story>>()
            } else {
                cachedData.toSingletonObservable()
            }

            return Observable.mergeDelayError(memoryCacheObservable, dbObservable, apiObservable)
        }
    }

    private fun shouldMakeNetworkCall(pageType: PageType): Boolean {
        val lastCallTime = networkCallTimeArray.get(pageType.ordinal(), -1)
        return System.currentTimeMillis() - BuildConfig.STORY_LIST_NETWORK_CACHE_TIME > lastCallTime
    }
}
