package com.dgsd.android.hackernews.activity

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import com.dgsd.android.hackernews.HNAppImpl
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

public abstract class BaseActivity : RxActivity() {

    private lateinit var app: HNAppImpl

    /**
     * @return the layout resource to use for this activity,
     * or a value <= 0 if no layout should be used
     */
    LayoutRes protected abstract fun getLayoutResource(): Int

    override protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = applicationContext as HNAppImpl

        val layoutResId = getLayoutResource()
        if (layoutResId > 0) {
            setContentView(layoutResId)
        }
    }

    protected fun getApp() : HNAppImpl {
        return app
    }
}

