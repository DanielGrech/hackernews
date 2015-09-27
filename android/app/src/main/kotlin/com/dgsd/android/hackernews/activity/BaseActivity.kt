package com.dgsd.android.hackernews.activity

import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v4.app.ActivityCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.MenuItem
import com.dgsd.android.hackernews.HNAppImpl
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.Api

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupTaskDescription()
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        setupTaskDescription()
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

    private fun setupTaskDescription() {
        if (Api.isMin(Api.LOLLIPOP)) {
            setTaskDescription(ActivityManager.TaskDescription(
                    title?.toString(),
                    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
                    getColor(R.color.primary))
            )
        }
    }
}

