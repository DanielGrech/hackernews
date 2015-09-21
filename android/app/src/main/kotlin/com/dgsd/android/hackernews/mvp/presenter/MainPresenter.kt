package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import javax.inject.Inject

public class MainPresenter(view : MainMvpView, component : AppServicesComponent) : Presenter<MainMvpView>(view, component) {

    @Inject
    lateinit val dataSource: HNDataSource

    init {
        component.inject(this)
    }

}
