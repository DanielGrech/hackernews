package com.dgsd.android.hackernews.mvp.presenter

import android.content.Context
import android.os.Bundle
import com.dgsd.android.hackernews.analytics.Tracker
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MvpView
import javax.inject.Inject

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public abstract class Presenter<V : MvpView>(private val view: V, private val component: AppServicesComponent) {

    @Inject
    protected lateinit var analytics: Tracker

    protected abstract fun getScreenName(): String

    protected fun getContext(): Context {
        return view.getContext()
    }

    protected fun getView(): V {
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
        analytics.trackScreenView(getScreenName())
    }

    public open fun onPause() {
    }

    public open fun onStop() {
    }
}
