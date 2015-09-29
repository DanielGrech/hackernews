package com.dgsd.android.hackernews.util

import android.view.View
import android.widget.TextView
import com.dgsd.android.hackernews.HNTestRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(HNTestRunner::class)
public class ViewUtilsTest {

    @Test
    public fun testHide() {
        val view = View(RuntimeEnvironment.application)
        view.visibility = View.VISIBLE

        view.hide()

        assertThat(view.visibility).isEqualTo(View.GONE)
    }

    @Test
    public fun testHideInvisibleHandlesOneViewInput() {
        val view = View(RuntimeEnvironment.application)
        view.visibility = View.VISIBLE

        view.hideInvisible()

        assertThat(view.visibility).isEqualTo(View.INVISIBLE)
    }


    @Test
    public fun testShowHandlesOneViewInput() {
        val view = View(RuntimeEnvironment.application)
        view.visibility = View.GONE

        view.show()

        assertThat(view.visibility).isEqualTo(View.VISIBLE)
    }

    @Test
    public fun testShowWhenTrue() {
        val view = View(RuntimeEnvironment.application)
        view.visibility = View.GONE

        view.showWhen(true)

        assertThat(view.visibility).isEqualTo(View.VISIBLE)
    }

    @Test
    public fun testShowWhenFalse() {
        val view = View(RuntimeEnvironment.application)
        view.visibility = View.VISIBLE

        view.showWhen(false)

        assertThat(view.visibility).isEqualTo(View.GONE)
    }
}