package com.dgsd.hackernews.model

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
        val comments: List<Comment> = emptyList()) {
}