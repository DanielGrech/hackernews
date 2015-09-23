package com.dgsd.android.hackernews.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context) : SQLiteOpenHelper(context, DbOpenHelper.DB_NAME, null, DbOpenHelper.VERSION) {

    companion object {
        val VERSION = 1
        val DB_NAME = "hacker_news.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Tables.Stories.getCreateSql())
        db.execSQL(Tables.TopStoryIds.getCreateSql())
        db.execSQL(Tables.NewStoryIds.getCreateSql())
        db.execSQL(Tables.AskStoryIds.getCreateSql())
        db.execSQL(Tables.ShowStoryIds.getCreateSql())
        db.execSQL(Tables.JobStoryIds.getCreateSql())
        db.execSQL(Tables.Comments.getCreateSql())
        db.execSQL(Tables.CommentIds.getCreateSql())
        db.execSQL(Tables.PollAnswers.getCreateSql())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}