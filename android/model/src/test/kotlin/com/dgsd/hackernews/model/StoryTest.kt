package com.dgsd.hackernews.model

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


public class StoryTest {

    @Test
    fun testHasCommentsWhenNoCommentIds() {
        assertFalse {
            Story(commentIds = emptyList()).hasComments()
        }
    }

    @Test
    fun testHasComments() {
        assertTrue {
            Story(commentIds = listOf(123)).hasComments()
        }
    }

    @Test
    fun testHasCommentsToLoadWithNoCommentIds() {
        assertFalse {
            Story(commentIds = emptyList(), comments = emptyList()).hasCommentsToLoad()
        }
    }

    @Test
    fun testHasCommentsToLoadWithCommentIdsButNoComments() {
        assertTrue {
            Story(commentIds = listOf(123), comments = emptyList()).hasComments()
        }
    }
}