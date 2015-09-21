package com.dgsd.android.kotlindemo.data

import com.dgsd.android.kotlindemo.data.util.toContentValues
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DbDataSource
import com.squareup.sqlbrite.BriteDatabase
import rx.Observable

public class DbProvider(private val db: BriteDatabase) : DbDataSource {

    override fun getTopStories(): Observable<List<Story>> {
        return db.createQuery(Tables.Stories.name(), Tables.Stories.SELECT_ALL)
                .mapToList {
                    Tables.Stories.fromCursor(it)
                }
    }

    override fun saveTopStories(stories: List<Story>) {
        val transaction = db.newTransaction()
        try {
            stories.forEach {
                db.insert(Tables.Stories.name(), it.toContentValues())
            }

            transaction.markSuccessful()
        } finally {
            transaction.end()
        }
    }

}