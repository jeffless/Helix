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

class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {
    public TextView txtTitle, txtPubDate, txtContent;
    private ItemClickListener itemClickListener;

    public FeedViewHolder(View itemView) {
        super(itemView);

        txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        txtPubDate = (TextView) itemView.findViewById(R.id.txtPubDate);
        txtContent = (TextView) itemView.findViewById(R.id.txtContent);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private RSSObject rssObject;
    private Context mContext;
    private LayoutInflater inflater;

    private String title, pubDate, content;

    public FeedAdapter(RSSObject rssObject, Context mContext) {
        this.rssObject = rssObject;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public FeedAdapter(String title, String pubDate, String content, Context mContext) {
        this.title = title;
        this.pubDate = pubDate;
        this.content = content;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.custom_row, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {

        if(rssObject != null)
        {
            holder.txtTitle.setText(rssObject.getItems().get(position).getTitle());
            holder.txtPubDate.setText(rssObject.getItems().get(position).getPubDate());
            holder.txtContent.setText(rssObject.getItems().get(position).getContent());
        }

        else {
            holder.txtTitle.setText(title);
            holder.txtPubDate.setText(pubDate);
            holder.txtContent.setText(content);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(rssObject.getItems().get(position).getLink()));
                    mContext.startActivity(browserIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rssObject.items.size();
    }
}
