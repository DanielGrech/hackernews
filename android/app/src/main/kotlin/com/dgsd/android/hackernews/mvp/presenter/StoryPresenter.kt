package com.dgsd.android.hackernews.mvp.presenter

import android.net.Uri
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.model.Story
import javax.inject.Inject

public class StoryPresenter(view: StoryMvpView, val component: AppServicesComponent, val storyId: Long) : Presenter<StoryMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    private var story: Story? = null

    init {
        component.inject(this)
    }

    override fun onStart() {
        super.onStart()
        dataSource.getStory(storyId)
                .bind(getView())
                .onIoThread()
                .subscribe({
                    story = it
                    getView().showStory(it)
                }, {
                    getView().showError(it.toString())
                })
    }

    fun onViewStoryButtonClicked() {
        if (story?.url != null) {
            getView().showUri(Uri.parse(story!!.url))
        }
    }
}