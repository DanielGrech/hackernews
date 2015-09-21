package com.dgsd.hackernews.model

import java.util.concurrent.TimeUnit

public data class Story(
        val id: Long = -1,
        val time: Long = -1,
        val author: String? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null,
        val commentCount: Int = -1,
        val score: Int = -1,
        val commentIds: List<Long> = emptyList(),
        val comments: List<Comment> = emptyList(),
        val dateRetrieved: Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {
}