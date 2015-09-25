package com.dgsd.android.moodtracker.data.util

import android.content.ContentValues
import android.database.Cursor

public fun Cursor.getLong(colName: String): Long {
    val colIndex = getColumnIndexOrThrow(colName)
    return if (isNull(colIndex)) -1 else getLong(colIndex)
}

public fun Cursor.getInt(colName: String): Int {
    val colIndex = getColumnIndexOrThrow(colName)
    return if (isNull(colIndex)) -1 else getInt(colIndex)
}

public fun Cursor.getBool(colName: String): Boolean {
    val colIndex = getColumnIndexOrThrow(colName)
    return if (isNull(colIndex)) false else getInt(colIndex) > 0
}

public fun Cursor.getString(colName: String): String {
    return getString(getColumnIndexOrThrow(colName))
}

public fun Cursor.isNull(colName: String): Boolean {
    return isNull(getColumnIndexOrThrow(colName))
}

public fun ContentValues.with(col: String, value: Int): ContentValues {
    this.put(col, value)
    return this
}

public fun ContentValues.with(col: String, value: Long): ContentValues {
    this.put(col, value)
    return this
}

public fun ContentValues.with(col: String, value: String?): ContentValues {
    if (value == null) {
        this.putNull(col)
    } else {
        this.put(col, value)
    }

    return this
}