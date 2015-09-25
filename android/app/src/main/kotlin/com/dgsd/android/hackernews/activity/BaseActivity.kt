package com.dgsd.android.hackernews.activity

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.MenuItem
import com.dgsd.android.hackernews.HNAppImpl

public abstract class BaseActivity : RxActivity() {

    private lateinit var app: HNAppImpl

    /**
     * @return the layout resource to use for this activity,
     * or a value <= 0 if no layout should be used
     */
    @LayoutRes protected abstract fun getLayoutResource(): Int

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = applicationContext as HNAppImpl

        val layoutResId = getLayoutResource()
        if (layoutResId > 0) {
            setContentView(layoutResId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun getApp() : HNAppImpl {
        return app
    }
}

