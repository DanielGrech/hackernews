package com.dgsd.hackernews.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A story in the custom HackerNews API
 */
public class HnStory {

    @SerializedName("id")
    long id;

    @SerializedName("author")
    String author;

    @SerializedName("type")
    String type;

    @SerializedName("comment_ids")
    List<Long> commentIds;

    @SerializedName("parts")
    List<Long> parts;

    @SerializedName("parent_id")
    long parentId;

    @SerializedName("score")
    int score;

    @SerializedName("time")
    long time;

    @SerializedName("title")
    String title;

    @SerializedName("text")
    String text;

    @SerializedName("url")
    String url;

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

    public String getType() {
        return type;
    }

    public List<Long> getCommentIds() {
        return commentIds;
    }

    public List<Long> getParts() {
        return parts;
    }

    public long getParentId() {
        return parentId;
    }

    public int getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public List<HnComment> getComments() {
        return comments;
    }
}
