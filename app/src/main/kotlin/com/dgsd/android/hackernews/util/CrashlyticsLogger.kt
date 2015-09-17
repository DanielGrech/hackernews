package com.dgsd.android.hackernews.util

import com.crashlytics.android.Crashlytics
import com.dgsd.android.hackernews.BuildConfig
import timber.log.Timber

public class CrashlyticsLogger : Timber.Tree() {

    init {
        Crashlytics.setString("GIT_SHA", BuildConfig.GIT_SHA)
        Crashlytics.setString("BUILD_NUMBER", BuildConfig.BUILD_NUMBER)
        Crashlytics.setString("BUILD_TIME", BuildConfig.BUILD_TIME)
    }

    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        if (t != null) {
            Crashlytics.logException(t)
        } else {
            Crashlytics.log(message)
        }
    }
}