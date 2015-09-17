package com.dgsd.android.hackernews.module

import com.dgsd.android.hackernews.mvp.presenter.MainPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component to provide dependency injection
 */
Singleton
Component(modules = arrayOf(HNModule::class))
public interface AppServicesComponent {

    fun inject(presenter: MainPresenter)

}