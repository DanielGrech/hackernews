package com.dgsd.android.hackernews.module

import com.dgsd.android.hackernews.analytics.Tracker
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.mvp.presenter.StoryListPresenter
import com.dgsd.android.hackernews.mvp.presenter.StoryPresenter
import com.dgsd.android.hackernews.mvp.presenter.UrlInterceptPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = arrayOf(HNModule::class))
public interface AppServicesComponent {

    fun inject(presenter: StoryListPresenter)

    fun inject(presenter: UrlInterceptPresenter)

    fun inject(presenter: StoryPresenter)

    fun dataSource(): HNDataSource

    fun analytics(): Tracker

    fun appSettings(): AppSettings
}