package com.dgsd.android.hackernews.mvp.view

import com.dgsd.hackernews.model.Story

public interface UrlInterceptMvpView : MvpView {

    fun showStory(story: Story, originalItemId: Long)

    fun showError(message: String)

    fun exit()
}
