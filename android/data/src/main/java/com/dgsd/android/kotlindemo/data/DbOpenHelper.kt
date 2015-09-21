package com.dgsd.android.kotlindemo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

public class DbOpenHelper(context: Context) : SQLiteOpenHelper(context, DbOpenHelper.DB_NAME, null, DbOpenHelper.VERSION) {

    companion object {
        val VERSION = 1
        val DB_NAME = "hacker_news.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Tables.Stories().getCreateSql())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}