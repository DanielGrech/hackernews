package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.StoryPresenter
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.CustomTabActivityHelper
import com.dgsd.hackernews.model.Story
import kotlinx.android.synthetic.act_story.storyText
import kotlinx.android.synthetic.act_story.toolbar
import kotlinx.android.synthetic.act_story.viewStoryButton
import org.jetbrains.anko.browse
import org.jetbrains.anko.onClick
import timber.log.Timber

public class StoryActivity : PresentableActivity<StoryMvpView, StoryPresenter>(), StoryMvpView {

    private lateinit var customTabActivityHelper: CustomTabActivityHelper

    companion object {

        private val EXTRA_STORY_ID = "_story_id"

        public fun getStartIntent(context: Context, story: Story): Intent {
            return Intent(context, StoryActivity::class.java)
                    .putExtra(EXTRA_STORY_ID, story.id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        with (supportActionBar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        customTabActivityHelper = CustomTabActivityHelper()

        viewStoryButton.onClick {
            presenter.onViewStoryButtonClicked()
        }
    }


    override fun onStart() {
        super.onStart()
        customTabActivityHelper.bindCustomTabsService(this)
    }

    override fun onStop() {
        customTabActivityHelper.unbindCustomTabsService(this)
        super.onStop()
    }

    override fun getLayoutResource(): Int {
        return R.layout.act_story
    }

    override fun createPresenter(component: AppServicesComponent): StoryPresenter {
        return StoryPresenter(this, component, intent.getLongExtra(EXTRA_STORY_ID, -1L))
    }

    override fun getContext(): Context {
        return this
    }

    override fun showError(message: String) {
        Timber.d("SHOW ERROR: " + message)
    }

    override fun showStory(story: Story) {
        toolbar.title = story.title
        storyText.text = story.text
    }

    override fun showUri(uri: Uri) {
        val customTabIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(getColor(R.color.primary))
                .build()

        CustomTabActivityHelper.openCustomTab(this, customTabIntent, uri) { activity, uri ->
            activity.browse(uri.toString())
        }
    }
}
