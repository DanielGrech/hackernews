package com.dgsd.android.hackernews.util

import com.dgsd.android.hackernews.activity.BaseActivity
import com.dgsd.android.hackernews.fragment.BaseFragment
import com.dgsd.android.hackernews.mvp.view.MvpView
import com.trello.rxlifecycle.RxLifecycle
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

public fun <T> Observable<T>.onIoThread() : Observable<T> {
    return this.observeOnMainThread().subscribeOnIoThread()
}

public fun<T> Observable<T>.bind(view: MvpView) : Observable<T> {
    if (view is BaseActivity) {
        return bind(view)
    } else if (view is BaseFragment) {
        return bind(view)
    } else {
        return this
    }
}

private fun <T> Observable<T>.bind(activity : BaseActivity) : Observable<T> {
    return this.compose<T>(RxLifecycle.bindActivity(activity.lifecycle()));
}

private fun <T> Observable<T>.bind(fragment : BaseFragment) : Observable<T> {
    return this.compose<T>(RxLifecycle.bindFragment(fragment.lifecycle()));
}

fun <T> Observable<T>.observeOnMainThread() : Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnIoThread() : Observable<T> {
    return this.subscribeOn(Schedulers.io())
}