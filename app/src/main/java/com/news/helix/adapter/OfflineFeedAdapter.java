package com.news.helix.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.news.helix.R;
import com.news.helix.model.RSSObject;

import java.util.ArrayList;

public class OfflineFeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;

    private ArrayList<String> title, pubDate, content;
    private int itemCount;

    public OfflineFeedAdapter(ArrayList<String> title, ArrayList<String> pubDate,
                              ArrayList<String> content, int itemCount, Context mContext) {
        this.title = title;
        this.pubDate = pubDate;
        this.content = content;
        this.mContext = mContext;
        this.itemCount = itemCount;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.custom_row, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        holder.txtTitle.setText(title.get(position));
        holder.txtPubDate.setText(pubDate.get(position));
        holder.txtContent.setText(content.get(position));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
