package com.dgsd.hackernews.network.utils

import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import hackernews.PbComment
import hackernews.PbStory

public fun PbComment.convert(): Comment {
    var item = Comment(
            id = this.id,
            time = this.time,
            author = this.author,
            text = this.text,
            deleted = this.deleted ?: false,
            dead =  this.dead ?: false,
            commentCount = this.comment_count ?: -1
    )

    if (this.comment_ids != null) {
        item = item.copy(commentIds = this.comment_ids.sortedDescending())
    }

    if (this.comments != null) {
        item = item.copy(comments = this.comments.map { it.convert() }.sortedByDescending { it -> it.time  }.toList())
    }

    if (this.parent_id != null && this.parent_id  > 0) {
        item = item.copy(parentId = this.parent_id)
    }

    return item
}

public fun PbStory.convert(): Story {
    var item = Story(
            id = this.id,
            time = this.time,
            author = this.author,
            title = this.title,
            text = this.text,
            url = this.url,
            score = this.score ?: -1,
            deleted = this.deleted ?: false,
            dead =  this.dead ?: false,
            commentCount = this.comment_count ?: -1
    )

    if (this.comment_ids != null) {
        item = item.copy(commentIds = this.comment_ids.sortedDescending())
    }

    if (this.comments != null) {
        item = item.copy(comments = this.comments.map { it.convert() }.sortedByDescending { it -> it.time  }.toList())
    }

    if (this.parent_id != null && this.parent_id  > 0) {
        item = item.copy(parentId = this.parent_id)
    }

    if (this.parts != null) {
        item = item.copy(pollAnswers = this.parts)
    }

    if (this.type != null) {
        try {
            item = item.copy(type = Story.Type.valueOf(this.type.toUpperCase()))
        } catch (ex: IllegalArgumentException) {
            item = item.copy(type = Story.Type.STORY)
        }
    }

    return item
}
