package com.dgsd.hackernews.network.utils

import com.dgsd.hackernews.model.Story
import hackernews.PbComment
import hackernews.PbStory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

public class ModelUtilsTest {

    @Test
    fun testConvertComment() {
        val EXPECTED_ID = 1L
        val EXPECTED_TIME = System.currentTimeMillis()
        val EXPECTED_AUTHOR = "dg"
        val EXPECTED_TEXT = "my awesome text"
        val EXPECTED_DELETED = true
        val EXPECTED_DEAD = true
        val EXPECTED_COMMENT_COUNT = 14
        val EXPECTED_PARENT_ID = 9L

        val pbComment = PbComment.Builder()
                .id(EXPECTED_ID)
                .time(EXPECTED_TIME)
                .author(EXPECTED_AUTHOR)
                .text(EXPECTED_TEXT)
                .deleted(EXPECTED_DELETED)
                .dead(EXPECTED_DEAD)
                .comment_count(EXPECTED_COMMENT_COUNT)
                .parent_id(EXPECTED_PARENT_ID)
                .build()

        val comment = pbComment.convert()

        assertThat(comment.id).isEqualTo(EXPECTED_ID)
        assertThat(comment.time).isEqualTo(EXPECTED_TIME)
        assertThat(comment.author).isEqualTo(EXPECTED_AUTHOR)
        assertThat(comment.text).isEqualTo(EXPECTED_TEXT)
        assertThat(comment.deleted).isEqualTo(EXPECTED_DELETED)
        assertThat(comment.dead).isEqualTo(EXPECTED_DEAD)
        assertThat(comment.commentCount).isEqualTo(EXPECTED_COMMENT_COUNT)
        assertThat(comment.parentId).isEqualTo(EXPECTED_PARENT_ID)

    }

    @Test
    fun testConvertCommentWithNoCommentIds() {
        val comment = PbComment.Builder(createComment()).comment_ids(null).build().convert()
        assertThat(comment.commentIds).isEmpty()
    }

    @Test
    fun testConvertCommentWithNoComments() {
        val comment = PbComment.Builder(createComment()).comments(null).build().convert()
        assertThat(comment.comments).isEmpty()
    }

    @Test
    fun testConvertCommentSortsCommentsIdsDescending() {
        val commentsId = 1L.rangeTo(10L).toList()
        val comment = PbComment.Builder(createComment()).comment_ids(commentsId).build().convert()

        assertThat(comment.commentIds).containsExactlyElementsOf(10L.downTo(1L))
    }

    @Test
    fun testConvertCommentSortsCommentsDecendingByTime() {
        val comments = 1L.rangeTo(10L).map { PbComment.Builder(createComment()).id(it).time(it).build() }
        val comment = PbComment.Builder(createComment()).comments(comments).build().convert()

        assertThat(comment.comments.map { it.id }).containsExactlyElementsOf(10L.downTo(1L))
    }

    @Test
    fun testConvertStory() {
        val EXPECTED_ID = 1L
        val EXPECTED_TIME = System.currentTimeMillis()
        val EXPECTED_AUTHOR = "dg"
        val EXPECTED_TITLE = "title"
        val EXPECTED_TEXT = "my awesome text"
        val EXPECTED_URL = "http://google.com"
        val EXPECTED_SCORE = 1337
        val EXPECTED_DELETED = true
        val EXPECTED_DEAD = true
        val EXPECTED_COMMENT_COUNT = 14
        val EXPECTED_PARENT_ID = 9L
        val EXPECTED_TYPE = "JOB"

        val pbStory = PbStory.Builder()
                .id(EXPECTED_ID)
                .time(EXPECTED_TIME)
                .author(EXPECTED_AUTHOR)
                .title(EXPECTED_TITLE)
                .text(EXPECTED_TEXT)
                .url(EXPECTED_URL)
                .score(EXPECTED_SCORE)
                .deleted(EXPECTED_DELETED)
                .dead(EXPECTED_DEAD)
                .comment_count(EXPECTED_COMMENT_COUNT)
                .parent_id(EXPECTED_PARENT_ID)
                .type(EXPECTED_TYPE)
                .build()

        val story = pbStory.convert()

        assertThat(story.id).isEqualTo(EXPECTED_ID)
        assertThat(story.time).isEqualTo(EXPECTED_TIME)
        assertThat(story.author).isEqualTo(EXPECTED_AUTHOR)
        assertThat(story.title).isEqualTo(EXPECTED_TITLE)
        assertThat(story.text).isEqualTo(EXPECTED_TEXT)
        assertThat(story.url).isEqualTo(EXPECTED_URL)
        assertThat(story.score).isEqualTo(EXPECTED_SCORE)
        assertThat(story.deleted).isEqualTo(EXPECTED_DELETED)
        assertThat(story.dead).isEqualTo(EXPECTED_DEAD)
        assertThat(story.commentCount).isEqualTo(EXPECTED_COMMENT_COUNT)
        assertThat(story.parentId).isEqualTo(EXPECTED_PARENT_ID)
        assertThat(story.type).isEqualTo(Story.Type.JOB)
    }

    @Test
    fun testConvertWithNoCommentIdsStory() {
        val story = PbStory.Builder(createStory()).comment_ids(null).build().convert()
        assertThat(story.commentIds).isEmpty()
    }

    @Test
    fun testConvertWithNoCommentsStory() {
        val story = PbStory.Builder(createStory()).comments(null).build().convert()
        assertThat(story.comments).isEmpty()
    }

    @Test
    fun testConvertStorySortsCommentsIdsDescending() {
        val commentsId = 1L.rangeTo(10L).toList()
        val story = PbStory.Builder(createStory()).comment_ids(commentsId).build().convert()

        assertThat(story.commentIds).containsExactlyElementsOf(10L.downTo(1L))
    }

    @Test
    fun testConvertStorySortsCommentsDescendingByTime() {
        val comments = 1L.rangeTo(10L).map { PbComment.Builder(createComment()).id(it).time(it).build() }
        val story = PbStory.Builder(createStory()).comments(comments).build().convert()

        assertThat(story.comments.map { it.id }).containsExactlyElementsOf(10L.downTo(1L))
    }

    @Test
    fun testConvertStoryWithNoParentId() {
        val story = PbStory.Builder(createStory()).parent_id(null).build().convert()
        assertThat(story.parentId).isNegative()
    }

    @Test
    fun testConvertStoryWithNoScore() {
        val story = PbStory.Builder(createStory()).score(null).build().convert()
        assertThat(story.score).isNegative()
    }

    @Test
    fun testConvertStoryWithNoDeadFlag() {
        val story = PbStory.Builder(createStory()).dead(null).build().convert()
        assertThat(story.dead).isFalse()
    }

    @Test
    fun testConvertStoryWithNoDeletedFlag() {
        val story = PbStory.Builder(createStory()).deleted(null).build().convert()
        assertThat(story.deleted).isFalse()
    }

    @Test
    fun testConvertStoryWithNoParts() {
        val story = PbStory.Builder(createStory()).parts(null).build().convert()
        assertThat(story.pollAnswers).isEmpty()
    }

    @Test
    fun testConvertStoryWithUnknownType() {
        val story = PbStory.Builder(createStory()).type("unknown").build().convert()
        assertThat(story.type).isEqualTo(Story.Type.STORY)
    }

    private fun createStory(): PbStory {
        return PbStory.Builder()
                .id(123)
                .time(System.currentTimeMillis())
                .type(Story.Type.STORY.name())
                .build()
    }

    private fun createComment(): PbComment {
        return PbComment.Builder()
                .id(123)
                .time(System.currentTimeMillis())
                .build()
    }
}
