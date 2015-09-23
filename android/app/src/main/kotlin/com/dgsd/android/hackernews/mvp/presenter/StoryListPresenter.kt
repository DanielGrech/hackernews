package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import com.dgsd.android.hackernews.mvp.view.StoryListMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DataSource
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import timber.log.Timber
import javax.inject.Inject

public class StoryListPresenter(view : StoryListMvpView, component : AppServicesComponent,
                                val pageType: PageType) : Presenter<StoryListMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    init {
        component.inject(this)
    }

    override public fun onResume() {
        super.onResume()
        getView().showLoading()
        getStories(false)
    }

    fun onRefreshRequested() {
        getStories(true)
    }

    private fun getStories(skipCache: Boolean) {
        getStoryObservable(skipCache)
                .bind(getView())
                .onIoThread()
                .subscribe({
                    if (it.isEmpty()) {
                        getView().showEmptyMessage(getContext().getString(R.string.empty_stories))
                    } else {
                        getView().showStories(it)
                    }
                }, {
                    Timber.e(it, "Error showing stories")

                    if (skipCache) {
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

}
