package com.grepsound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.fragments.MenuFragment;
import com.grepsound.views.TypefaceTextView;

public class MenuAdapter extends BaseAdapter {

    // private static final String TAG = MenuAdapter.class.getSimpleName();
    private Context mContext;
    MenuFragment.ObjectDrawerItem categories[];

    public MenuAdapter(Context c, MenuFragment.ObjectDrawerItem[] cat) {
        mContext = c;
        categories = cat;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (v == null) {
            v = inflater.inflate(R.layout.item_menu, parent, false);
        }
        TypefaceTextView title = (TypefaceTextView) v.findViewById(R.id.menu_title_categorie);
        ImageView icon = (ImageView) v.findViewById(R.id.menu_title_categorie_icon);
        title.setText(categories[pos].name);
        icon.setImageResource(categories[pos].icon);

        return v;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public MenuFragment.ObjectDrawerItem getItem(int pos) {
        return categories[pos];
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

}
