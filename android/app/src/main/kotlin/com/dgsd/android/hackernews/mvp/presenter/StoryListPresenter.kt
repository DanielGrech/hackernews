package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import com.dgsd.android.hackernews.mvp.view.StoryListMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import com.dgsd.hackernews.network.DataSource
import timber.log.Timber
import javax.inject.Inject

public class StoryListPresenter(view : StoryListMvpView, component : AppServicesComponent) : Presenter<StoryListMvpView>(view, component) {

    @Inject
    lateinit val dataSource: DataSource

    init {
        component.inject(this)
    }

    override public fun onResume() {
        super.onResume()

        getView().showLoading()
        dataSource.getTopStories()
                .bind(getView())
                .onIoThread()
                .subscribe({
                    if (it.isEmpty()) {
                        getView().showEmptyMessage(getContext().getString(R.string.empty_stories))
                    } else {
                        getView().showStories(it)
                    }
                }, {
                    Timber.e(it, "Error getting top stories!")
                    // TODO: Show ephemeral error (or full screen error if none visible)
                })
    }

}
