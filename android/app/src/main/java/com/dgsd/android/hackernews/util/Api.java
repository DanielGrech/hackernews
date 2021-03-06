package com.dgsd.android.hackernews.util;

import android.os.Build;

/**
 * Simple wrapper around values in {@link android.os.Build.VERSION_CODES} to make it
 * semantically nicer to use
 */
public class Api {
    public static final int LEVEL = Build.VERSION.SDK_INT;
    public static final int KITKAT = Build.VERSION_CODES.KITKAT;
    public static final int LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;
    public static final int LOLLIPOP_MR1 = Build.VERSION_CODES.LOLLIPOP_MR1;
    public static final int MARSHMALLOW = Build.VERSION_CODES.M;

    Api() {
        // No instances..
    }

    /**
     * Check if the current version of Android the app is running on is at least on a given api version
     *
     * @param level The Api version to check. Should be one of the constants defined in this class
     * @return <code>true</code> if the current version of Android the app is running on meets this level, <code>false</code> otherwise
     */
    public static boolean isMin(int level) {
        return LEVEL >= level;
    }

    /**
     * Check if the current version of Android is app is running on at most a given api version
     *
     * @param level The Api version to check. Should be one of the constants defined in this class
     * @return <code>true</code> if the current version of Android the app is running on is no more than this level, <code>false</code> otherwise
     */
    public static boolean isUpTo(int level) {
        return LEVEL <= level;
    }

    /**
     * Check if the current version of Android the app is running on is a particular api version
     *
     * @param level The Api version to check. Should be one of the constants defined in this class
     * @return <code>true</code> if the current version of Android the app is running is the same as the given version, <code>false</code> otherwise
     */
    public static boolean is(int level) {
        return LEVEL == level;
    }
}