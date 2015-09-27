package com.dgsd.android.hackernews.mvp.presenter

import android.net.Uri
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.model.Story
import timber.log.Timber
import javax.inject.Inject

public class StoryPresenter(view: StoryMvpView, val component: AppServicesComponent, val storyId: Long) : Presenter<StoryMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    private var story: Story? = null

    init {
        component.inject(this)
    }

    fun onRefreshRequested() {
        loadStory(true)
    }

    override fun onStart() {
        super.onStart()
        loadStory(false)
    }

    fun onViewStoryButtonClicked() {
        if (!story?.url.isNullOrBlank()) {
            getView().showUri(Uri.parse(story!!.url))
        }
    }

    fun onCommentPlaceholderClicked(commentIds: List<Long>) {
        getView().showPlaceholderAsLoading(commentIds, true)
        dataSource.getComments(storyId, commentIds.toLongArray())
                .flatMap { dataSource.getStory(storyId) }
                .bind(getView())
                .onIoThread()
                .subscribe({
                    story = it
                    getView().showStory(it)
                    getView().showPlaceholderAsLoading(commentIds, false)
                }, {
                    // TODO: Proper error messages..
                    Timber.e(it, "Error getting comments")
                    getView().showError(it.toString())

                    getView().showPlaceholderAsLoading(commentIds, false)
                })
    }

    private fun loadStory(skipCache: Boolean) {
        dataSource.getStory(storyId, skipCache)
                .bind(getView())
                .onIoThread()
                .subscribe({
                    story = it
                    getView().showStory(it)

                    getView().setViewStoryButtonVisible(!story?.url.isNullOrBlank())
                }, {
                    // TODO: Proper error messages..
                    Timber.e(it, "Error getting story")
                    getView().showError(it.toString())
                })
    }

}
