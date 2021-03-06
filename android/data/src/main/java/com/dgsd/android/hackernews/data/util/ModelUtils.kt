package com.dgsd.android.hackernews.data.util

import android.content.ContentValues
import com.dgsd.android.hackernews.data.Tables
import com.dgsd.android.moodtracker.data.util.with
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story

public fun Story.toContentValues(): ContentValues {
    return ContentValues()
            .with(Tables._Stories.COL_ID, this.id)
            .with(Tables._Stories.COL_TIME, this.time)
            .with(Tables._Stories.COL_TYPE, this.type.name())
            .with(Tables._Stories.COL_AUTHOR, this.author)
            .with(Tables._Stories.COL_PARENT_ID, this.parentId)
            .with(Tables._Stories.COL_TITLE, this.title)
            .with(Tables._Stories.COL_TEXT, this.text)
            .with(Tables._Stories.COL_URL, this.url)
            .with(Tables._Stories.COL_COMMENT_COUNT, this.commentCount)
            .with(Tables._Stories.COL_SCORE, this.score)
            .with(Tables._Stories.COL_DELETED, if(this.deleted) 1 else 0)
            .with(Tables._Stories.COL_DEAD, if(this.dead) 1 else 0)
            .with(Tables._Stories.COL_RETRIEVE_DATE, dateRetrieved)
}

public fun Comment.toContentValues(): ContentValues {
    return ContentValues()
            .with(Tables._Comments.COL_ID, this.id)
            .with(Tables._Comments.COL_TIME, this.time)
            .with(Tables._Comments.COL_AUTHOR, this.author)
            .with(Tables._Comments.COL_PARENT_ID, this.parentId)
            .with(Tables._Comments.COL_TEXT, this.text)
            .with(Tables._Comments.COL_DELETED, if(this.deleted) 1 else 0)
            .with(Tables._Comments.COL_DEAD, if(this.dead) 1 else 0)
            .with(Tables._Comments.COL_COMMENT_COUNT, this.commentCount)
}