package com.dgsd.android.hackernews.util

import com.dgsd.android.hackernews.HNTestRunner
import com.dgsd.android.hackernews.activity.UrlInterceptActivity
import com.dgsd.android.hackernews.fragment.BaseFragment
import com.dgsd.android.hackernews.mvp.view.MvpView
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import rx.Observable
import rx.observers.TestSubscriber
import java.util.concurrent.TimeUnit


@RunWith(HNTestRunner::class)
public class RxUtilsTest {

    @Test
    public fun testBindActivity() {
        val observable = Observable.just("").delay(2, TimeUnit.SECONDS)
        val subscriber = TestSubscriber<String>()

        val controller = Robolectric.buildActivity(UrlInterceptActivity::class.java)

        controller.create()
        controller.resume()

        observable.bind(controller.get() as MvpView).subscribe(subscriber)
        assertThat(subscriber.isUnsubscribed).isFalse()

        controller.pause()
        assertThat(subscriber.isUnsubscribed).isTrue()
    }

    @Test
    @Throws(InterruptedException::class)
    public fun testBindFragment() {
        val observable = Observable.just("").delay(2, TimeUnit.SECONDS)
        val subscriber = TestSubscriber<String>()

        val frag = DummyFragment()

        SupportFragmentTestUtil.startFragment(frag)

        frag.onResume()
        observable.bind(frag as MvpView).subscribe(subscriber)
        assertThat(subscriber.isUnsubscribed).isFalse()

        frag.onPause()
        assertThat(subscriber.isUnsubscribed).isTrue()
    }

    private class DummyFragment : BaseFragment(), MvpView {
        override fun getLayoutId(): Int {
            return 0
        }
    }
}
