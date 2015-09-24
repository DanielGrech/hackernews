package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.view.Menu
import android.view.MenuItem
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.StoryPresenter
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.CustomTabActivityHelper
import com.dgsd.android.hackernews.util.getSubtitleView
import com.dgsd.android.hackernews.util.getSummaryString
import com.dgsd.android.hackernews.util.getTitleView
import com.dgsd.hackernews.model.Story
import kotlinx.android.synthetic.act_story.storyText
import kotlinx.android.synthetic.act_story.toolbar
import kotlinx.android.synthetic.act_story.viewStoryButton
import org.jetbrains.anko.*
import timber.log.Timber

public class StoryActivity : PresentableActivity<StoryMvpView, StoryPresenter>(), StoryMvpView, CustomTabActivityHelper.ConnectionCallback {

    private lateinit var customTabActivityHelper: CustomTabActivityHelper

    companion object {

        private val EXTRA_STORY_ID = "_story_id"
        private val EXTRA_HINT_URL = "_hint_url"

        public fun getStartIntent(context: Context, story: Story): Intent {
            return Intent(context, StoryActivity::class.java)
                    .putExtra(EXTRA_STORY_ID, story.id)
                    .putExtra(EXTRA_HINT_URL, story.url)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.act_story, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.share -> {

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper.setConnectionCallback(this)
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
        toolbar.subtitle = story.getSummaryString(this)
        storyText.text = story.text

        val titleView = toolbar.getTitleView()
        titleView?.verticalPadding = dimen(R.dimen.padding_small)
        titleView?.singleLine = false

        val subtitleView = toolbar.getSubtitleView()
        subtitleView?.singleLine = false
        subtitleView?.bottomPadding = dimen(R.dimen.padding_small)
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

    override fun onCustomTabsDisconnected() {
        // No-op
    }

    override fun onCustomTabsConnected() {
        val hintUrl = intent.getStringExtra(EXTRA_HINT_URL)
        if (hintUrl != null) {
            customTabActivityHelper.mayLaunchUrl(Uri.parse(hintUrl), null, null)
        }
    }
}
