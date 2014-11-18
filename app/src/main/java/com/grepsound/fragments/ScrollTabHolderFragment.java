package com.grepsound.fragments;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;

public abstract class ScrollTabHolderFragment extends Fragment implements ScrollTabHolder {

	protected ScrollTabHolder mScrollTabHolder;

	public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
		mScrollTabHolder = scrollTabHolder;
	}

	@Override
	public void onScroll(RecyclerView view, int firstVisiblePos, int pagePosition) {
		// nothing
	}

}