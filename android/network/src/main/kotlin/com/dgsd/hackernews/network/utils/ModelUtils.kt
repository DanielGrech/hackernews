package com.dgsd.hackernews.network.utils

import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import com.dgsd.hackernews.network.model.HnComment
import com.dgsd.hackernews.network.model.HnStory

public fun HnComment.convert(): Comment {
    var item = Comment(
            id = this.id,
            time = this.time,
            author = this.author,
            parentId = this.parentId,
            text = this.text,
            commentCount = this.commentCount
    )

    if (this.commentIds != null) {
        item = item.copy(commentIds = this.commentIds)
    }

    if (this.comments != null) {
        item = item.copy(comments = this.comments.map { it.convert() }.toList())
    }

    return item
}

public fun HnStory.convert(): Story {
    var item = Story(
            id = this.id,
            time = this.time,
            author = this.author,
            title = this.title,
            text = this.text,
            url = this.url,
            score = this.score,
            commentCount = this.commentCount
    )

    if (this.commentIds != null) {
        item = item.copy(commentIds = this.commentIds)
    }

    if (this.comments != null) {
        item = item.copy(comments = this.comments.map { it.convert() }.toList())
    }

    return item
}
