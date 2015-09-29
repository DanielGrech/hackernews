package com.dgsd.android.hackernews.data

import com.lacronicus.easydatastorelib.BooleanEntry
import com.lacronicus.easydatastorelib.Preference

public interface AppSettings {

    @Preference("show_links_first")
    fun showLinksFirst(): BooleanEntry

}