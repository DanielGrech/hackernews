package com.dgsd.hackernews.network.utils

import com.dgsd.hackernews.model.Item
import com.dgsd.hackernews.network.model.HnItem

private fun convertType(typeAsStr: String?): Item.Type {
    return when (typeAsStr) {
        HnItem.TYPE_COMMENT -> Item.Type.COMMENT
        HnItem.TYPE_JOB -> Item.Type.JOB
        HnItem.TYPE_STORY -> Item.Type.STORY
        HnItem.TYPE_POLL -> Item.Type.POLL
        HnItem.TYPE_POLLOPT -> Item.Type.POLLOPT
        else -> Item.Type.UNKNOWN
    }
}

public fun HnItem.convert(): Item {
    var item = Item(
            id = this.id,
            time = this.time,
            author = this.by,
            title = this.title,
            text = this.text,
            url = this.url,
            deleted = this.isDeleted,
            dead = this.isDead,
            type = convertType(this.type)
    )

    if (this.descendants != null) {
        item = item.copy(commentCount = this.descendants)
    }

    if (this.score != null) {
        item = item.copy(score = this.score)
    }

    if (this.parent != null) {
        item = item.copy(parentId = this.parent)
    }

    if (this.kids != null) {
        item = item.copy(childItemIds = this.kids)
    }

    if (this.parts != null) {
        item = item.copy(pollOpts = this.parts)
    }

    return item
}
