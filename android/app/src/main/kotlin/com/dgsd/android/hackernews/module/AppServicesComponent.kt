package com.dgsd.android.hackernews.module

import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.mvp.presenter.MainPresenter
import com.dgsd.android.hackernews.mvp.presenter.StoryListPresenter
import com.dgsd.android.hackernews.mvp.presenter.StoryPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = arrayOf(HNModule::class))
public interface AppServicesComponent {

    fun inject(presenter: MainPresenter)

    fun inject(presenter: StoryListPresenter)

    fun inject(presenter: StoryPresenter)

    fun dataSource(): HNDataSource

}