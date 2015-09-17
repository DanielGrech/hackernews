package com.dgsd.android.hackernews.fragment

import android.os.Bundle
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.Presenter
import com.dgsd.android.hackernews.mvp.view.MvpView

/**
 * Base class for all fragments which have a corresponding [Presenter] object

 * @param T The type of presenter the fragment uses
 */
public abstract class PresentableFragment<V : MvpView, T : Presenter<V>> : BaseFragment() {

    private lateinit var presenter : T

    protected abstract fun createPresenter(servicesComponent: AppServicesComponent, savedInstanceState: Bundle?): T

    override public fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.presenter = createPresenter(getApp().getAppServicesComponent(), savedInstanceState)
        presenter.onCreate(savedInstanceState)
    }

    override public fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override public fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override public fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override public fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override public fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override public fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}