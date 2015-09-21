package com.dgsd.android.kotlindemo.data.util

import android.content.ContentValues
import com.dgsd.android.kotlindemo.data.Tables
import com.dgsd.android.moodtracker.data.util.with
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story

public fun Story.toContentValues(): ContentValues {
    return ContentValues()
            .with(Tables._Stories.COL_ID, this.id)
            .with(Tables._Stories.COL_TIME, this.time)
            .with(Tables._Stories.COL_AUTHOR, this.author)
            .with(Tables._Stories.COL_TITLE, this.title)
            .with(Tables._Stories.COL_TEXT, this.text)
            .with(Tables._Stories.COL_URL, this.url)
            .with(Tables._Stories.COL_COMMENT_COUNT, this.commentCount)
            .with(Tables._Stories.COL_SCORE, this.score)
            .with(Tables._Stories.COL_RETRIEVE_DATE, dateRetrieved)
}

public fun Comment.toContentValues(): ContentValues {
    return ContentValues()
            .with(Tables._Comments.COL_ID, this.id)
            .with(Tables._Comments.COL_TIME, this.time)
            .with(Tables._Comments.COL_AUTHOR, this.author)
            .with(Tables._Comments.COL_PARENT_ID, this.parentId)
            .with(Tables._Comments.COL_TEXT, this.text)
            .with(Tables._Comments.COL_COMMENT_COUNT, this.commentCount)
}