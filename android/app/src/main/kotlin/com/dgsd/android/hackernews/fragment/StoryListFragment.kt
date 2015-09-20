package com.dgsd.android.hackernews.fragment

import android.os.Bundle
import android.view.View
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.StoryListPresenter
import com.dgsd.android.hackernews.mvp.view.StoryListMvpView
import com.dgsd.android.hackernews.view.LceViewGroup
import com.dgsd.android.hackernews.view.StoryRecyclerView
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import timber.log.Timber

public class StoryListFragment: PresentableFragment<StoryListMvpView, StoryListPresenter>(), StoryListMvpView {

    private lateinit var loadingContentErrorView: LceViewGroup

    private lateinit var recyclerView: StoryRecyclerView

    companion object {

        public fun newInstance(): StoryListFragment {
            return StoryListFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_story_list
    }

    override fun createPresenter(servicesComponent: AppServicesComponent, savedInstanceState: Bundle?): StoryListPresenter {
        return StoryListPresenter(this, servicesComponent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingContentErrorView = view.find(R.id.loadingContentErrorView)
        recyclerView = view.find(R.id.recyclerView)
        recyclerView.setOnStoryClickListener { story, view ->
            // TODO: Open story!
            Timber.d("Clicked on %s", story)
        }
    }

    override fun showStories(stories: List<Story>) {
        recyclerView.setStories(stories)
        loadingContentErrorView.showContent()
    }

    override fun showEmptyMessage(message: String) {
        loadingContentErrorView.showError(message)
    }

    override fun showLoading() {
        loadingContentErrorView.showLoading()
    }
}