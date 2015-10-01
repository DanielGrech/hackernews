package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.StoryListMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.getShareLink
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.model.Story
import rx.Observable
import timber.log.Timber
import javax.inject.Inject

public class StoryListPresenter(view : StoryListMvpView, component : AppServicesComponent,
                                val pageType: PageType) : Presenter<StoryListMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    @Inject
    lateinit val appSettings: AppSettings

    var hasLoadedStories = false

    init {
        component.inject(this)
    }

    override fun getScreenName(): String {
        return "story_list_${pageType.name().toLowerCase()}"
    }

    override public fun onResume() {
        super.onResume()
        getView().showLoading()
        getStories(false)
    }

    fun onStoryClicked(story: Story): Boolean {
        analytics.trackClick("story")
        return appSettings.showLinksFirst().get(false)
    }

    fun onRefreshRequested() {
        analytics.trackScreenView("${pageType.name().toLowerCase()}_story_list")
        getStories(true)
    }

    private fun getStories(skipCache: Boolean) {
        // For materialize/dematerialize nonsense, see https://github.com/ReactiveX/RxJava/issues/2887
        getStoryObservable(skipCache)
                .bind(getView())
                .materialize()
                .onIoThread()
                .dematerialize<List<Story>>()
                .subscribe({
                    if (it.isEmpty()) {
                        hasLoadedStories = false
                        getView().showEmptyMessage(getContext().getString(R.string.empty_stories))
                    } else {
                        hasLoadedStories = true
                        getView().showStories(it)
                    }
                }, {
                    Timber.e(it, "Error loading stories")
                    if (hasLoadedStories) {
                        getView().showError(getContext().getString(R.string.error_retrieving_stories_ephemeral))
                    } else {
                        getView().showError(getContext().getString(R.string.error_retrieving_stories))
                    }
                })
    }

    private fun getStoryObservable(skipCache: Boolean): Observable<List<Story>> {
        return when (pageType) {
            PageType.TOP -> dataSource.getTopStories(skipCache)
            PageType.NEW -> dataSource.getNewStories(skipCache)
            PageType.ASK_HN -> dataSource.getAskStories(skipCache)
            PageType.SHOW_HN -> dataSource.getShowStories(skipCache)
            PageType.JOBS -> dataSource.getJobStories(skipCache)
        }
    }

    fun onShareStoryLink(story: Story) {
        analytics.trackClick("share_link")

        val url = story.url
        if (!url.isNullOrBlank()) {
            getView().shareUrl(url!!)
        }
    }

    fun onShareStoryCommentLink(story: Story) {
        analytics.trackClick("share_comments")

        val url = story.getShareLink()
        if (!url.isNullOrBlank()) {
            getView().shareUrl(url!!)
        }
    }
}
