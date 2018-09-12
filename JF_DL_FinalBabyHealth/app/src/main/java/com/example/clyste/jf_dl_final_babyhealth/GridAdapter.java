package com.example.clyste.jf_dl_final_babyhealth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

    private final Context mContext;
    private final GridCell[] categories;

    //
    public GridAdapter(Context context, GridCell[] categories) {
        this.mContext = context;
        this.categories = categories;
    }

    // 2
    @Override
    public int getCount() {
        return categories.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.cell_layout, null);
        }

        ImageView image = convertView.findViewById(R.id.category_image);
        TextView title = convertView.findViewById(R.id.category_title);
        image.setImageResource(categories[position].getImage());
        title.setText(categories[position].getName());

        return convertView;
    }

}