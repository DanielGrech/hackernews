package com.dgsd.android.hackernews.mvp.presenter

import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.view.MainMvpView

public class MainPresenter(view : MainMvpView, component : AppServicesComponent) : Presenter<MainMvpView>(view, component) {

    init {
        component.inject(this)
    }


}
