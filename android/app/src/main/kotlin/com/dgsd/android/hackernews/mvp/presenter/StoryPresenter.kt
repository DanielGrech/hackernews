package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import javax.inject.Inject

public class StoryPresenter(view: StoryMvpView, val component: AppServicesComponent, val storyId: Long) : Presenter<StoryMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    init {
        component.inject(this)
    }

    override fun onStart() {
        super.onStart()
        dataSource.getStory(storyId)
                .bind(getView())
                .onIoThread()
                .subscribe({
                    getView().showStory(it)
                }, {
                    getView().showError(it.toString())
                })
    }
}
