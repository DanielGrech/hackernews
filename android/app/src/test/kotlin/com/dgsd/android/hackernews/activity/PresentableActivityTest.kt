package com.dgsd.android.hackernews.activity

import android.os.Bundle
import com.dgsd.android.hackernews.HNTestRunner
import com.dgsd.android.hackernews.TestUtils
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.mvp.presenter.Presenter
import com.dgsd.android.hackernews.mvp.view.MvpView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.Robolectric.buildActivity

@RunWith(HNTestRunner::class)
public class PresentableActivityTest {

    @Test
    public fun testDelegatesToPresenter() {
        val controller = buildActivity(PresentableActivityWithMockPresenter::class.java)

        val presenter = controller.setup().get().presenter
        controller.saveInstanceState(mock(Bundle::class.java)).pause().stop().destroy().get()

        verify(presenter).onCreate(any(Bundle::class.java))
        verify(presenter).onStart()
        verify(presenter).onResume()
        verify(presenter).onSaveInstanceState(any(Bundle::class.java))
        verify(presenter).onPause()
        verify(presenter).onStop()
        verify(presenter).onDestroy()
    }

    open class PresentableActivityWithMockPresenter : PresentableActivity<DummyMvpView, DummyPresenter>() {
        protected override fun createPresenter(component: AppServicesComponent): DummyPresenter {
            return spy(DummyPresenter(TestUtils.createView(DummyMvpView::class.java), component))
        }

        protected override fun getLayoutResource(): Int {
            return 0
        }
    }

    interface DummyMvpView : MvpView {

    }

    open class DummyPresenter(view: DummyMvpView, component : AppServicesComponent) : Presenter<DummyMvpView>(view, component) {
        override fun getScreenName(): String {
            return "Dummy"
        }

        override fun onResume() {

        }
    }
}