package com.dgsd.android.kotlindemo.data

import android.database.Cursor
import com.dgsd.android.moodtracker.data.util.getInt
import com.dgsd.android.moodtracker.data.util.getLong
import com.dgsd.android.moodtracker.data.util.getString
import com.dgsd.hackernews.model.Story

class Tables {

    companion object {
        val COL_TYPE_PK = "INTEGER PRIMARY KEY AUTOINCREMENT"
        val COL_TYPE_INTEGER = "INTEGER"
        val COL_TYPE_TEXT = "TEXT"

        val COL_TYPE_NOT_NULL = "NOT NULL"
    }

    abstract class Table<T> {

        abstract fun name(): String

        abstract fun columns(): Array<String>

        abstract fun fromCursor(cursor: Cursor): T

        abstract fun getColumnType(col: String): String

        fun getCreateSql(): String {
            val sb = StringBuilder()
            sb.append("CREATE TABLE IF NOT EXISTS ").append(name()).append("(")
            with(columns()) {
                forEachIndexed { index, col ->
                    sb.append(col).append(' ').append(getColumnType(col))
                    if (index != lastIndex) {
                        sb.append(", ")
                    }
                }
            }

            sb.append(")")

            return sb.toString()
        }
    }

    class Stories : Table<Story>() {
        override fun getColumnType(col: String): String {
            return when (col) {
                COL_ID -> Tables.COL_TYPE_PK
                COL_TIME -> Tables.COL_TYPE_INTEGER + " " + Tables.COL_TYPE_NOT_NULL
                COL_AUTHOR -> Tables.COL_TYPE_TEXT
                COL_TITLE -> Tables.COL_TYPE_TEXT
                COL_TEXT -> Tables.COL_TYPE_TEXT
                COL_URL -> Tables.COL_TYPE_TEXT
                COL_COMMENT_COUNT -> Tables.COL_TYPE_INTEGER
                COL_SCORE -> Tables.COL_TYPE_INTEGER
                else -> ""
            }
        }

        companion object {
            val COL_ID = "_id"
            val COL_TIME = "_time"
            val COL_AUTHOR = "_author"
            val COL_TITLE = "_title"
            val COL_TEXT = "_text"
            val COL_URL = "_url"
            val COL_COMMENT_COUNT = "_comment_count"
            val COL_SCORE = "_score"
        }

        override fun name(): String {
            return "stories"
        }

        override fun columns(): Array<String> {
            return arrayOf(
                    COL_ID,
                    COL_TIME,
                    COL_AUTHOR,
                    COL_TITLE,
                    COL_TEXT,
                    COL_URL,
                    COL_COMMENT_COUNT,
                    COL_SCORE
            )
        }

        override fun fromCursor(cursor: Cursor): Story {
            return Story(
                    id = cursor.getLong(COL_ID),
                    time = cursor.getLong(COL_TIME),
                    author = cursor.getString(COL_AUTHOR),
                    title = cursor.getString(COL_TITLE),
                    url = cursor.getString(COL_URL),
                    commentCount = cursor.getInt(COL_COMMENT_COUNT),
                    score = cursor.getInt(COL_SCORE)
            )
        }

    }
}