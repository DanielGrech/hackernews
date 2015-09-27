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
import rx.lang.kotlin.toObservable

public class DbProvider(private val db: BriteDatabase) : DbDataSource {

    override fun clearOldData(): Int {
        return db.delete(Tables.Stories.name(), Tables.DELETE_UNUSED_DATA_CLAUSE)
    }

    override fun getTopStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.TopStoryIds.name(), Tables.SELECT_TOP_STORIES)
    }

    override fun getNewStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.NewStoryIds.name(), Tables.SELECT_NEW_STORIES)
    }

    override fun getAskStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.AskStoryIds.name(), Tables.SELECT_ASK_STORIES)
    }

    override fun getShowStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.ShowStoryIds.name(), Tables.SELECT_SHOW_STORIES)
    }

    override fun getJobStories(): Observable<List<Story>> {
        return getStoriesFromIds(Tables.JobStoryIds.name(), Tables.SELECT_JOB_STORIES)
    }

    override fun saveStory(story: Story) {
        db.insert(Tables.Stories.name(), story.toContentValues(), CONFLICT_REPLACE)

        story.commentIds.forEach {
            db.insert(Tables.CommentIds.name(), ContentValues()
                    .with(Tables._CommentIds.COL_PARENT_ID, story.id)
                    .with(Tables._CommentIds.COL_COMMENT_ID, it), CONFLICT_REPLACE)
        }

        story.pollAnswers.forEach {
            db.insert(Tables.PollAnswers.name(), ContentValues()
                    .with(Tables._PollAnswers.COL_PARENT_ID, story.id)
                    .with(Tables._PollAnswers.COL_ANSWER_ID, it), CONFLICT_REPLACE)
        }

        saveComments(story.comments)
    }

    override fun saveComment(comment: Comment) {
        db.insert(Tables.Comments.name(), comment.toContentValues(), CONFLICT_REPLACE)

        comment.commentIds.forEach {
            db.insert(Tables.CommentIds.name(), ContentValues()
                    .with(Tables._CommentIds.COL_PARENT_ID, comment.id)
                    .with(Tables._CommentIds.COL_COMMENT_ID, it), CONFLICT_REPLACE)
        }

        comment.comments.forEach { saveComment(it) }
    }

    override fun saveTopStories(stories: List<Story>) {
        saveStories(Tables.TopStoryIds.name(), stories)
    }

    override fun saveNewStories(stories: List<Story>) {
        saveStories(Tables.NewStoryIds.name(), stories)
    }

    override fun saveAskStories(stories: List<Story>) {
        saveStories(Tables.AskStoryIds.name(), stories)
    }

    override fun saveShowStories(stories: List<Story>) {
        saveStories(Tables.ShowStoryIds.name(), stories)
    }

    override fun saveJobStories(stories: List<Story>) {
        saveStories(Tables.JobStoryIds.name(), stories)
    }

    override fun saveComments(comments: List<Comment>) {
        saveModels(comments, ::saveComment)
    }

    override fun getStory(storyId: Long): Observable<Story> {
        val commentObservable = getComments(storyId).defaultIfEmpty(emptyList())
        val commentIdObservable = getCommentIds(storyId).defaultIfEmpty(emptyList())
        val storyObservable = db.createQuery(Tables.Comments.name(), Tables.Stories.SELECT_BY_ID, storyId.toString())
                .mapToOne {
                    Tables.Stories.fromCursor(it)
                }

        return Observable.zip(storyObservable, commentObservable, commentIdObservable) { story, comments, commentIds ->
            story.copy(comments = comments, commentIds = commentIds)
        }
    }

    private fun getCommentIds(parentId: Long): Observable<List<Long>> {
        return db.createQuery(Tables.CommentIds.name(), Tables.CommentIds.SELECT_ALL_FOR_ITEM, parentId.toString())
                .mapToList {
                    Tables.CommentIds.fromCursor(it)
                }
    }

    override fun getComments(parentId: Long): Observable<List<Comment>> {
        return db.createQuery(Tables.Comments.name(), Tables.Comments.SELECT_ALL_FOR_ITEM, parentId.toString())
                .mapToList { Tables.Comments.fromCursor(it) }
                .firstOrDefault(emptyList())
                .flatMap { it.toObservable() }
                .flatMap { comment ->
                    getCommentIds(comment.id).firstOrDefault(emptyList()).map { ids ->
                        comment to ids
                    }
                }
                .map { it.first.copy(commentIds = it.second) }
                .flatMap { comment ->
                    getComments(comment.id).firstOrDefault(emptyList()).map { comments ->
                        comment to comments
                    }
                }
                .map { it.first.copy(comments = it.second) }
                .toList()
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