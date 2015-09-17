package com.dgsd.hackernews.model

public data class Item(
        val id: Long = -1,
        val time: Long = -1,
        val type: Item.Type = Item.Type.UNKNOWN,
        val author: String? = null,
        val title: String? = null,
        val text: String? = null,
        val url: String? = null,
        val commentCount: Int = -1,
        val score: Int = -1,
        val deleted: Boolean = false,
        val dead: Boolean = false,
        val parentId: Long = -1,
        val childItemIds: List<Long> = emptyList(),
        private val pollOpts: List<Long> = emptyList()) {


    public enum class Type {
        JOB,
        STORY,
        COMMENT,
        POLL,
        POLLOPT,
        UNKNOWN;
    }

    public fun hasType(): Boolean {
        return !this.type.equals(Item.Type.UNKNOWN)
    }


    public val pollOptionIds: List<Long>
            get() = if (type.equals(Item.Type.POLL)) pollOpts else emptyList()


}