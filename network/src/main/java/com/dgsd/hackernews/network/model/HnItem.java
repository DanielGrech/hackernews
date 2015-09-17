package com.dgsd.hackernews.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents a single item in the HN API.
 * <p/>
 * This could be a comment, story, job, poll or 'poll option'
 *
 * @see <a href="https://github.com/HackerNews/API#items">Github API Reference</a>
 */
public class HnItem {

    public static final String TYPE_JOB = "job";
    public static final String TYPE_STORY = "story";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_POLL = "poll";
    public static final String TYPE_POLLOPT = "pollopt";

    @SerializedName("id")
    long id;

    @SerializedName("by")
    String by;

    @SerializedName("descendants")
    Integer descendants;

    @SerializedName("parent")
    Long parent;

    @SerializedName("score")
    Integer score;

    @SerializedName("text")
    String text;

    @SerializedName("time")
    long time;

    @SerializedName("title")
    String title;

    @SerializedName("type")
    String type;

    @SerializedName("url")
    String url;

    @SerializedName("deleted")
    boolean deleted;

    @SerializedName("dead")
    boolean dead;

    @SerializedName("parts")
    List<Long> parts;

    @SerializedName("kids")
    List<Long> kids;

    public long getId() {
        return this.id;
    }

    public String getBy() {
        return this.by;
    }

    public Integer getDescendants() {
        return this.descendants;
    }

    public Long getParent() {
        return this.parent;
    }

    public Integer getScore() {
        return this.score;
    }

    public String getText() {
        return this.text;
    }

    public long getTime() {
        return this.time;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isDead() {
        return dead;
    }

    public List<Long> getParts() {
        return parts;
    }

    public List<Long> getKids() {
        return kids;
    }
}
