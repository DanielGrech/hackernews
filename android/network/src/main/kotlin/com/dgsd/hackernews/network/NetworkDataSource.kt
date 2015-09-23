package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.utils.convert
import com.dgsd.hackernews.network.utils.flatMapList
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.wire.Wire
import hackernews.PbStoryList
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.WireConverterFactory
import rx.Observable
import java.util.*
import java.util.concurrent.TimeUnit

public class NetworkDataSource : DataSource {

    companion object {
        private val CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10)

        fun createDefaultHttpClient(): OkHttpClient {
            val client = OkHttpClient()
            client.setReadTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            client.setWriteTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            client.setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            return client
        }
    }

    val apiService: ApiService

    private constructor(builder: NetworkDataSource.Builder) {
        val client = createDefaultHttpClient()
        client.networkInterceptors().addAll(builder.networkInterceptors)

        apiService = Retrofit.Builder()
                .baseUrl(builder.endpoint)
                .client(client)
                .addConverterFactory(WireConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(ApiService::class.java)
    }

    override fun getTopStories(): Observable<List<Story>> {
        return apiService.getTopStories().process()
    }

    override fun getNewStories(): Observable<List<Story>> {
        return apiService.getNewStories().process()
    }

    override fun getAskStories(): Observable<List<Story>> {
        return apiService.getAskStories().process()
    }

    override fun getShowStories(): Observable<List<Story>> {
        return apiService.getShowStories().process()
    }

    override fun getJobStories(): Observable<List<Story>> {
        return apiService.getJobStories().process()
    }

    override fun getStory(storyId: Long): Observable<Story> {
        return apiService.getStory(storyId).map { it.convert() }
    }

    private fun Observable<PbStoryList>.process(): Observable<List<Story>> {
        return this.map { Wire.get(it.stories, PbStoryList.DEFAULT_STORIES) }
                .flatMapList()
                .map { it.convert() }
                .toList()
    }

    public class Builder {
        public var logging: Boolean = false

        public var endpoint: String = ""

        public var networkInterceptors: ArrayList<Interceptor> = arrayListOf()

        public fun build(): NetworkDataSource {
            if (this.logging) {
                this.networkInterceptors.add(LoggingInterceptor())
            }
            return NetworkDataSource(this)
        }
    }
}

fun networkDataSource(init: NetworkDataSource.Builder.() -> Unit): NetworkDataSource {
    val builder = NetworkDataSource.Builder()
    builder.init()
    return builder.build()
}