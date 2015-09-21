package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.network.DataSource
import timber.log.Timber
import javax.inject.Inject

public class StoryPresenter(view : StoryMvpView, component : AppServicesComponent) : Presenter<StoryMvpView>(view, component) {

    @Inject
    lateinit val dataSource: DataSource

    init {
        component.inject(this)
    }

}
