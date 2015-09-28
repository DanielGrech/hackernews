package com.dgsd.android.hackernews

import android.app.Application
import android.content.ComponentCallbacks2
import android.os.StrictMode
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.dgsd.android.hackernews.module.AppServicesComponent
import com.dgsd.android.hackernews.module.HNModule
import com.dgsd.android.hackernews.util.Api
import com.dgsd.android.hackernews.util.CrashlyticsLogger
import com.dgsd.android.hackernews.util.LoggingLifecycleCallbacks
import com.dgsd.android.hackernews.util.clearHtmlContentCache
import com.facebook.stetho.Stetho
import com.facebook.stetho.timber.StethoTree
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.fabric.sdk.android.Fabric
import rx.Observable
import rx.lang.kotlin.deferredObservable
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class HNApp : Application() {

    var refWatcher: RefWatcher = RefWatcher.DISABLED

    private lateinit var appServicesComponent: AppServicesComponent

    protected abstract fun createAppServicesComponent(): AppServicesComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            enableDebugTools()
        }

        enableAppOnlyFunctionality()

        appServicesComponent = createAppServicesComponent()

        clearOldAppData()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Timber.d("Android is suggesting to trim memoryLevel = %s", level)
            clearHtmlContentCache()

            appServicesComponent.dataSource().clearMemoryCache()
        }
    }

    /**
     * @return an [AppServicesComponent] which holds all the necessary dependencies
     * * other application components may want to use for injection purposes
     */
    public fun getAppServicesComponent(): AppServicesComponent {
        return appServicesComponent
    }

    protected fun getModule(): HNModule {
        return HNModule(this);
    }

    /**
     * Enables functionality only wanted in the actual app.
     *
     *
     * This allows overriding in tests/other modules
     */
    protected open fun enableAppOnlyFunctionality() {
        CustomActivityOnCrash.setShowErrorDetails(false)
        CustomActivityOnCrash.install(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics(), Answers());
            Timber.plant(CrashlyticsLogger())
        }
        registerActivityLifecycleCallbacks(LoggingLifecycleCallbacks())
    }

    protected open fun enableDebugTools() {
        Timber.plant(Timber.DebugTree())
        Timber.plant(StethoTree())

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build())

        StrictMode.enableDefaults()

        if (BuildConfig.LEAK_CANARY_ENABLED && Api.isUpTo(Api.LOLLIPOP)) {
            // LeakCanary causes a crash on M Developer Preview
            refWatcher = LeakCanary.install(this)
        }
    }

    protected fun clearOldAppData() {
        deferredObservable {
            Observable.just(appServicesComponent.dataSource().clearOldData())
        }.delay(2, TimeUnit.SECONDS, Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    Timber.d("Successfully cleared $it old data items")
                }, {
                    Timber.e(it, "Error clearing old data")
                })
    }
}
