package com.darwin.randim;

import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences {

    private static final String PREF_LAST_TAG = "lastTag";
    private static final String PREF_LAST_TIME = "lastTime";
    private static final String PREF_LAST_INDEX = "lastIndex";

    public static String getLastTag(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_TAG, null);
    }
    public static void setLastTag(Context context, String lastResult) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_TAG, lastResult)
                .apply();
    }
    public static String getLastTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_TIME, null);
    }
    public static void setLastTime(Context context, String lastResult) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_TIME, lastResult)
                .apply();
    }
    public static String getLastPhotoIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_INDEX, null);
    }
    public static void setLastPhotoIndex(Context context, String lastResult) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_INDEX, lastResult)
                .apply();
    }
}
