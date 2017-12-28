package com.news.helix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {


    public CustomAdapter(Context context, String[] list) {
        super(context, R.layout.custom_row, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mainInflater = (LayoutInflater.from(getContext()));
        View customView = mainInflater.inflate(R.layout.custom_row, parent, false);

        String singleFoodItem = getItem(position);
        TextView mainText = (TextView) customView.findViewById(R.id.mainText);
        ImageView mainImage = (ImageView) customView.findViewById(R.    id.mainImage);

        mainText.setText(singleFoodItem);
        mainImage.setImageResource(R.mipmap.images);
        return customView;
    }
}
