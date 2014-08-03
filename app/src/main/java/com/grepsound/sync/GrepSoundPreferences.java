package com.grepsound.sync;

import android.content.Context;
import android.preference.PreferenceManager;
import com.grepsound.R;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 * <p/>
 * Alexandre Lision on 03/08/14.
 */
public class GrepSoundPreferences {

    public static int getSyncInterval(Context context) {
        int defaultValue = context.getResources().getIntArray(R.array.sync_interval_strings)[0];
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_interval", String.valueOf(defaultValue)));
    }

    public static int getCacheLikesCount(Context context) {
        int defaultValue = context.getResources().getIntArray(R.array.cache_likes_count_strings)[0];
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("cache_likes_count", String.valueOf(defaultValue)));
    }

    public static boolean downloadOnlyOverWifi(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wifi_only", false);
    }

    public static boolean downloadOnlyWhileCharging(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("charging_only", false);
    }
}
