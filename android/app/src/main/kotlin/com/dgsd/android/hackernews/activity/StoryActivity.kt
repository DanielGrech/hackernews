package com.dgsd.android.hackernews.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.StoryPresenter
import com.dgsd.android.hackernews.mvp.view.StoryMvpView
import com.dgsd.android.hackernews.util.*
import com.dgsd.android.hackernews.view.CommentRecyclerView
import com.dgsd.android.hackernews.view.LceViewGroup
import com.dgsd.hackernews.model.Story
import kotlinx.android.synthetic.act_story.swipeRefreshLayout
import kotlinx.android.synthetic.act_story.toolbar
import kotlinx.android.synthetic.act_story.viewStoryButton
import org.jetbrains.anko.*

public class StoryActivity : PresentableActivity<StoryMvpView, StoryPresenter>(),
        StoryMvpView, CustomTabActivityHelper.ConnectionCallback, NfcAdapter.CreateNdefMessageCallback {

    private lateinit var customTabActivityHelper: CustomTabActivityHelper

    private lateinit var recyclerView: CommentRecyclerView

    private lateinit var loadingContentErrorView: LceViewGroup

    private var commentIdToScrollTo: Long? = null

    companion object {

        private val EXTRA_STORY_ID = "_story_id"
        private val EXTRA_SCROLL_TO_COMMENT = "_scroll_to_comment"
        private val EXTRA_HINT_URL = "_hint_url"
        private val EXTRA_SHOW_STORY = "_show_story"

        public fun getStartIntent(context: Context, story: Story, commentToShow: Long = -1, showStoryImmediately: Boolean = false): Intent {
            return Intent(context, StoryActivity::class.java)
                    .putExtra(EXTRA_STORY_ID, story.id)
                    .putExtra(EXTRA_SCROLL_TO_COMMENT, commentToShow)
                    .putExtra(EXTRA_HINT_URL, story.url)
                    .putExtra(EXTRA_SHOW_STORY, showStoryImmediately)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        commentIdToScrollTo = intent.getLongExtra(EXTRA_SCROLL_TO_COMMENT, -1)
        if (commentIdToScrollTo!! < 0) {
            commentIdToScrollTo = null
        }

        with (supportActionBar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        customTabActivityHelper = CustomTabActivityHelper()

        viewStoryButton.onClick {
            presenter.onViewStoryButtonClicked()
        }

        loadingContentErrorView = find(R.id.loadingContentErrorView)
        recyclerView = find(R.id.recyclerView)

        recyclerView.setOnShareCommentLinkListener { comment ->
            presenter.onShareCommentClicked(comment)
        }

        recyclerView.setOnCommentPlaceholderClickListener { ids, view ->
            presenter.onCommentPlaceholderClicked(ids)
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.accent)
        swipeRefreshLayout.setOnRefreshListener {
            presenter.onRefreshRequested()
        }

        loadingContentErrorView.errorMessage.onClick {
            showLoading()
            presenter.onRefreshRequested()
        }

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            val nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter != null && nfcAdapter.isEnabled) {
                nfcAdapter.setNdefPushMessageCallback(this, this);
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.act_story, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val shareLinkItem = menu.findItem(R.id.share_link)
        val shareCommentsItem = menu.findItem(R.id.share_comments)

        with (presenter.canShareLink()) {
            shareLinkItem.setEnabled(this)
            shareLinkItem.setVisible(this)
        }

        with (presenter.canShareComments()) {
            shareCommentsItem.setEnabled(this)
            shareCommentsItem.setVisible(this)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.share_link) {
            presenter.onShareLinkClicked()
            true
        } else if (item.itemId == R.id.share_comments) {
            presenter.onShareCommentsClicked()
            true
        } else if (item.itemId == android.R.id.home) {
            startActivity(MainActivity.getStartIntent(this))
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper.setConnectionCallback(this)
        customTabActivityHelper.bindCustomTabsService(this)
    }

    override fun onStop() {
        customTabActivityHelper.setConnectionCallback(null)
        customTabActivityHelper.unbindCustomTabsService(this)
        super.onStop()
    }

    override fun getLayoutResource(): Int {
        return R.layout.act_story
    }

    override fun createPresenter(component: AppServicesComponent): StoryPresenter {
        return StoryPresenter(this, component,
                intent.getLongExtra(EXTRA_STORY_ID, -1L), intent.getBooleanExtra(EXTRA_SHOW_STORY, false))
    }

    override fun getContext(): Context {
        return this
    }

    override fun showPlaceholderAsLoading(commentIds: List<Long>, showLoading: Boolean) {
        recyclerView.setCommentPlaceholderLoading(commentIds, showLoading)
    }

    override fun showLoading() {
        loadingContentErrorView.showLoading()
    }

    override fun showError(message: String) {
        swipeRefreshLayout.isRefreshing = false
        loadingContentErrorView.showError(message)
    }

    override fun showEphemeralError(message: String) {
        swipeRefreshLayout.isRefreshing = false
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showStory(story: Story) {
        loadingContentErrorView.showContent()
        swipeRefreshLayout.isRefreshing = false

        this.title = story.title
        toolbar.title = story.title
        toolbar.subtitle = story.getSummaryString(this)

        recyclerView.setStory(story)

        val titleView = toolbar.getTitleView()
        titleView?.verticalPadding = dimen(R.dimen.padding_small)
        titleView?.singleLine = false

        val subtitleView = toolbar.getSubtitleView()
        subtitleView?.singleLine = false
        subtitleView?.bottomPadding = dimen(R.dimen.padding_small)

        if (commentIdToScrollTo != null) {
            recyclerView.scrollToComment(commentIdToScrollTo!!)
            commentIdToScrollTo = null
        }

        supportInvalidateOptionsMenu()
    }

    override fun showUri(uri: Uri) {
        swipeRefreshLayout.isRefreshing = false

        val customTabIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(resources.getColor(R.color.primary))
                .build()

        CustomTabActivityHelper.openCustomTab(this, customTabIntent, uri) { activity, uri ->
            activity.browse(uri.toString())
        }
    }

    override fun showNoCommentsMessage(message: String) {
        if (recyclerView.adapter.itemCount == 0) {
            showError(message)
        } else {
            recyclerView.showNoCommentsMessage(message)
        }
    }

    override fun setViewStoryButtonVisible(isVisible: Boolean) {
        viewStoryButton.showWhen(isVisible)
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

    override fun createNdefMessage(event: NfcEvent?): NdefMessage? {
        val shareLink = presenter.getNfcShareLink()
        if (shareLink == null) {
            return null
        } else {
            return NdefMessage(arrayOf(NdefRecord.createUri(shareLink)))
        }
    }

    override fun shareUrl(url: String) {
        share(url)
    }
}
