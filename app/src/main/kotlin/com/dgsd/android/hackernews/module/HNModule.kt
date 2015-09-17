package com.dgsd.android.hackernews.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dgsd.android.hackernews.BuildConfig
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.HNAppImpl
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.hackernews.DataSource
import com.dgsd.hackernews.networkDataSource
import com.lacronicus.easydatastorelib.DatastoreBuilder
import dagger.Module
import dagger.Provides
import retrofit.RestAdapter
import javax.inject.Singleton

/**
 * Dagger module to provide dependency injection
 */
SuppressWarnings("UNUSED_PARAMETER")
Module
public class HNModule(private val application: HNApp) {

    Provides
    Singleton
    fun providesApp(): HNApp {
        return application
    }

    Provides
    fun providesContext(): Context {
        return application
    }

    Provides
    Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    Provides
    Singleton
    fun providesAppSettings(sharedPreferences: SharedPreferences): AppSettings {
        return DatastoreBuilder(sharedPreferences).create(javaClass<AppSettings>())
    }

    Provides
    Singleton
    fun providesDataSource() : DataSource {
        return networkDataSource {
            logging = BuildConfig.DEBUG
            endpoint = "http://myawesomeserver.com"
            networkInterceptors = listOf()
        }
    }
}
