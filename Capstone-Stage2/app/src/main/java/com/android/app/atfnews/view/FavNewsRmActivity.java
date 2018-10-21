/*
package com.android.app.atfnews.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.controller.AtfNewsItmAdapter;
import com.android.app.atfnews.model.AtfNewsItem;
import com.android.app.atfnews.model.FavoriteAtfNewsItem;
import com.android.app.atfnews.model.FirebaseFavAtfNewsItem;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.PrefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavNewsRmActivity extends AppCompatActivity implements AtfNewsItmAdapter.AtfNewsItemClickListener {

    private static final String TAG = "FavNewsRmActivity.class";
    private AppDatabase mDb;
    private FirebaseFavAtfNewsItem firebaseFavAtfNewsItem;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private ValueEventListener valueEventListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("favatfnewsitem");
        mDb = AppDatabase.getsInstance(getApplicationContext());
        progressDialog = new ProgressDialog(FavNewsRmActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }


    @Override
    public void onAtfNewsItemClick(int clickedIndex) {

    }

    @Override
    public void onFavAtfNewsItemClickAtAtfNewsActivity(int clickedIndex, ImageView favIconInactive, Drawable favIconActive) {

    }

    @Override
    public void onRemoveFavAtfNewsItemClickAtAtfNewsActivity(AtfNewsItem atfNewsItem, int clickedIndex) {
        Toast.makeText(this, "Clicked at news item number:" + clickedIndex, Toast.LENGTH_SHORT);
        removeFromFavWithUser(atfNewsItem);
    }

    private void removeFromFavWithUser(AtfNewsItem atfNewsItem) {
        new AsyncDBTaskForFavNewsDeletion().execute(atfNewsItem);
    }

    private String getUserEmailId() {
        return PrefUtils.getCurrentUser(this).getEmail();
    }

    private void removeFromFb(final AtfNewsItem atfNewsItem) {
        valueEventListener = (new ValueEventListener() {
            Boolean isFavItemDeleted = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFavItemDeleted) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot != null && null != dataSnapshot.getValue()) {
                            firebaseFavAtfNewsItem = dataSnapshot1.getValue(FirebaseFavAtfNewsItem.class);
                            if (firebaseFavAtfNewsItem != null && firebaseFavAtfNewsItem.getUrl().equalsIgnoreCase(atfNewsItem.getUrl())) {
                                String key = mUserFirebaseDatabaseReference.getKey();
                                mUserFirebaseDatabaseReference.child(key).removeValue();
                                isFavItemDeleted = true;
                            }
                        } else {
                            break;
                        }

                    }
                }

                if (isFavItemDeleted) {
                    startIntent();
                    Log.i(TAG, "Record for FavItemNews for this user has been deleted from Firebase");
                } else {
                    Log.i(TAG, "Record for FavItemNews for this user doesnt exist in Firebase for deletion");
                }
                // }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);


    }

    private void startIntent() {
        Intent favintent = new Intent(this, FavNewsDpActivity.class);
        startActivity(favintent);
    }


    public class AsyncDBTaskForFavNewsDeletion extends AsyncTask<AtfNewsItem, Void, FavoriteAtfNewsItem> {
        @Override
        protected FavoriteAtfNewsItem doInBackground(final AtfNewsItem... atfNewsItems) {
            final AtfNewsItem atfNewsItm = atfNewsItems[0];
            if (atfNewsItm != null) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        FavoriteAtfNewsItem mFavAtfNewsItem = mDb.favoriteAtfNewsItemDAO().getFavouriteNewsItemForUser(getUserEmailId(), atfNewsItm.getUrl());
                        mDb.favoriteAtfNewsItemDAO().deleteFavAtfNewsItem(mFavAtfNewsItem);
                        removeFromFb(atfNewsItm);
                        String key = mUserFirebaseDatabaseReference.getKey();
                        mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(FavoriteAtfNewsItem mFavoriteAtfNewsItem) {
            // Lets update UI now
            if (mFavoriteAtfNewsItem != null) {
                Toast.makeText(FavNewsRmActivity.this, "We removed from your favorite section", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FavNewsRmActivity.this, "Failed to remove favorite due to some interruption. Please try again later.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}*/
