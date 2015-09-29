package com.dgsd.android.hackernews.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dgsd.android.hackernews.BuildConfig

private val PLAY_STORE_LINK = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

public fun getPlayStoreIntent(): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_LINK));
}

public fun Intent?.isAvailable(context: Context): Boolean {
    return this != null && this.resolveActivity(context.packageManager) != null
}