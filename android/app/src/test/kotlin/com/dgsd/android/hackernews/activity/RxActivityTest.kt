package com.dgsd.android.hackernews.activity

import com.dgsd.android.hackernews.HNTestRunner
import com.trello.rxlifecycle.ActivityEvent

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.util.ActivityController

import rx.observers.TestSubscriber

@RunWith(HNTestRunner::class)
public class RxActivityTest {

    @Test
    public fun testLifecycleObservable() {
        val controller = Robolectric.buildActivity(LameRxActivity::class.java)

        val subscriber = TestSubscriber<ActivityEvent>()

        controller.get().lifecycle().subscribe(subscriber)

        controller.create().start().resume().pause().stop().destroy()

        subscriber.assertValues(
                ActivityEvent.CREATE,
                ActivityEvent.START,
                ActivityEvent.RESUME,
                ActivityEvent.PAUSE,
                ActivityEvent.STOP,
                ActivityEvent.DESTROY
        )
    }

    /**
     * RxActivity is an abstract class, so we need a concrete implementation here..
     */
    public class LameRxActivity : RxActivity()
}