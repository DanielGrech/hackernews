package com.dgsd.android.hackernews.view

import android.content.Context
import android.graphics.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.dgsd.android.hackernews.R
import com.dgsd.android.hackernews.adapter.CommentListAdapter
import com.dgsd.android.hackernews.util.children
import com.dgsd.android.hackernews.util.getCommentColorForIndentation
import com.dgsd.android.hackernews.util.onPreDraw
import com.dgsd.hackernews.model.Comment
import com.dgsd.hackernews.model.Story
import org.jetbrains.anko.dimen
import org.jetbrains.anko.dip

public class CommentRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView (context, attrs, defStyle) {

    private var onClickListener: (Comment, View) -> Unit = { s, v -> }

    private var onShareCommentLinkListener: (Comment) -> Unit = { c -> }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = CommentListAdapter()

        addItemDecoration(IndentItemDecoration(context,
                context.dimen(R.dimen.padding_default),
                context.dimen(R.dimen.padding_default),
                context.dip(1),
                context.dimen(R.dimen.comment_list_item_circle_indicator_radius)))

        with (adapter as CommentListAdapter) {
            setOnCommentClickListener { Comment, view ->
                onClickListener(Comment, view)
            }

            setOnCommentLongClickListener { comment, view ->
                with (PopupMenu(context, view)) {
                    inflate(R.menu.cm_share_comment)

                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.share_link -> {
                                onShareCommentLinkListener(comment)
                            }
                        }

                        true
                    }

                    show()
                }
            }
        }
    }

    public fun setStory(story: Story) {
        (adapter as CommentListAdapter).setStory(story)
    }

    public fun setOnShareCommentLinkListener(listener: (Comment) -> Unit) {
        onShareCommentLinkListener = listener
    }

    fun showNoCommentsMessage(message: String) {
        (adapter as CommentListAdapter).showNoCommentsMessage(message)
    }

    public fun setOnCommentClickListener(listener: (Comment, View) -> Unit) {
        (adapter as CommentListAdapter).setOnCommentClickListener(listener)
    }

    public fun setOnCommentPlaceholderClickListener(listener: (List<Long>, View) -> Unit) {
        (adapter as CommentListAdapter).setOnCommentPlaceholderClickListener(listener)
    }

    public class IndentItemDecoration(val context: Context, val indentSize: Int, val verticalPadding: Int, val lineWidth: Int, val circleRadius: Int) : RecyclerView.ItemDecoration() {

        private val linePaint: Paint

        private val circlePaint: Paint

        private val path: Path

        private val dottedPathEffect: DashPathEffect

        init {
            linePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = lineWidth.toFloat()

            circlePaint = Paint(linePaint)
            circlePaint.style = Paint.Style.FILL_AND_STROKE

            path = Path()

            dottedPathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        }

        override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(canvas, parent, state)

            parent.children().forEach {
                val vh = parent.getChildViewHolder(it) as CommentListAdapter.CommentViewHolder
                val deepestIndentation = vh.getIndentationLevel()
                val isComment = vh.itemViewType == CommentListAdapter.VIEW_TYPE_COMMENT
                val isCommentPlaceholder = vh.itemViewType == CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER

                for (indentation in deepestIndentation downTo 1) {
                    val drawAsDotted = indentation != deepestIndentation

                    val lineStartX = (indentation * indentSize).toFloat()
                    val lineEndX = (indentation * indentSize).toFloat()
                    var lineStartY = parent.layoutManager.getDecoratedTop(it).toFloat()
                    var lineEndY = parent.layoutManager.getDecoratedBottom(it).toFloat()

                    if (isComment && !drawAsDotted) {
                        lineStartY += (vh.itemView as CommentListItemView).getHeaderIndicatorY()
                    }

                    if (!drawAsDotted && isCommentPlaceholder) {
                        lineEndY -= verticalPadding
                    }

                    circlePaint.color = getCommentColorForIndentation(context, indentation)
                    linePaint.color = circlePaint.color
                    linePaint.setPathEffect(if (drawAsDotted) dottedPathEffect else null)

                    path.reset()
                    path.moveTo(lineStartX, lineStartY)
                    path.lineTo(lineEndX, lineEndY)

                    canvas.drawPath(path, linePaint)

                    if (isComment && !drawAsDotted) {
                        canvas.drawCircle(lineStartX, lineStartY, circleRadius.toFloat(), circlePaint)
                    }
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val vh = parent.getChildViewHolder(view) as CommentListAdapter.CommentViewHolder
            val indentLevel = vh.getIndentationLevel()
            outRect.left = indentSize * indentLevel

            val isLastItem = vh.position == parent.adapter.itemCount - 1
            val isCommentPlaceholder = vh.itemViewType == CommentListAdapter.VIEW_TYPE_COMMENT_PLACEHOLDER

            if (isLastItem) {
                outRect.bottom = verticalPadding

                if (indentLevel == 0 && isCommentPlaceholder) {
                    outRect.top = verticalPadding
                }
            }

            if (isCommentPlaceholder) {
                outRect.bottom = verticalPadding
            }
        }
    }

    fun setCommentPlaceholderLoading(commentIds: List<Long>, showLoading: Boolean) {
        (adapter as CommentListAdapter).setCommentPlaceholderLoading(commentIds, showLoading)
    }

    fun scrollToComment(commentId: Long) {
        for (pos in 0 rangeTo adapter.itemCount - 1) {
            if (adapter.getItemId(pos) == commentId) {
                (adapter as CommentListAdapter).highlightComment(commentId)
                adapter.notifyItemChanged(pos)

                onPreDraw {
                    smoothScrollToPosition(pos)

                    true
                }

                break
            }
        }
    }
}
