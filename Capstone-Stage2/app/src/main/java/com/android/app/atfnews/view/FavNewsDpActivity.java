package com.android.app.atfnews.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.controller.AtfNewsItmAdapter;
import com.android.app.atfnews.model.AtfNewsItem;
import com.android.app.atfnews.model.FavoriteAtfNewsItem;
import com.android.app.atfnews.model.FavoriteNewsViewModel;
import com.android.app.atfnews.model.FirebaseFavAtfNewsItem;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavNewsDpActivity extends AppCompatActivity implements AtfNewsItmAdapter.AtfNewsItemClickListener {

    private static final String TAG = "FavNewsDpActivity.class";
    private AppDatabase mDb;
    private AtfNewsItmAdapter mFavAtfNewsItemAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_newsitem)
    RecyclerView mRecyclerView;
    @BindView(R.id.spinner)
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    private FirebaseFavAtfNewsItem firebaseFavAtfNewsItem;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private ValueEventListener valueEventListener;
    List<FirebaseFavAtfNewsItem> favAtfNewsItemListFb;
    List<AtfNewsItem> atfNewsItemList = null;
    AtfNewsItem atfNewsItemFbCopy = null;
    @BindView(R.id.adView)
    AdView mAdView;
    private static final String FIREBASE_TABLE_FAVNEWS = "favatfnewsitem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getUserEmailId() == null) {
            Intent li = new Intent(FavNewsDpActivity.this, LoginActivity.class);
            startActivity(li);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_news);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference(FIREBASE_TABLE_FAVNEWS);
        mDb = AppDatabase.getsInstance(getApplicationContext());
        layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        showFavoriteNewsView(getUserEmailId());
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private String getUserEmailId() {
        if (PrefUtils.getCurrentUser(FavNewsDpActivity.this) != null) {
            return PrefUtils.getCurrentUser(FavNewsDpActivity.this).email;
        }
        return null;
    }


    private void setmFavNewsRecyclerView(List<AtfNewsItem> atfNewsItems, Boolean isFavItem) {

        mFavAtfNewsItemAdapter = new AtfNewsItmAdapter(FavNewsDpActivity.this, atfNewsItems, isFavItem, this);
        mRecyclerView.setAdapter(mFavAtfNewsItemAdapter);
        mFavAtfNewsItemAdapter.notifyDataSetChanged();
    }

    private void showFavoriteNewsView(String userId) {
        Log.d("Display ", " FAVORITE UI");
        setupFavViewModel(userId);
        Toast.makeText(this, "Displaying your favorite news", Toast.LENGTH_LONG).show();
        if(!validNetworkStatus()) Toast.makeText(this, "Some of your previously saved news (if any) might not be in sync during offline.", Toast.LENGTH_LONG).show();
    }

    private void setupFavViewModel(String emailId) {
        mDb = AppDatabase.getsInstance(getApplicationContext());
        FavoriteNewsViewModel viewModel = ViewModelProviders.of(this).get(FavoriteNewsViewModel.class);
        viewModel.getFavoriteAtfNewsItemDAO().getAllFavouriteNewsItemForUser(emailId).observe(this, new Observer<List<AtfNewsItem>>() {
            Boolean dataRead = false;

            @Override
            public void onChanged(@Nullable List<AtfNewsItem> atfNewsItemListLocal) {
                if (!dataRead) {
                    Log.d(TAG, "Retrieving LiveData using Rooms in FavoriteNewsViewModel");
                    if (atfNewsItemListLocal != null && atfNewsItemListLocal.size() > 0) {
                        if (validNetworkStatus())
                            chkAndRetrieveFavAtfNewsItemFromFb(true, atfNewsItemListLocal);
                        else
                            setmFavNewsRecyclerView(atfNewsItemListLocal, true);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Retrieving Firebase data for users favorite news during device change ..");
                        // check if it exists in fb to load fav data while reinistalling atfnews incase of device change or not.
                        if (validNetworkStatus())
                            chkAndRetrieveFavAtfNewsItemFromFb(true, null);
                        else
                            Toast.makeText(FavNewsDpActivity.this, "We could retrieve your previously saved favourite news (if any) but you seem to be offline.", Toast.LENGTH_SHORT).show();

                    }
                    dataRead = true;
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean validNetworkStatus() {
        if (Utils.isNetworkAvailable(this)) return true;
        else return false;
    }


    private void chkAndRetrieveFavAtfNewsItemFromFb(boolean isReady, @Nullable final List<AtfNewsItem> atfNewsItemListLocal) {
        favAtfNewsItemListFb = new ArrayList<FirebaseFavAtfNewsItem>();
        atfNewsItemList = new ArrayList<AtfNewsItem>();
        if (isReady)
            valueEventListener = (new ValueEventListener() {
                Boolean dataRead = false;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataRead) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String key = dataSnapshot1.getKey();
                            firebaseFavAtfNewsItem = dataSnapshot1.getValue(FirebaseFavAtfNewsItem.class);
                            if (firebaseFavAtfNewsItem != null && firebaseFavAtfNewsItem.getEmailId().equalsIgnoreCase(getUserEmailId())) {
                                if (!favAtfNewsItemListFb.contains(firebaseFavAtfNewsItem))
                                    favAtfNewsItemListFb.add(firebaseFavAtfNewsItem);
                            }
                            dataRead = true;
                        }

                        for (FirebaseFavAtfNewsItem favAtfNewsItemFbCopy : favAtfNewsItemListFb) {
                            atfNewsItemFbCopy = new AtfNewsItem();
                            atfNewsItemFbCopy.setAuthor(favAtfNewsItemFbCopy.getAuthor());
                            atfNewsItemFbCopy.setCategory(favAtfNewsItemFbCopy.getCategory());
                            atfNewsItemFbCopy.setContent(favAtfNewsItemFbCopy.getContent());
                            atfNewsItemFbCopy.setCountry(favAtfNewsItemFbCopy.getCountry());
                            atfNewsItemFbCopy.setDescription(favAtfNewsItemFbCopy.getDescription());
                            atfNewsItemFbCopy.setEmailId(favAtfNewsItemFbCopy.getEmailId());
                            atfNewsItemFbCopy.setImgUrl(favAtfNewsItemFbCopy.getImgUrl());
                            atfNewsItemFbCopy.setNewsType(favAtfNewsItemFbCopy.getNewsType());
                            atfNewsItemFbCopy.setPublishDate(favAtfNewsItemFbCopy.getPublishDate());
                            atfNewsItemFbCopy.setTitle(favAtfNewsItemFbCopy.getTitle());
                            atfNewsItemFbCopy.setUrl(favAtfNewsItemFbCopy.getUrl());
                            atfNewsItemList.add(atfNewsItemFbCopy);
                        }
                    }

                    /*if (atfNewsItemListLocal != null){
                        for (AtfNewsItem atfNewsItem : atfNewsItemListLocal) {
                            if (atfNewsItemList.contains(atfNewsItem)) {
                                continue;
                            } else {
                                atfNewsItemFbCopy = new AtfNewsItem();
                                atfNewsItemFbCopy = atfNewsItem;
                                atfNewsItemList.add(atfNewsItemFbCopy);
                            }
                        }
                    }*/

                    setmFavNewsRecyclerView(atfNewsItemList, true);
                    //Toast.makeText(FavNewsDpActivity.this, "We saved your previously added favourite news. Enjoy!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        if (isReady)
            mUserFirebaseDatabaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onAtfNewsItemClick(int clickedIndex) {

    }

    @Override
    public void onAddFavAtfNewsItemClickAtAtfNewsActivity(int clickedIndex) {

    }

    @Override
    public void onRemoveFavAtfNewsItemClickAtAtfNewsActivity(AtfNewsItem atfNewsItem, int clickedIndex) {
        final String atfnewsItemUrl = atfNewsItem.getUrl();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteAtfNewsItemDAO().deleteFavAtfNewsItem(new FavoriteAtfNewsItem(getUserEmailId(), atfnewsItemUrl));// emailid and url
                removeFromFb(atfnewsItemUrl);
                String key = mUserFirebaseDatabaseReference.getKey();
                mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                if (!validNetworkStatus()) setupFavViewModel(getUserEmailId());
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void removeFromFb(final String atfnewsItemUrl) {
        valueEventListener = (new ValueEventListener() {
            Boolean isFavItemDeleted = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isFavItemDeleted) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot != null && null != dataSnapshot.getValue()) {
                            firebaseFavAtfNewsItem = dataSnapshot1.getValue(FirebaseFavAtfNewsItem.class);
                            if (firebaseFavAtfNewsItem != null && firebaseFavAtfNewsItem.getUrl().equalsIgnoreCase(atfnewsItemUrl)) {
                                String key = dataSnapshot1.getKey();
                                mUserFirebaseDatabaseReference.child(key).removeValue();
                                isFavItemDeleted = true;
                                break;
                            }
                        } else {
                            break;
                        }

                    }
                }

                if (isFavItemDeleted) {
                    setupFavViewModel(getUserEmailId());
                    String key = mUserFirebaseDatabaseReference.getKey();
                    mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                    progressBar.setVisibility(View.GONE);
                    Log.i(TAG, "Record for FavItemNews for this user has been deleted from Firebase");
                } else {
                    Log.i(TAG, "Record for FavItemNews for this user doesnt exist in Firebase for deletion");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (mUserFirebaseDatabaseReference != null && valueEventListener != null) {
            String key = mUserFirebaseDatabaseReference.getKey();
            mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        }
        Glide.get(this).clearMemory();
    }
}