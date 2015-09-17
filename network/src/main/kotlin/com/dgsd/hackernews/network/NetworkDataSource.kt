package com.dgsd.hackernews.network

import com.dgsd.hackernews.model.Item
import com.dgsd.hackernews.network.utils.convert
import com.dgsd.hackernews.network.utils.filterNulls
import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import rx.Observable
import java.util.*
import java.util.concurrent.TimeUnit
import com.dgsd.hackernews.network.utils.*

public class NetworkDataSource : DataSource {

    companion object {
        private val CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10)

        private val COLLECTION_ITEMS_TO_FETCH = 10

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
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(ApiService::class.java)
    }

    override fun getItem(itemId: Long): Observable<Item> {
        return apiService.getItem(itemId)
                .filterNulls()
                .map {
                    it.convert()
                }
    }

    override fun getTopStories(): Observable<List<Item>> {
        return apiService.getTopStories()
                .map {
                    it.mapIndexed { index, value ->
                        value.to(index < NetworkDataSource.COLLECTION_ITEMS_TO_FETCH)
                    }
                }
                .flatMapList()
                .flatMap {
                    if (it.second) {
                        getItem(it.first)
                    } else {
                        Observable.just(Item(id = it.first))
                    }
                }
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