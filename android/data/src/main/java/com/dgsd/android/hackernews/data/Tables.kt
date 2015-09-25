package com.dgsd.android.hackernews.data

import android.database.Cursor
import com.dgsd.android.moodtracker.data.util.getInt
import com.dgsd.android.moodtracker.data.util.getLong
import com.dgsd.android.moodtracker.data.util.getString
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story

class Tables {

    companion object {
        val Stories: _Stories = _Stories()
        val TopStoryIds: _TopStoryIds = _TopStoryIds()
        val NewStoryIds: _NewStoryIds = _NewStoryIds()
        val AskStoryIds: _AskStoryIds = _AskStoryIds()
        val ShowStoryIds: _ShowStoryIds = _ShowStoryIds()
        val JobStoryIds: _JobStoryIds = _JobStoryIds()
        val Comments: _Comments = _Comments()
        val CommentIds: _CommentIds = _CommentIds()
        val PollAnswers: _PollAnswers= _PollAnswers()

        val COL_TYPE_PK = "INTEGER PRIMARY KEY AUTOINCREMENT"
        val COL_TYPE_INTEGER = "INTEGER"
        val COL_TYPE_TEXT = "TEXT"

        val COL_TYPE_NOT_NULL = "NOT NULL"

        private val SELECT_STORIES_TEMPLATE = "SELECT s.* FROM %s as t " +
                "INNER JOIN ${Stories.name()} AS s " +
                "ON t.${StoryIdTable.COL_ID} = s.${_Stories.COL_ID}"

        val SELECT_TOP_STORIES = SELECT_STORIES_TEMPLATE.format(TopStoryIds.name())
        val SELECT_NEW_STORIES = SELECT_STORIES_TEMPLATE.format(NewStoryIds.name())
        val SELECT_ASK_STORIES = SELECT_STORIES_TEMPLATE.format(AskStoryIds.name())
        val SELECT_SHOW_STORIES = SELECT_STORIES_TEMPLATE.format(ShowStoryIds.name())
        val SELECT_JOB_STORIES = SELECT_STORIES_TEMPLATE.format(JobStoryIds.name())
    }

    abstract class Table<T> {

        abstract fun name(): String

        abstract fun columns(): Array<String>

        abstract fun fromCursor(cursor: Cursor): T

        abstract fun getColumnType(col: String): String

        open fun getCreateSql(): String {
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

    abstract class StoryIdTable : Table<Long>() {
        companion object {
            val COL_ID = "_id"
        }

        override fun columns(): Array<String> {
            return arrayOf(COL_ID)
        }

        override fun fromCursor(cursor: Cursor): Long {
            return cursor.getLong(COL_ID)
        }

        override fun getColumnType(col: String): String {
            return when (col) {
                COL_ID -> COL_TYPE_INTEGER
                else -> ""
            }
        }
    }

    class _TopStoryIds : StoryIdTable() {
        companion object {
            val TABLE_NAME = "top_story_ids"
        }

        override fun name(): String {
            return TABLE_NAME
        }
    }

    class _NewStoryIds : StoryIdTable() {
        companion object {
            val TABLE_NAME = "new_story_ids"
        }

        override fun name(): String {
            return TABLE_NAME
        }
    }

    class _AskStoryIds : StoryIdTable() {
        companion object {
            val TABLE_NAME = "ask_story_ids"
        }

        override fun name(): String {
            return TABLE_NAME
        }
    }

    class _ShowStoryIds : StoryIdTable() {
        companion object {
            val TABLE_NAME = "show_story_ids"
        }

        override fun name(): String {
            return TABLE_NAME
        }
    }

    class _JobStoryIds : StoryIdTable() {
        companion object {
            val TABLE_NAME = "job_story_ids"
        }

        override fun name(): String {
            return TABLE_NAME
        }
    }

    class _Stories : Table<Story>() {

        val SELECT_ALL = "SELECT * FROM $TABLE_NAME ORDER BY $COL_TIME DESC"
        val SELECT_BY_ID = "SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?"

        companion object {
            val TABLE_NAME = "stories"

            val COL_ID = "_id"
            val COL_TYPE = "_type"
            val COL_TIME = "_time"
            val COL_AUTHOR = "_author"
            val COL_PARENT_ID = "_parent_id"
            val COL_TITLE = "_title"
            val COL_TEXT = "_text"
            val COL_URL = "_url"
            val COL_COMMENT_COUNT = "_comment_count"
            val COL_SCORE = "_score"
            val COL_RETRIEVE_DATE = "_date_retrieved"
        }

        override fun name(): String {
            return TABLE_NAME
        }

        override fun columns(): Array<String> {
            return arrayOf(
                    COL_ID,
                    COL_TYPE,
                    COL_TIME,
                    COL_PARENT_ID,
                    COL_AUTHOR,
                    COL_TITLE,
                    COL_TEXT,
                    COL_URL,
                    COL_COMMENT_COUNT,
                    COL_SCORE,
                    COL_RETRIEVE_DATE
            )
        }

        override fun fromCursor(cursor: Cursor): Story {
            return Story(
                    id = cursor.getLong(COL_ID),
                    type = Story.Type.valueOf(cursor.getString(COL_TYPE)),
                    parentId = cursor.getLong(COL_PARENT_ID),
                    time = cursor.getLong(COL_TIME),
                    author = cursor.getString(COL_AUTHOR),
                    title = cursor.getString(COL_TITLE),
                    text = cursor.getString(COL_TEXT),
                    url = cursor.getString(COL_URL),
                    commentCount = cursor.getInt(COL_COMMENT_COUNT),
                    score = cursor.getInt(COL_SCORE),
                    dateRetrieved = cursor.getLong(COL_RETRIEVE_DATE)
            )
        }

        override fun getColumnType(col: String): String {
            return when (col) {
                COL_ID -> Tables.COL_TYPE_PK
                COL_TYPE -> Tables.COL_TYPE_TEXT
                COL_TIME -> Tables.COL_TYPE_INTEGER + " " + Tables.COL_TYPE_NOT_NULL
                COL_PARENT_ID -> Tables.COL_TYPE_INTEGER
                COL_AUTHOR -> Tables.COL_TYPE_TEXT
                COL_TITLE -> Tables.COL_TYPE_TEXT
                COL_TEXT -> Tables.COL_TYPE_TEXT
                COL_URL -> Tables.COL_TYPE_TEXT
                COL_COMMENT_COUNT -> Tables.COL_TYPE_INTEGER
                COL_SCORE -> Tables.COL_TYPE_INTEGER
                COL_RETRIEVE_DATE -> Tables.COL_TYPE_INTEGER
                else -> ""
            }
        }
    }

    class _Comments : Table<Comment>() {

        val SELECT_ALL_FOR_ITEM = "SELECT * FROM $TABLE_NAME WHERE $COL_PARENT_ID = ? ORDER BY $COL_TIME DESC"

        companion object {
            val TABLE_NAME = "comments"

            val COL_ID = "_id"
            val COL_TIME = "_time"
            val COL_AUTHOR = "_author"
            val COL_TEXT = "_text"
            val COL_PARENT_ID = "_parent_id"
            val COL_COMMENT_COUNT = "_comment_count"
        }

        override fun name(): String {
            return TABLE_NAME
        }

        override fun columns(): Array<String> {
            return arrayOf(
                    COL_ID,
                    COL_TIME,
                    COL_AUTHOR,
                    COL_PARENT_ID,
                    COL_TEXT,
                    COL_COMMENT_COUNT
            )
        }

        override fun fromCursor(cursor: Cursor): Comment {
            return Comment(
                    id = cursor.getLong(COL_ID),
                    time = cursor.getLong(COL_TIME),
                    author = cursor.getString(COL_AUTHOR),
                    parentId = cursor.getLong(COL_PARENT_ID),
                    text = cursor.getString(COL_TEXT),
                    commentCount = cursor.getInt(COL_COMMENT_COUNT)
            )
        }

        override fun getColumnType(col: String): String {
            return when (col) {
                COL_ID -> Tables.COL_TYPE_PK
                COL_TIME -> Tables.COL_TYPE_INTEGER + " " + Tables.COL_TYPE_NOT_NULL
                COL_AUTHOR -> Tables.COL_TYPE_TEXT
                COL_PARENT_ID -> Tables.COL_TYPE_INTEGER
                COL_TEXT -> Tables.COL_TYPE_TEXT
                COL_COMMENT_COUNT -> Tables.COL_TYPE_INTEGER
                else -> ""
            }
        }
    }

    class _CommentIds : Table<Long>() {

        val SELECT_ALL_FOR_ITEM = "SELECT * FROM $TABLE_NAME WHERE $COL_PARENT_ID = ?"

        companion object {
            val TABLE_NAME = "comment_ids"

            val COL_PARENT_ID = "_parent_id"
            val COL_COMMENT_ID = "_comment_id"
        }

        override fun name(): String {
            return TABLE_NAME
        }

        override fun columns(): Array<String> {
            return arrayOf(
                    COL_PARENT_ID,
                    COL_COMMENT_ID
            )
        }

        override fun fromCursor(cursor: Cursor): Long {
            return cursor.getLong(COL_COMMENT_ID)
        }

        override fun getColumnType(col: String): String {
            return when (col) {
                COL_PARENT_ID -> Tables.COL_TYPE_INTEGER
                COL_COMMENT_ID -> Tables.COL_TYPE_INTEGER
                else -> ""
            }
        }

        override fun getCreateSql(): String {
            val createSql = super.getCreateSql()

            val lastParenth = createSql.lastIndexOf(')')
            return createSql.substring(0, lastParenth) +
                    ", PRIMARY KEY ($COL_PARENT_ID, $COL_COMMENT_ID)" +
                    createSql.substring(lastParenth)

        }
    }

    class _PollAnswers : Table<Long>() {

        companion object {
            val TABLE_NAME = "poll_answers"

            val COL_PARENT_ID = "_parent_id"
            val COL_ANSWER_ID = "_answer_id"
        }

        override fun name(): String {
            return TABLE_NAME
        }

        override fun columns(): Array<String> {
            return arrayOf(COL_PARENT_ID, COL_ANSWER_ID)
        }

        override fun fromCursor(cursor: Cursor): Long {
            return cursor.getLong(COL_ANSWER_ID)
        }

        override fun getColumnType(col: String): String {
            return when (col) {
                COL_PARENT_ID -> Tables.COL_TYPE_INTEGER
                COL_ANSWER_ID -> Tables.COL_TYPE_INTEGER
                else -> ""
            }
        }

        override fun getCreateSql(): String {
            val createSql = super.getCreateSql()

            val lastParenth = createSql.lastIndexOf(')')
            return createSql.substring(0, lastParenth) +
                    ", PRIMARY KEY ($COL_PARENT_ID, $COL_ANSWER_ID)" +
                    createSql.substring(lastParenth)
        }
    }
}