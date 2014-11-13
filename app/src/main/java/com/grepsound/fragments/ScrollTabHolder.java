package com.grepsound.fragments;

import android.support.v7.widget.RecyclerView;

public interface ScrollTabHolder {

	void adjustScroll(int scrollHeight);

	void onScroll(RecyclerView view, int dy, int pagePosition);
}
