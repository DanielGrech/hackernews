package com.dgsd.hackernews.network.utils

import rx.Observable

public fun <T> Observable<T>.filterNulls() : Observable<T> {
    return this.filter { it != null }
}

public fun <T, S : List<T>> Observable<S>.flatMapList() : Observable<T> {
    return flatMap { Observable.from(it) }
}

public fun <T, S : Array<T>> Observable<S>.flatMapArray() : Observable<T> {
    return flatMap { Observable.from(it) }
}
