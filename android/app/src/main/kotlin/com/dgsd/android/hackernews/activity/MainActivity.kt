package com.dgsd.android.hackernews.activity

import android.content.Context
import android.os.Bundle
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.MainPresenter
import com.dgsd.android.hackernews.mvp.view.MainMvpView

public class MainActivity : PresentableActivity<MainMvpView, MainPresenter>(), MainMvpView {

    override fun getLayoutResource(): Int {
        return 0
    }

    override fun createPresenter(component: AppServicesComponent): MainPresenter {
        return MainPresenter(this, component)
    }

    override fun getContext(): Context {
        return this
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super<PresentableActivity>.onCreate(savedInstanceState)

    }

}
