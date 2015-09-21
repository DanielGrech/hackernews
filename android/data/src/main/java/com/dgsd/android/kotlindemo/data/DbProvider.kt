package com.dgsd.android.kotlindemo.data

import com.dgsd.android.kotlindemo.data.util.toContentValues
import com.dgsd.hackernews.model.Comment
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

    override fun saveStory(story: Story) {
        db.insert(Tables.Stories.name(), story.toContentValues())
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

    override fun getStory(storyId: Long): Observable<Story> {
        val storyObservable = db.createQuery(Tables.Comments.name(), Tables.Stories.SELECT_BY_ID, storyId.toString())
                .mapToOne {
                    Tables.Stories.fromCursor(it)
                }

        val commentObservable = getComments(storyId)

        return Observable.zip(storyObservable, commentObservable) { story, comments ->
            story.copy(comments = comments)
        }
    }

    override fun getComments(parentId: Long): Observable<List<Comment>> {
        return db.createQuery(Tables.Comments.name(), Tables.Comments.SELECT_ALL_FOR_ITEM, parentId.toString())
                .mapToList {
                    Tables.Comments.fromCursor(it)
                }
    }
}