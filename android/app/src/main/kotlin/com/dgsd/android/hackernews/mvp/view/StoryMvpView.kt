package com.dgsd.android.hackernews.mvp.view

import android.net.Uri
import com.dgsd.hackernews.model.Story

public interface StoryMvpView : MvpView {

    fun showStory(story: Story)

    fun showError(message: String)

    fun showUri(uri: Uri)

    fun setViewStoryButtonVisible(isVisible: Boolean)

    fun showPlaceholderAsLoading(commentIds: List<Long>, showLoading: Boolean)

}
