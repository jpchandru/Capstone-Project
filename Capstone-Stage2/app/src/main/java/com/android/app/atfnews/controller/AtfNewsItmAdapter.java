package com.android.app.atfnews.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsItem;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AtfNewsItmAdapter extends RecyclerView.Adapter<AtfNewsItmAdapter.AtfNewsItemViewHolder> {

    private static final String TAG = "AtfNewsItmAdapter.class";

    List<AtfNewsItem> mAtfNewsItemList, mFavAtfNewsItemList;
    Context mContext;
    Boolean isFavItem = false;
    public AtfNewsItemClickListener mOnClickListener;

    public interface AtfNewsItemClickListener {
        void onAtfNewsItemClick(int clickedIndex);

        void onAddFavAtfNewsItemClickAtAtfNewsActivity(int clickedIndex);

        void onRemoveFavAtfNewsItemClickAtAtfNewsActivity(AtfNewsItem atfNewsItem, int clickedIndex);
    }

    public AtfNewsItmAdapter(Context context, List<AtfNewsItem> atfNewsItemList, Boolean isFavItem, AtfNewsItemClickListener listener) {
        this.mContext = context;
        this.mAtfNewsItemList = atfNewsItemList;
        this.isFavItem = isFavItem;
        this.mOnClickListener = listener;
    }

    public AtfNewsItmAdapter(Context context, List<AtfNewsItem> atfNewsItemList, List<AtfNewsItem> mFavAtfNewsItemList, AtfNewsItemClickListener listener) {
        this.mContext = context;
        this.mAtfNewsItemList = atfNewsItemList;
        this.mFavAtfNewsItemList = mFavAtfNewsItemList;
        this.mOnClickListener = listener;
    }

    @Override
    public AtfNewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_list, parent, false);
        return new AtfNewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AtfNewsItemViewHolder holder, int position) {
        holder.bindTo(mAtfNewsItemList.get(position), mFavAtfNewsItemList);
    }

    @Override
    public int getItemCount() {
        if (mAtfNewsItemList != null && mAtfNewsItemList.size() != 0) {
            return mAtfNewsItemList.size();
        } else
            return 0;
    }

    class AtfNewsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView atfNewsItemImageView, favIconActive, favIconInActive;
        TextView atfNewsItemTitleTextVIew;

        public AtfNewsItemViewHolder(View itemView) {
            super(itemView);
            atfNewsItemImageView = itemView.findViewById(R.id.iv_news_item_image);
            favIconActive = itemView.findViewById(R.id.tv_fav_news_icon_active);
            favIconInActive = itemView.findViewById(R.id.tv_fav_news_icon_inactive);
            atfNewsItemTitleTextVIew = itemView.findViewById(R.id.tv_news_item_title);
            itemView.setOnClickListener(this);
        }

        public void bindTo(final AtfNewsItem atfNewsItem, List<AtfNewsItem> mFavAtfNewsItemList) {
            if ((mFavAtfNewsItemList != null && mFavAtfNewsItemList.size() > 0 && mFavAtfNewsItemList.contains(atfNewsItem))
                    || isFavItem) {
                favIconInActive.setVisibility(View.GONE);
                favIconActive.setVisibility(View.VISIBLE);
            } else {
                favIconActive.setVisibility(View.GONE);
                favIconInActive.setVisibility(View.VISIBLE);
            }
            if (!atfNewsItem.getTitle().equals("")) {
                if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (atfNewsItem.getTitle().length() > 75)
                        atfNewsItemTitleTextVIew.setText(atfNewsItem.getTitle().substring(0, 80) + "...");
                    else
                        atfNewsItemTitleTextVIew.setText(atfNewsItem.getTitle());
                } else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (atfNewsItem.getTitle().length() > 50)
                        atfNewsItemTitleTextVIew.setText(atfNewsItem.getTitle().substring(0, 50) + "...");
                    else
                        atfNewsItemTitleTextVIew.setText(atfNewsItem.getTitle());
                }
            }

            // Check if there is a atfNewsItem image. If so, load it in the ImageView
            String atfNewsItemImageString = atfNewsItem.getImgUrl();
            if (atfNewsItemImageString != null && !atfNewsItemImageString.equalsIgnoreCase("null")) {
                Picasso.get().load(atfNewsItemImageString)
                        .resize(160, 120)
                        .into(atfNewsItemImageView);


            } else {
                Glide.with(mContext)
                        .load(R.drawable.noimageavailable)
                        .asBitmap()
                        .override(160, 120)
                        .into(atfNewsItemImageView);

            }

            favIconInActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onAddFavAtfNewsItemClickAtAtfNewsActivity(clickedPosition);
                    favIconActive.setVisibility(View.VISIBLE);
                    favIconInActive.setVisibility(View.GONE);
                }
            });

            favIconActive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onRemoveFavAtfNewsItemClickAtAtfNewsActivity(atfNewsItem, clickedPosition);
                    favIconActive.setVisibility(View.GONE);
                    favIconInActive.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onClick(View itemView) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onAtfNewsItemClick(clickedPosition);
        }
    }
}
