package com.dgsd.hackernews.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HnItem {

    @SerializedName("id")
    int id;

    @SerializedName("by")
    String by;

    @SerializedName("descendants")
    int descendants;

    @SerializedName("parent")
    int parent;

    @SerializedName("score")
    int score;

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
    List<Integer> parts;

    @SerializedName("kids")
    List<Integer> kids;

    public int getId() {
        return this.id;
    }

    public String getBy() {
        return this.by;
    }

    public int getDescendants() {
        return this.descendants;
    }

    public int getParent() {
        return this.parent;
    }

    public int getScore() {
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

    public List<Integer> getParts() {
        return parts;
    }

    public List<Integer> getKids() {
        return kids;
    }
}
