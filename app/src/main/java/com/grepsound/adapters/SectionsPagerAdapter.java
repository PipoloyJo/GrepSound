/*
 *  Copyright (C) 2004-2014 Savoir-Faire Linux Inc.
 *
 *  Author: Alexandre Lision <alexandre.lision@savoirfairelinux.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  If you modify this program, or any covered work, by linking or
 *  combining it with the OpenSSL project's OpenSSL library (or a
 *  modified version of that library), containing parts covered by the
 *  terms of the OpenSSL or SSLeay licenses, Savoir-Faire Linux Inc.
 *  grants you additional permission to convey the resulting work.
 *  Corresponding Source for a non-source form of such a combination
 *  shall include the source code for the parts of OpenSSL used as well
 *  as that of the covered work.
 */

package com.grepsound.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Switch;
import com.grepsound.R;
import com.grepsound.fragments.LikesFragment;
import com.grepsound.fragments.PlaylistsFragment;
import com.grepsound.fragments.ScrollTabHolder;
import com.grepsound.fragments.ScrollTabHolderFragment;
import com.grepsound.views.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Locale;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private static final String TAG = SectionsPagerAdapter.class.getSimpleName();
    Context mContext;
    private SparseArray<ScrollTabHolder> mScrollTabHolders;
    private ScrollTabHolder mListener;

    public SectionsPagerAdapter(Context c, FragmentManager fm) {
        super(fm);
        mContext = c;
        mScrollTabHolders = new SparseArray<ScrollTabHolder>();
    }

    public SparseArray<ScrollTabHolder> getScrollTabHolders() {
        return mScrollTabHolders;
    }

    public void setTabHolderScrollingContent(ScrollTabHolder listener) {
        mListener = listener;
    }

    @Override
    public Fragment getItem(int i) {

        ScrollTabHolderFragment fragment;
        switch (i) {
            case 0:
                fragment = new LikesFragment();
                break;
            case 1:
                fragment = new PlaylistsFragment();
                break;
            default:
                throw new InstantiationError("No other position for this Viewpager");
        }

        mScrollTabHolders.put(i, fragment);
        if (mListener != null) {
            fragment.setScrollTabHolder(mListener);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
        case 0:
            return mContext.getString(R.string.title_section1).toUpperCase(Locale.getDefault());
        case 1:
            return mContext.getString(R.string.title_section2).toUpperCase(Locale.getDefault());
        default:
            Log.e(TAG, "getPageTitle: unknown tab position " + position);
            break;
        }
        return null;
    }

    @Override
    public int getPageIconResId(int position) {
        switch (position) {
        case 0:
            return R.drawable.ic_action_favorite_white;
        case 1:
            return R.drawable.ic_action_playlist;
        default:
            Log.e(TAG, "getPageTitle: unknown tab position " + position);
            break;
        }
        return 0;
    }
}
