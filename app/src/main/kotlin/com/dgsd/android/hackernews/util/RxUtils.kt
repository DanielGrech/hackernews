package com.dgsd.android.hackernews.util

import com.dgsd.android.hackernews.activity.BaseActivity
import com.dgsd.android.hackernews.fragment.BaseFragment
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

fun <T> Observable<T>.bind(activity : BaseActivity) : Observable<T> {
    return this.compose<T>(RxLifecycle.bindActivity(activity.lifecycle()));
}

fun <T> Observable<T>.bind(fragment : BaseFragment) : Observable<T> {
    return this.compose<T>(RxLifecycle.bindFragment(fragment.lifecycle()));
}

fun <T> Observable<T>.observeOnMainThread() : Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnIoThread() : Observable<T> {
    return this.observeOn(Schedulers.io())
}

fun <T> Observable<T>.filterNulls() : Observable<T> {
    return this.filter { it != null }
}

fun <T, S : List<T>> Observable<S>.flatMapList() : Observable<T> {
    return flatMap { Observable.from(it) }
}