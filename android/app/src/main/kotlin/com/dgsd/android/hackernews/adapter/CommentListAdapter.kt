package com.dgsd.android.hackernews.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.util.getHtmlContent
import com.dgsd.android.hackernews.util.groupById
import com.dgsd.android.hackernews.view.CommentListItemView
import com.dgsd.android.hackernews.view.CommentPlaceholderListItemView
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.find
import timber.log.Timber
import java.util.*

public class CommentListAdapter : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    companion object {
        public val VIEW_TYPE_COMMENT = 0
        public val VIEW_TYPE_STORY_TEXT = 1
        public val VIEW_TYPE_COMMENT_PLACEHOLDER = 2
    }

    private var onCommentPlaceholderClickListener: (comment: List<Long>, View) -> Unit = { ids, v -> }

    private var onCommentClickListener: (Comment, View) -> Unit = { c, v -> }

    private val items = arrayListOf<ListItem>()

    val commentPlaceholderLoadingSet = HashSet<List<Long>>()

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.populate(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder? {
        val view: View = when (viewType) {
            CommentListAdapter.VIEW_TYPE_COMMENT -> {
                CommentListItemView.inflate(parent)
            }
            CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER -> {
                CommentPlaceholderListItemView.inflate(parent)
            }
            CommentListAdapter.VIEW_TYPE_STORY_TEXT -> {
                val storyCardView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.li_story_text, parent, false) as CardView
                val storyTextView = storyCardView.find<TextView>(R.id.text)
                storyTextView.movementMethod = LinkMovementMethod.getInstance()

                storyCardView.tag = storyTextView

                storyCardView
            }
            else -> {
                throw IllegalStateException("Unknown viewType: $viewType")
            }
        }
        return CommentViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getType()
    }

    override fun getItemCount(): Int {
        return items.size()
    }

    fun setStory(story: Story) {
        val oldItems = items.clone() as ArrayList<ListItem>

        items.clear()

        if (!story.text.isNullOrEmpty()) {
            items.add(ListItem(storyText = story.getHtmlContent()))
        }

        val commentIdToCommentMap = story.comments.groupById()

        addComments(commentIdToCommentMap, story.commentIds, 0)

        val startRange = items.mapIndexed { index, item ->
            if (!item.equals(oldItems.getOrNull(index))) {
                index
            } else {
                -1
            }
        }.filter { it >= 0 }.firstOrNull() ?: 0

        Timber.d("Notifying items changed: $startRange --> $itemCount")
        notifyItemRangeChanged(startRange, itemCount);
    }

    private fun addComments(commentMap: Map<Long, Comment>, commentIds: List<Long>, indentationLevel: Int) {
        val commentIdsToAdd = arrayListOf<Long>()
        commentIds.forEach { commentId ->
            val comment = commentMap.get(commentId)
            if (comment == null) {
                commentIdsToAdd.add(commentId)
            } else {
                items.add(ListItem(comment = comment, indentationLevel = indentationLevel))

                // Add the comments of this comment
                addComments(commentMap.plus(comment.comments.groupById()), comment.commentIds, indentationLevel + 1)
            }
        }

        if (commentIdsToAdd.isNotEmpty()) {
            items.add(ListItem(commentIds = commentIdsToAdd, indentationLevel = indentationLevel))
        }

    }

    public fun setCommentPlaceholderLoading(commentIds: List<Long>, showLoading: Boolean) {
        if (showLoading) {
            commentPlaceholderLoadingSet.add(commentIds)
        } else {
            commentPlaceholderLoadingSet.remove(commentIds)
        }

        with (items.indexOfFirst { commentIds.equals(it.commentIds) }) {
            if (this >= 0) {
                notifyItemChanged(this)
            }
        }
    }

    public fun setOnCommentClickListener(listener: (Comment, View) -> Unit) {
        onCommentClickListener = listener
    }

    public fun setOnCommentPlaceholderClickListener(listener: (List<Long>, View) -> Unit) {
        onCommentPlaceholderClickListener = listener
    }

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val item = items[position]
            when (item.getType()) {
                CommentListAdapter.VIEW_TYPE_COMMENT -> onCommentClickListener(item.comment!!, v)
                CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER -> onCommentPlaceholderClickListener(item.commentIds!!, v)
            }
        }

        public fun populate(item: ListItem) {
            when (item.getType()) {
                CommentListAdapter.VIEW_TYPE_COMMENT -> {
                    (itemView as CommentListItemView).populate(item.comment!!)
                }
                CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER -> {
                    val commentIds = item.commentIds!!
                    with (itemView as CommentPlaceholderListItemView) {
                        populate(commentIds)
                        setLoading(commentPlaceholderLoadingSet.contains(commentIds))
                    }
                }
                CommentListAdapter.VIEW_TYPE_STORY_TEXT -> {
                    (itemView.tag as TextView).text = item.storyText
                }
            }
        }

        public fun getIndentationLevel(): Int {
            return items[position].indentationLevel
        }
    }

    private data class ListItem(val commentIds: List<Long>? = null, val comment: Comment? = null,
                                val storyText: CharSequence? = null, val indentationLevel: Int = 0) {

        fun getType(): Int {
            return when {
                comment != null -> return CommentListAdapter.VIEW_TYPE_COMMENT
                commentIds != null -> return CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER
                storyText != null -> return CommentListAdapter.VIEW_TYPE_STORY_TEXT
                else -> throw IllegalStateException("Unknown view type for: " + this)
            }
        }

    }
}
