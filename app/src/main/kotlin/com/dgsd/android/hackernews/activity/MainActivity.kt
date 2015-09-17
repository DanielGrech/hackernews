package com.dgsd.android.hackernews.activity

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.MainPresenter
import com.dgsd.android.hackernews.mvp.view.MainMvpView
import org.jetbrains.anko.*

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

        verticalLayout {
            textView {
                text = "Hello, Kotlin!"
                gravity = Gravity.CENTER
            }.layoutParams(width = matchParent, height = matchParent)
        }
    }

}
