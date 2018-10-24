package com.android.app.atfnews.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
    private AtfNewsItmAdapter mAtfNewsItemAdapter, mFavAtfNewsItemAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_newsitem)
    RecyclerView mRecyclerView;
    @BindView(R.id.spinner)
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    private FirebaseFavAtfNewsItem firebaseFavAtfNewsItem;
    SharedPreferences sharedPreferences;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private ValueEventListener valueEventListener;
    List<FirebaseFavAtfNewsItem> favAtfNewsItemListFb;
    List<AtfNewsItem> atfNewsItemList = null;
    AtfNewsItem atfNewsItemFbCopy = null;

    //private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("favatfnewsitem");
        mDb = AppDatabase.getsInstance(getApplicationContext());
        layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        showFavoriteNewsView(getUserEmailId());
        progressBar.setVisibility(View.VISIBLE);


    }

    private String getUserEmailId() {
        return PrefUtils.getCurrentUser(FavNewsDpActivity.this).email;
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
    }

    private void setupFavViewModel(String emailId) {
        mDb = AppDatabase.getsInstance(getApplicationContext());
        FavoriteNewsViewModel viewModel = ViewModelProviders.of(this).get(FavoriteNewsViewModel.class);
        viewModel.getFavoriteAtfNewsItemDAO().getAllFavouriteNewsItemForUser(emailId).observe(this, new Observer<List<AtfNewsItem>>() {
            @Override
            public void onChanged(@Nullable List<AtfNewsItem> atfNewsItemListLocal) {
                Log.d(TAG, "Retrieving LiveData using Rooms in FavoriteNewsViewModel");
                if (atfNewsItemListLocal != null && atfNewsItemListLocal.size() > 0) {
                    setmFavNewsRecyclerView(atfNewsItemListLocal, true);
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(FavNewsDpActivity.this, "Enjoy your favourite news", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Retrieving Firebase data for users favorite news during device change ..");
                    // check if it exists in fb
                    if (validNetworkStatus())
                        chkAndRetrieveFavAtfNewsItemFromFb();
                    else
                        Toast.makeText(FavNewsDpActivity.this, "We could retrieve your previously saved favourite news (if any) but you seem to be offline.", Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean validNetworkStatus() {
        if (Utils.isNetworkAvailable(this)) return true;
        else return false;
    }


    private void chkAndRetrieveFavAtfNewsItemFromFb() {
        favAtfNewsItemListFb = new ArrayList<FirebaseFavAtfNewsItem>();
        atfNewsItemList = new ArrayList<AtfNewsItem>();
        valueEventListener = (new ValueEventListener() {
            Boolean dataRead= false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataRead){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String key = dataSnapshot1.getKey();
                        firebaseFavAtfNewsItem = dataSnapshot1.getValue(FirebaseFavAtfNewsItem.class);
                        if (firebaseFavAtfNewsItem != null && firebaseFavAtfNewsItem.getEmailId().equalsIgnoreCase(getUserEmailId())) {
                            favAtfNewsItemListFb.add(firebaseFavAtfNewsItem);
                        }
                        dataRead = true;
                    }
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

                setmFavNewsRecyclerView(atfNewsItemList, true);
                //Toast.makeText(FavNewsActivity.this, "We saved your previously added favourite news. Enjoy!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addValueEventListener(valueEventListener);
    }

    private String getNewCountryCode(){
        return PrefUtils.getUrlNewsType(this);
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
                //if(iFavRemove == null){
                removeFromFb(atfnewsItemUrl);
                String key = mUserFirebaseDatabaseReference.getKey();
                mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                // }
                // url
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
                    chkAndRetrieveFavAtfNewsItemFromFb();
                    String key = mUserFirebaseDatabaseReference.getKey();
                    mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                    progressBar.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressBar != null) progressBar.setVisibility(View.GONE);
        if (mUserFirebaseDatabaseReference != null && valueEventListener != null) {
            String key = mUserFirebaseDatabaseReference.getKey();
            mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        }
        Glide.get(this).clearMemory();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}