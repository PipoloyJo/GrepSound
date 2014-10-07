package com.grepsound.utils;

import android.content.Context;
import com.grepsound.model.Playlists;
import com.grepsound.model.Tracks;

import java.io.File;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 * <p/>
 * Alexandre Lision on 06/10/14.
 */

public class CacheManager {

    private Context mContext;
    private File mCacheDir;

    public void CacheManager(Context c) {
        mContext = c;
        mCacheDir = mContext.getExternalCacheDir();
    }


    public Playlists getPlaylists() {
        return null;
    }

    public Tracks getTracks() {
        return null;
    }
}
