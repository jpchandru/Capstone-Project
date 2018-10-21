package com.android.app.atfnews.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsItem;
import com.android.app.atfnews.model.FavoriteAtfNewsItem;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.PrefUtils;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FavNewsAdapter extends RecyclerView.Adapter<FavNewsAdapter.ViewHolder> {
    private static final String TAG = "FavNewsAdapter.class";
    List<AtfNewsItem> mAtfNewsItemList;
    Context vContext;
    private AppDatabase mDb;
    private Context mContext;
    int userId;
    SharedPreferences.Editor editor;


    public FavNewsAdapter(List<AtfNewsItem> mAtfNewsItemList, Context vContext, int userId, SharedPreferences.Editor editor) {
        this.mAtfNewsItemList = mAtfNewsItemList;
        this.vContext = vContext;
        this.userId = userId;
        this.editor = editor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AtfNewsItem atfNewsItem = mAtfNewsItemList.get(position);
        //holder.name.setText(trailer.getName());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "Position clicked is:" + position);
                /*Intent watchTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getUrl()));
                watchTrailer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                vContext.startActivity(watchTrailer);*/
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                       // mDb.favoriteAtfNewsItemDAO().insert(new FavoriteAtfNewsItem(PrefUtils.getCurrentUser(getApplicationContext()).getId(), atfNewsItem.getUrl()));
                    }
                });

                editor.putString(atfNewsItem.getTitle(), "true");
                editor.putString(atfNewsItem.getUrl(), "true");
                editor.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mAtfNewsItemList != null) {
            return mAtfNewsItemList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            //name = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            itemView.setOnClickListener(this);

        }

        private void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }
    }
}
