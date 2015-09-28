package com.dgsd.android.hackernews.mvp.presenter

import android.net.Uri
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.getShareLink
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

    override fun getScreenName(): String {
        return "story"
    }

    fun onRefreshRequested() {
        analytics.trackSwipeRefresh("comment_list")
        loadStory(true)
    }

    override fun onStart() {
        super.onStart()
        getView().showLoading()
        loadStory(false)
    }

    fun onViewStoryButtonClicked() {
        analytics.trackClick("view_story")

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
                    onStoryLoaded(it)
                    getView().showPlaceholderAsLoading(commentIds, false)
                }, {
                    Timber.e(it, "Error getting comments")
                    getView().showEphemeralError(getContext().getString(R.string.error_retrieving_comments_ephemeral))
                    getView().showPlaceholderAsLoading(commentIds, false)
                })
    }

    private fun loadStory(skipCache: Boolean) {
        dataSource.getStory(storyId, skipCache)
                .bind(getView())
                .onIoThread()
                .subscribe({
                    onStoryLoaded(it)
                }, {
                    Timber.e(it, "Error getting story")
                    if (this.story?.hasComments() ?:false) {
                        getView().showEphemeralError(getContext().getString(R.string.error_retrieving_comments_ephemeral))
                    } else {
                        getView().showError(getContext().getString(R.string.error_retrieving_comments))
                    }
                })
    }

    private fun onStoryLoaded(story: Story) {
        this.story = story
        getView().showStory(story)
        getView().setViewStoryButtonVisible(!story.url.isNullOrBlank())

        if (story.hasComments()) {
            if (story.hasCommentsToLoad()) {
                onCommentPlaceholderClicked(story.commentIds)
            }
        } else {
            val msgRes = if (story.text.isNullOrBlank()) R.string.comment_list_no_comments_with_retry else R.string.comment_list_no_comments
            getView().showNoCommentsMessage(getContext().getString(msgRes))
        }

    }

    fun getNfcShareLink(): String? {
        return story?.getShareLink()
    }

    fun onShareLinkClicked() {
        analytics.trackClick("share_link")

        val url = story?.url
        if (url.isNullOrBlank()) {
            getView().showEphemeralError(getContext().getString(R.string.error_sharing_link))
        } else {
            getView().shareUrl(url!!)
        }
    }

    fun onShareCommentsClicked() {
        analytics.trackClick("share_comments")

        val url = story?.getShareLink()
        if (url.isNullOrBlank()) {
            getView().showEphemeralError(getContext().getString(R.string.error_sharing_comments))
        } else {
            getView().shareUrl(url!!)
        }
    }

    fun canShareLink(): Boolean {
        return !story?.url.isNullOrBlank()
    }

    fun canShareCommens(): Boolean {
        return !story?.getShareLink().isNullOrBlank()
    }
}
