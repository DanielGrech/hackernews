package com.dgsd.hackernews.model

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


public class CommentTest {

    @Test
    fun testDeadOrDeletedWhenDeadButNotDeleted() {
        assertTrue {
            Comment(dead = true, deleted = false).deadOrDeleted()
        }
    }

    @Test
    fun testDeadOrDeletedWhenDeletedButNotDead() {
        assertTrue {
            Comment(dead = false, deleted = true).deadOrDeleted()
        }
    }

    @Test
    fun testDeadOrDeletedWhenNeither() {
        assertFalse {
            Comment(dead = false, deleted = false).deadOrDeleted()
        }
    }

    @Test
    fun testDeadOrDeleted() {
        assertTrue {
            Comment(dead = true, deleted = true).deadOrDeleted()
        }
    }

    @Test
    fun testHasBeenRemovedWhenNotDeadButNoComments() {
        assertFalse {
            Comment(dead = false, commentIds = emptyList()).hasBeenRemoved()
        }
    }

    @Test
    fun testHasBeenRemovedWhenNotDeletedNoComments() {
        assertFalse {
            Comment(deleted = false, commentIds = emptyList()).hasBeenRemoved()
        }
    }

    @Test
    fun testHasBeenRemovedWhenDeadWithComments() {
        assertFalse {
            Comment(dead = true, commentIds = listOf(123)).hasBeenRemoved()
        }
    }

    @Test
    fun testHasBeenRemovedWhenDeletedWithComments() {
        assertFalse {
            Comment(deleted = false, commentIds = listOf(123)).hasBeenRemoved()
        }
    }

    @Test
    fun testHasBeenRemovedWhenDeadOrDeletedWithComments() {
        assertTrue {
            Comment(dead = true, commentIds = emptyList()).hasBeenRemoved()
        }
    }
}