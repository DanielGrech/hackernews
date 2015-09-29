package com.dgsd.hackernews.network.utils

import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

public class RxUtilsTest {

    @Test
    public fun testNullFilterWithNullInput() {
        val testSubscriber = TestSubscriber<Any>()

        Observable.just<Any>(null).filterNulls().subscribe(testSubscriber)

        testSubscriber.assertNoValues()
        testSubscriber.assertTerminalEvent()
    }

    @Test
    public fun testNullFilterWithNonNullInput() {
        val EXPECTED_OUTPUT = Object()

        val testSubscriber = TestSubscriber<Any>()
        Observable.just<Any>(EXPECTED_OUTPUT).filterNulls().subscribe(testSubscriber)

        testSubscriber.assertValue(EXPECTED_OUTPUT)
        testSubscriber.assertTerminalEvent()
    }

    @Test
    public fun testFlatMapList() {
        val listInput = listOf(123, 456, 789)

        val testSubscriber = TestSubscriber<Int>()
        Observable.just(listInput).flatMapList().subscribe(testSubscriber)

        testSubscriber.assertValues(123, 456, 789)
    }
}
