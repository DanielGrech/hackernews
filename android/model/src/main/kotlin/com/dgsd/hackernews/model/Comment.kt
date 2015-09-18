package com.dgsd.hackernews.model

public data class Comment(
        val id: Long = -1,
        val time: Long = -1,
        val author: String? = null,
        val text: String? = null,
        var parentId: Long = -1,
        val commentCount: Int = -1,
        val commentIds: List<Long> = emptyList(),
        val comments: List<Comment> = emptyList()) {
}