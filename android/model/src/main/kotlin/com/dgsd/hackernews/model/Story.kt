package com.dgsd.hackernews.model

import java.util.concurrent.TimeUnit

public data class Story(
        val id: Long = -1,
        val time: Long = -1,
        val type: Story.Type = Story.Type.STORY,
        val parentId: Long = -1,
        val author: String? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null,
        val commentCount: Int = -1,
        val score: Int = -1,
        val pollAnswers: List<Long> = emptyList(),
        val commentIds: List<Long> = emptyList(),
        val comments: List<Comment> = emptyList(),
        val dateRetrieved: Long = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())) {

    public enum class Type {
        STORY, JOB, POLL, POLL_ANSWER
    }

}