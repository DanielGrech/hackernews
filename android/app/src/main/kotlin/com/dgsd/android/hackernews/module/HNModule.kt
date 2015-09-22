package com.dgsd.android.hackernews.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dgsd.android.hackernews.BuildConfig
import com.dgsd.android.hackernews.HNApp
import com.dgsd.android.hackernews.data.AppSettings
import com.dgsd.android.hackernews.data.HNDataSource
import com.dgsd.android.hackernews.data.DbOpenHelper
import com.dgsd.android.hackernews.data.DbProvider
import com.dgsd.hackernews.network.DataSource
import com.dgsd.hackernews.network.DbDataSource
import com.dgsd.hackernews.network.NetworkDataSource
import com.dgsd.hackernews.network.networkDataSource
import com.lacronicus.easydatastorelib.DatastoreBuilder
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import dagger.Module
import dagger.Provides
import timber.log.Timber
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
    fun providesDbOpenHelper(context: Context): DbOpenHelper {
        return DbOpenHelper(context.applicationContext)
    }

    Provides
    Singleton
    fun providesSqlBrite(): SqlBrite {
        return SqlBrite.create {
            Timber.tag("Database").v(it)
        }
    }

    Provides
    Singleton
    fun providesDatabase(sqlBrite: SqlBrite, dbOpenHelper: DbOpenHelper): BriteDatabase {
        return sqlBrite.wrapDatabaseHelper(dbOpenHelper)
    }

    @Provides
    @Singleton
    fun providesDbDataSource(db: BriteDatabase): DbDataSource {
        return DbProvider(db)
    }

    @Provides
    @Singleton
    fun providesNetworkDataSource(): NetworkDataSource {
        return networkDataSource {
            logging = BuildConfig.DEBUG
            endpoint = BuildConfig.API_SERVER
        }
    }

    @Provides
    @Singleton
    fun providesDataSource(networkDataSource: NetworkDataSource, db: DbDataSource): HNDataSource {
        return HNDataSource(networkDataSource, db)
    }

}
