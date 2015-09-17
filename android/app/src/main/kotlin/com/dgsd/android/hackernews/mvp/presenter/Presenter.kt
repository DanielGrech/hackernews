package com.dgsd.android.hackernews.mvp.presenter

import android.content.Context
import android.os.Bundle
import com.dgsd.android.hackernews.activity.BaseActivity
import com.dgsd.android.hackernews.fragment.BaseFragment
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.observeOnMainThread
import com.dgsd.android.hackernews.util.subscribeOnIoThread
import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.Subscription

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public abstract class Presenter<V : MvpView>(private val view: V, private val component : AppServicesComponent) {

    protected fun getContext(): Context {
        return view.getContext()
    }

    protected fun getView() : V {
        return view
    }

    public open fun onCreate(savedInstanceState: Bundle?) {

    }

    public open fun onSaveInstanceState(savedInstanceState: Bundle?) {
    }

    public open fun onDestroy() {
    }

    public open fun onStart() {
    }

    public open fun onResume() {
    }

    public open fun onPause() {

    }

    public open fun onStop() {
    }
}
