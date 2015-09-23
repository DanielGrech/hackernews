package com.dgsd.android.hackernews.mvp.view

import com.dgsd.hackernews.model.Story

public interface StoryListMvpView : MvpView {

    fun showStories(stories: List<Story>)

    fun showEmptyMessage(message: String)

    fun showError(message: String)

    fun showLoading()
}
