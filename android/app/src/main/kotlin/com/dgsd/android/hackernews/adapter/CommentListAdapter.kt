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
        public val VIEW_TYPE_NO_COMMENTS = 3
    }

    private var onCommentPlaceholderClickListener: (comment: List<Long>, View) -> Unit = { ids, v -> }

    private var onCommentClickListener: (Comment, View) -> Unit = { c, v -> }

    private var onCommentLongClickListener: (Comment, View) -> Unit = {c , v -> }

    private val items = arrayListOf<ListItem>()

    private var commentIdToHightlight: Long? = null

    val commentPlaceholderLoadingSet = HashSet<List<Long>>()

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.populate(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder? {
        val view: View = when (viewType) {
            CommentListAdapter.VIEW_TYPE_NO_COMMENTS -> {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.li_no_comments, parent, false)
            }
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
                storyTextView.setLineSpacing(0f, 1.2f)

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

    override fun getItemId(position: Int): Long {
        return when (getItemViewType(position)) {
            CommentListAdapter.VIEW_TYPE_COMMENT -> items[position].comment!!.id
            CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER -> Long.MAX_VALUE
            CommentListAdapter.VIEW_TYPE_NO_COMMENTS -> Long.MAX_VALUE - 1
            CommentListAdapter.VIEW_TYPE_STORY_TEXT -> Long.MAX_VALUE - 2
            else -> Long.MAX_VALUE - 3
        }
    }

    fun showNoCommentsMessage(message: String) {
        items.add(ListItem(noCommentMessage = message))
        notifyDataSetChanged()
    }

    fun setStory(story: Story) {
        val oldItems = items.clone() as ArrayList<ListItem>

        items.clear()

        if (!story.text.isNullOrBlank()) {
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
        }.filter { it >= 0 }.firstOrNull() ?: -1

        if (startRange >= 0) {
            Timber.d("Notifying items changed: $startRange --> $itemCount")
            notifyItemRangeChanged(startRange, itemCount);
        } else {
            Timber.d("No items updated")
        }
    }

    private fun addComments(commentMap: Map<Long, Comment>, commentIds: List<Long>, indentationLevel: Int) {
        val commentIdsToAdd = arrayListOf<Long>()
        commentIds.forEach { commentId ->
            val comment = commentMap.get(commentId)
            if (comment == null) {
                commentIdsToAdd.add(commentId)
            } else {
                if (!comment.hasBeenRemoved()) {
                    items.add(ListItem(comment = comment, indentationLevel = indentationLevel))

                    // Add the comments of this comment
                    addComments(commentMap.plus(comment.comments.groupById()), comment.commentIds, indentationLevel + 1)
                }
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

    public fun setOnCommentLongClickListener(listener: (Comment, View) -> Unit) {
        onCommentLongClickListener = listener
    }

    public fun setOnCommentClickListener(listener: (Comment, View) -> Unit) {
        onCommentClickListener = listener
    }

    public fun setOnCommentPlaceholderClickListener(listener: (List<Long>, View) -> Unit) {
        onCommentPlaceholderClickListener = listener
    }

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            val item = items.getOrNull(position)
            when (item?.getType()) {
                CommentListAdapter.VIEW_TYPE_COMMENT -> onCommentClickListener(item!!.comment!!, v)
                CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER -> {
                    with (v as CommentPlaceholderListItemView) {
                        if (!isLoading()) {
                            onCommentPlaceholderClickListener(item!!.commentIds!!, v)
                        }
                    }
                }
            }
        }

        override fun onLongClick(v: View): Boolean {
            val item = items.getOrNull(position)
            when (item?.getType()) {
                CommentListAdapter.VIEW_TYPE_COMMENT -> onCommentLongClickListener(item!!.comment!!, v)
            }
            return true
        }

        public fun populate(item: ListItem) {
            when (item.getType()) {
                CommentListAdapter.VIEW_TYPE_NO_COMMENTS -> {
                    (itemView as TextView).text = item.noCommentMessage
                }
                CommentListAdapter.VIEW_TYPE_COMMENT -> {
                    with (itemView as CommentListItemView) {
                        populate(item.comment!!, item.indentationLevel)
                        showHighlighted(commentIdToHightlight != null && item.comment.id == commentIdToHightlight)
                    }
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
            return items.getOrNull(position)?.indentationLevel ?: 0
        }
    }

    private data class ListItem(val commentIds: List<Long>? = null, val comment: Comment? = null,
                                val storyText: CharSequence? = null, val indentationLevel: Int = 0,
                                val noCommentMessage: CharSequence? = null) {

        fun getType(): Int {
            return when {
                comment != null -> return CommentListAdapter.VIEW_TYPE_COMMENT
                commentIds != null -> return CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER
                storyText != null -> return CommentListAdapter.VIEW_TYPE_STORY_TEXT
                noCommentMessage != null -> return CommentListAdapter.VIEW_TYPE_NO_COMMENTS
                else -> throw IllegalStateException("Unknown view type for: " + this)
            }
        }

    }

    fun highlightComment(commentId: Long) {
        commentIdToHightlight = commentId
    }
}
