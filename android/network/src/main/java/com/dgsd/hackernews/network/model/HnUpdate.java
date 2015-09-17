package com.dgsd.hackernews.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents the most recent updates to HN items and profiles.
 *
 * @see <a href="https://hacker-news.firebaseio.com/v0/updates">Firebase API Reference</a>
 */
public class HnUpdate {

    @SerializedName("items")
    List<Integer> items;

    @SerializedName("profiles")
    List<String> profiles;

    public List<Integer> getItems() {
        return items;
    }

    public List<String> getProfiles() {
        return profiles;
    }
}
