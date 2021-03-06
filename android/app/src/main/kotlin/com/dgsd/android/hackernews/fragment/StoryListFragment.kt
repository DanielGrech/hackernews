package com.dgsd.android.hackernews.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.activity.StoryActivity
import com.dgsd.android.hackernews.model.PageType
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.StoryListPresenter
import com.dgsd.android.hackernews.mvp.view.StoryListMvpView
import com.dgsd.android.hackernews.util.startActivity
import com.dgsd.android.hackernews.view.LceViewGroup
import com.dgsd.android.hackernews.view.StoryRecyclerView
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.share

public class StoryListFragment : PresentableFragment<StoryListMvpView, StoryListPresenter>(), StoryListMvpView {

    private lateinit var loadingContentErrorView: LceViewGroup

    private lateinit var recyclerView: StoryRecyclerView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {

        private val KEY_PAGE_TYPE = "_page_type"

        public fun newInstance(type: PageType): StoryListFragment {
            val args = Bundle()
            args.putInt(KEY_PAGE_TYPE, type.ordinal())

            val frag = StoryListFragment()
            frag.arguments = args
            return frag
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_story_list
    }

    override fun createPresenter(servicesComponent: AppServicesComponent, savedInstanceState: Bundle?): StoryListPresenter {
        val ordinal = arguments?.getInt(KEY_PAGE_TYPE, -1) ?: -1
        val pageType = PageType.values().elementAtOrNull(ordinal) ?: throw IllegalStateException("No page type found!")
        return StoryListPresenter(this, servicesComponent, pageType)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingContentErrorView = view.find(R.id.loadingContentErrorView)
        recyclerView = view.find(R.id.recyclerView)
        swipeRefreshLayout = view.find(R.id.swipeRefreshLayout)

        with (recyclerView) {
            setOnStoryClickListener { story, view ->
                val showLinkDirectly = presenter.onStoryClicked(story)
                view.startActivity(StoryActivity.getStartIntent(activity, story, showStoryImmediately = showLinkDirectly))
            }

            setOnShareStoryLinkListener {
                presenter.onShareStoryLink(it)
            }

            setOnShareStoryCommentLinkListener {
                presenter.onShareStoryCommentLink(it)
            }
        }

        with (swipeRefreshLayout) {
            setColorSchemeResources(R.color.accent)
            setOnRefreshListener {
                presenter.onRefreshRequested()
            }
        }
    }

    override fun showStories(stories: List<Story>) {
        swipeRefreshLayout.isRefreshing = false
        recyclerView.setStories(stories)
        loadingContentErrorView.showContent()
    }

    override fun showEmptyMessage(message: String) {
        swipeRefreshLayout.isRefreshing = false
        loadingContentErrorView.showError(message)
        loadingContentErrorView.errorMessage.setOnClickListener(null)
    }

    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = false
        loadingContentErrorView.showLoading()
    }

    override fun showError(message: String) {
        swipeRefreshLayout.isRefreshing = false

        if (recyclerView.adapter.itemCount == 0) {
            showEmptyMessage(message)
            loadingContentErrorView.errorMessage.onClick {
                showLoading()
                presenter.onRefreshRequested()
            }
        } else {
            loadingContentErrorView.showContent()

            if (userVisibleHint) {
                // Only show snackbar if the fragment is actually visible
                Snackbar.make(loadingContentErrorView, message, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.retry) {
                            swipeRefreshLayout.isRefreshing = true
                            presenter.onRefreshRequested()
                        }
                        .show()
            }
        }
    }

    override fun shareUrl(url: String) {
        context.share(url)
    }
}