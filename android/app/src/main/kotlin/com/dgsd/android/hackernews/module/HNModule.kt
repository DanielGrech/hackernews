package com.dgsd.android.hackernews.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dgsd.android.hackernews.BuildConfig
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.networkDataSource
import com.lacronicus.easydatastorelib.DatastoreBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module to provide dependency injection
 */
@SuppressWarnings("UNUSED_PARAMETER")
@Module
public class HNModule(private val application: HNApp) {

    @Provides
    @Singleton
    fun providesApp(): HNApp {
        return application
    }

    @Provides
    fun providesContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun providesAppSettings(sharedPreferences: SharedPreferences): AppSettings {
        return DatastoreBuilder(sharedPreferences).create(AppSettings::class.java)
    }

    @Provides
    @Singleton
    fun providesDataSource() : DataSource {
        return networkDataSource {
            logging = BuildConfig.DEBUG
            endpoint = BuildConfig.API_SERVER
        }
    }
}
