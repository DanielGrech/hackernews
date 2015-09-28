package com.dgsd.android.hackernews.mvp.presenter

import android.net.Uri
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.UrlInterceptMvpView
import com.dgsd.android.hackernews.util.bind
import com.dgsd.android.hackernews.util.onIoThread
import rx.lang.kotlin.firstOrNull
import timber.log.Timber
import javax.inject.Inject

public class UrlInterceptPresenter(view: UrlInterceptMvpView, component: AppServicesComponent) : Presenter<UrlInterceptMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    init {
        component.inject(this)
    }

    fun onLinkRequested(requestedUri: Uri?) {
        try {
            val itemId = requestedUri?.getQueryParameter("id")?.toLong() ?: -1L
            if (itemId > 0) {
                Timber.d("Got id: %s", itemId)
                loadItem(itemId)
                return
            }
        } catch(ex: NumberFormatException) {
            Timber.w(ex, "Error getting item id")
            // Fallthrough..
        }

        exitWithError()
    }


    private fun loadItem(itemId: Long) {
        dataSource.getStory(itemId, false)
                .onErrorResumeNext {
                    // Couldn't get item as a story .. try as a comment
                    dataSource.getStoryByCommentId(itemId)
                }
                .first()
                .bind(getView())
                .onIoThread()
                .subscribe({
                    Timber.d("Got story: $it")
                    getView().showStory(it, itemId)
                }, {
                    Timber.e(it, "Error getting item $itemId")
                    exitWithError()
                })

    }

    private fun exitWithError() {
        getView().showError(getContext().getString(R.string.error_opening_link))
        getView().exit()
    }
}
