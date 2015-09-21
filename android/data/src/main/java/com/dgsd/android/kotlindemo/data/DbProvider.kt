package com.dgsd.android.kotlindemo.data

import android.content.ContentValues
import com.dgsd.android.kotlindemo.data.util.toContentValues
import com.dgsd.android.moodtracker.data.util.with
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

        story.commentIds.forEach {
            db.insert(Tables.CommentIds.name(), ContentValues()
                    .with(Tables._CommentIds.COL_PARENT_ID, story.id)
                    .with(Tables._CommentIds.COL_COMMENT_ID, it))
        }

        saveComments(story.comments)
    }

    override fun saveComment(comment: Comment) {
        db.insert(Tables.Comments.name(), comment.toContentValues())
    }

    override fun saveTopStories(stories: List<Story>) {
        saveModels(stories, ::saveStory)
    }

    override fun saveComments(comments: List<Comment>) {
        saveModels(comments, ::saveComment)
    }

    private fun <T> saveModels(models: List<T>, saveFn: DbProvider.(T) -> Unit) {
        val transaction = db.newTransaction()
        try {
            models.forEach {
                saveFn(it)
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