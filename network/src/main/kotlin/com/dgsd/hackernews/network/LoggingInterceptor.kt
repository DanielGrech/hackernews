package com.dgsd.hackernews.network

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

internal class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();

        val t1 = System.nanoTime();
        System.err.println("Sending request %s on %s%n%s".format(
                request.url(), chain.connection(), request.headers()
        ));

        val response = chain.proceed(request);

        val t2 = System.nanoTime();
        System.err.println("Received response for %s in %.1fms%n%s".format(
                response.request().url(), TimeUnit.NANOSECONDS.toMillis(t2 - t1), response.headers()
        ));

        return response
    }

}
