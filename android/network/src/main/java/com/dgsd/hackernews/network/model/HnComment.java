package com.dgsd.hackernews.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A comment in the custom HackerNews API
 */
public class HnComment {

    @SerializedName("id")
    long id;

    @SerializedName("author")
    String author;

    @SerializedName("comment_ids")
    List<Long> commentIds;

    @SerializedName("parent")
    long parentId;

    @SerializedName("time")
    long time;

    @SerializedName("text")
    String text;

    @SerializedName("comment_count")
    int commentCount;

    @SerializedName("comments")
    List<HnComment> comments;

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public long getParentId() {
        return parentId;
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public List<HnComment> getComments() {
        return comments;
    }
}
