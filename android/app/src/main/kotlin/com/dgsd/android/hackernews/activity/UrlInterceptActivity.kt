package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.UrlInterceptPresenter
import com.dgsd.android.hackernews.mvp.view.UrlInterceptMvpView
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.toast

public class UrlInterceptActivity : PresentableActivity<UrlInterceptMvpView, UrlInterceptPresenter>(), UrlInterceptMvpView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)
        presenter.onLinkRequested(intent.data)
    }

    override fun getLayoutResource(): Int {
        return R.layout.act_url_intercept
    }

    override fun createPresenter(component: AppServicesComponent): UrlInterceptPresenter {
        return UrlInterceptPresenter(this, component)
    }

    override fun getContext(): Context {
        return this
    }

    override fun showError(message: String) {
        toast(message)
    }

    override fun exit() {
        finish()
    }

    override fun showStory(story: Story, originalItemId: Long) {
        val flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        if (story.id == originalItemId) {
            // We requested a story, show it no problems.
            startActivity(StoryActivity.getStartIntent(this, story).addFlags(flags))
        } else {
            startActivity(StoryActivity.getStartIntent(this, story, originalItemId).addFlags(flags))
        }

        exit()
    }
}