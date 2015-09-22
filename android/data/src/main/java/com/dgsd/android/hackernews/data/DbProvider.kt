package com.dgsd.android.hackernews.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import com.dgsd.android.hackernews.data.util.toContentValues
import com.dgsd.android.moodtracker.data.util.with
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.DbDataSource
import com.squareup.sqlbrite.BriteDatabase
import rx.Observable

public class DbProvider(private val db: BriteDatabase) : DbDataSource {
    override fun getTopStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.TopStoryIds.name(), Tables.SELECT_TOP_STORIES)
    }

    override fun getNewStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.NewStoryIds.name(), Tables.SELECT_NEW_STORIES)
    }

    override fun saveStory(story: Story) {
        db.insert(Tables.Stories.name(), story.toContentValues(), CONFLICT_REPLACE)

        story.commentIds.forEach {
            db.insert(Tables.CommentIds.name(), ContentValues()
                    .with(Tables._CommentIds.COL_PARENT_ID, story.id)
                    .with(Tables._CommentIds.COL_COMMENT_ID, it), CONFLICT_REPLACE)
        }

        saveComments(story.comments)
    }

    override fun saveComment(comment: Comment) {
        db.insert(Tables.Comments.name(), comment.toContentValues(), CONFLICT_REPLACE)
    }

    override fun saveTopStories(stories: List<Story>) {
        saveStories(Tables.TopStoryIds.name(), stories)
    }

    override fun saveNewStories(stories: List<Story>) {
        saveStories(Tables.NewStoryIds.name(), stories)
    }

    override fun saveComments(comments: List<Comment>) {
        saveModels(comments, ::saveComment)
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

    private fun getStoriesFromIds(tableName: String, query: String): Observable<List<Story>> {
        return db.createQuery(listOf(Tables.Stories.name(), tableName), query)
                .mapToList {
                    Tables.Stories.fromCursor(it)
                }
    }

    private fun saveStories(tableName: String, stories: List<Story>) {
        saveModels(stories, ::saveStory)

        transaction {
            db.delete(tableName, null)

            stories.map {
                it.id
            }.forEach {
                db.insert(tableName, ContentValues().with(Tables.StoryIdTable.COL_ID, it), CONFLICT_REPLACE)
            }
        }
    }

    private fun <T> saveModels(models: List<T>, saveFn: DbProvider.(T) -> Unit) {
        transaction {
            models.forEach {
                saveFn(it)
            }
        }
    }

    private fun transaction(operation: () -> Unit) {
        val transaction = db.newTransaction()
        try {
            operation()
            transaction.markSuccessful()
        } finally {
            transaction.end()
        }
    }
}