package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Story
import hackernews.PbStory
import hackernews.PbStoryList
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rx.Observable
import rx.observers.TestSubscriber

public class NetworkDataSourceTest {

    lateinit var networkDataSource: NetworkDataSource

    @Before
    fun setup() {
        networkDataSource = networkDataSource {
            endpoint = "http://google.com"
        }
        networkDataSource.apiService = mock(ApiService::class.java)
    }

    @Test
    fun testGetTopStoriesOnNoApiResult() {
        forEachGetStoriesCall { apiServiceFn, dataSourceFn ->
            `when`(apiServiceFn()).thenReturn(Observable.empty())

            val subscriber = TestSubscriber<List<Story>>()
            dataSourceFn().subscribe(subscriber)

            subscriber.assertValueCount(1)
            assertThat(subscriber.onNextEvents.first()).isEmpty()
        }
    }

    @Test
    fun testGetTopStoriesOnEmptyApiResult() {
        forEachGetStoriesCall { apiServiceFn, dataSourceFn ->
            `when`(apiServiceFn()).thenReturn(
                    Observable.just(PbStoryList.Builder().build()))

            val subscriber = TestSubscriber<List<Story>>()
            dataSourceFn().subscribe(subscriber)

            subscriber.assertValueCount(1)
            assertThat(subscriber.onNextEvents.first()).isEmpty()
        }
    }

    @Test
    fun testGetStories() {
        forEachGetStoriesCall { apiServiceFn, dataSourceFn ->
            `when`(apiServiceFn()).thenReturn(
                    Observable.just(PbStoryList.Builder()
                            .stories(listOf(PbStory.Builder().id(1).time(1).type("story").build()))
                            .build()))

            val subscriber = TestSubscriber<List<Story>>()
            dataSourceFn().subscribe(subscriber)

            subscriber.assertValueCount(1)
            val story = subscriber.onNextEvents
                    .filter { it.size() == 1 }
                    .map { it.first() }
                    .first()

            assertThat(story.id).isEqualTo(1)
            assertThat(story.time).isEqualTo(1)
        }
    }

    private fun forEachGetStoriesCall(fn: (() -> Observable<PbStoryList>, () -> Observable<List<Story>>) -> Unit) {
        listOf(
                { networkDataSource.apiService.getTopStories() } to {networkDataSource.getTopStories()},
                { networkDataSource.apiService.getNewStories() } to {networkDataSource.getNewStories()},
                { networkDataSource.apiService.getAskStories() } to {networkDataSource.getAskStories()},
                { networkDataSource.apiService.getShowStories() } to {networkDataSource.getShowStories()},
                { networkDataSource.apiService.getJobStories() } to {networkDataSource.getJobStories()}
        ).forEach { fnPair ->
            fn(fnPair.first, fnPair.second)
        }
    }
}