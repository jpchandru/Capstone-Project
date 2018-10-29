package com.android.app.atfnews.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.controller.AtfNewsItmAdapter;
import com.android.app.atfnews.model.ATFNewsCountryType;
import com.android.app.atfnews.model.AtfNewsItem;
import com.android.app.atfnews.model.AtfNewsItemViewModel;
import com.android.app.atfnews.model.AtfNewsNavigator;
import com.android.app.atfnews.model.FavoriteAtfNewsItem;
import com.android.app.atfnews.model.FavoriteNewsViewModel;
import com.android.app.atfnews.model.FirebaseFavAtfNewsItem;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.JsonUtils;
import com.android.app.atfnews.utils.NetworkUtils;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopNewsActivity extends AppCompatActivity implements AtfNewsItmAdapter.AtfNewsItemClickListener {

    private static final String TAG = "TopNewsActivity.class";
    private AppDatabase mDb;
    List<AtfNewsItem> mAtfNewsItemList = new ArrayList<AtfNewsItem>();
    List<AtfNewsItem> mFavAtfNewsItemList = new ArrayList<AtfNewsItem>();
    private AtfNewsItmAdapter mAtfNewsItemAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_newsitem)
    RecyclerView mRecyclerView;
    @BindView(R.id.spinner)
    ProgressBar progressBar;
    @BindView(R.id.adView)
    AdView mAdView;
    LinearLayoutManager layoutManager;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private FirebaseFavAtfNewsItem firebaseFavAtfNewsItem;
    private ValueEventListener valueEventListener;
    private static final String NEWSTYPE = "top-headlines";
    private static final String CATEGORY = "business";
    int clickedItemIndex = -1;
    String countryCode, clickedCountryCode = null;
    private static final String FIREBASE_TABLE_FAVNEWS = "favatfnewsitem";
    private static final String COUNTRYCODE = "countryCode";
    private static final String CLICKEDCOUNTRYCODE = "clickedCountryCode";
    private static final String USCOUNTRYCODE = "us";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);
        ButterKnife.bind(this);
        if (PrefUtils.getCurrentUser(TopNewsActivity.this) == null) {
            Intent iLogin = new Intent(this, LoginActivity.class);
            startActivity(iLogin);
        } else {
            //super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main_news);
            //ButterKnife.bind(this);
            setSupportActionBar(toolbar);
            mDb = AppDatabase.getsInstance(getApplicationContext());
            mUserFirebaseDatabase = FirebaseDatabase.getInstance();
            mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference(FIREBASE_TABLE_FAVNEWS);
            Intent i = getIntent();
            countryCode = i.getStringExtra(COUNTRYCODE);
            clickedCountryCode = i.getStringExtra(CLICKEDCOUNTRYCODE);
            mAtfNewsItemList = new ArrayList<AtfNewsItem>();
            layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            clickedItemIndex = -1;
            progressBar.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);

            buildAndExecute(AtfNewsNavigator.trending.name());
        }
    }

    private void setmTopNewsRecyclerView(List<AtfNewsItem> atfNewsItems, List<AtfNewsItem> mFavAtfNewsItemList) {
        mAtfNewsItemList = atfNewsItems;
        mAtfNewsItemAdapter = new AtfNewsItmAdapter(this, atfNewsItems, mFavAtfNewsItemList, this);
        mRecyclerView.setAdapter(mAtfNewsItemAdapter);
        mAtfNewsItemAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.atfnews_navigator, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            buildAndExecute(AtfNewsNavigator.settings.name());
            return true;
        } else if (id == R.id.action_favorite) {
            buildAndExecute(AtfNewsNavigator.favorite.name());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTaskForNewsApiDataDownload extends AsyncTask<URL, Void, List<AtfNewsItem>> {
        @Override
        protected List<AtfNewsItem> doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults;
            try {
                searchResults = NetworkUtils.getResponseFromHTTPUrl(searchUrl);
                mAtfNewsItemList = JsonUtils.parseNewsApiJson(searchResults, NEWSTYPE, getNewsCountryCode(), CATEGORY, getUserEmailId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mAtfNewsItemList != null && mAtfNewsItemList.size() > 0) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.atfNewsItemDAO().insertAtfNewsItem(mAtfNewsItemList);

                    }
                });

            }
            return mAtfNewsItemList;
        }


        @Override
        protected void onPostExecute(final List<AtfNewsItem> mAtfNewsItemList) {
            // Lets update UI now
            if (mAtfNewsItemList != null && mAtfNewsItemList.size() > 0) {
                setUpAtfNewsItemsViewModel();
            } else {
                Toast.makeText(TopNewsActivity.this, "Failed to retrieve data due to some interruption. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUserEmailId() {
        return PrefUtils.getCurrentUser(this).email;
    }

    private void setUpAtfNewsItemsViewModel() {
        AtfNewsItemViewModel viewModel = ViewModelProviders.of(this).get(AtfNewsItemViewModel.class);
        viewModel.getAtfNewsItemsDAO().loadAllAtfNewsItem(getNewsCountryCode()).observe(this, new Observer<List<AtfNewsItem>>() {
            Boolean dataRead = false;
            @Override
            public void onChanged(@Nullable List<AtfNewsItem> atfNewsItems) {
                if(!dataRead){
                    Log.d(TAG, "Retrieving LiveData using Rooms in AtfNewsItemViewModel");
                    displayToastMessage(atfNewsItems);
                    retrieveAllFavoriteNewsData(atfNewsItems);
                    dataRead = true;
                }
            }
        });
        if(!Utils.isNetworkAvailable(this)) displayToastMessage(null);
    }

    private void retrieveAllFavoriteNewsData(final List<AtfNewsItem> atfNewsItems) {
        mDb = AppDatabase.getsInstance(getApplicationContext());
        FavoriteNewsViewModel viewModel = ViewModelProviders.of(this).get(FavoriteNewsViewModel.class);
        viewModel.getFavoriteAtfNewsItemDAO().getAllFavouriteNewsItemForUser(PrefUtils.getCurrentUser(TopNewsActivity.this).email).observe(this, new Observer<List<AtfNewsItem>>() {
            @Override
            public void onChanged(@Nullable List<AtfNewsItem> atfFavNewsItemList) {
                if (clickedItemIndex < 0) {
                    Log.d(TAG, "Retrieving All LiveData using Rooms in FavoriteNewsViewModel");
                    mFavAtfNewsItemList = new ArrayList<AtfNewsItem>();
                    mFavAtfNewsItemList = atfFavNewsItemList;
                    setmTopNewsRecyclerView(atfNewsItems, mFavAtfNewsItemList);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void insertIntoFbAndSave(final String atfNewsItemUrl) {
        FavoriteNewsViewModel viewModel = ViewModelProviders.of(this).get(FavoriteNewsViewModel.class);
        viewModel.getFavoriteAtfNewsItemDAO().getLiveFavouriteNewsItemForUser(PrefUtils.getCurrentUser(TopNewsActivity.this).email, atfNewsItemUrl).observe(this, new Observer<AtfNewsItem>() {
            @Override
            public void onChanged(@Nullable AtfNewsItem atfFavNewsItem) {
                Log.d(TAG, "Retrieving single LiveData using Rooms in FavoriteNewsViewModel");
                if (atfFavNewsItem != null) {
                    chkAndInsertFavAtfNewsItemToFb(atfFavNewsItem);
                    String key = mUserFirebaseDatabaseReference.push().getKey();
                    mUserFirebaseDatabaseReference.child(key).orderByChild("email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void chkAndInsertFavAtfNewsItemToFb(final AtfNewsItem atfNewsItem) {
        valueEventListener = (new ValueEventListener() {
            Boolean isFavItemAdded = false, isFavItemExists = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && null == dataSnapshot.getValue()) {
                    String key = mUserFirebaseDatabaseReference.push().getKey();
                    mUserFirebaseDatabaseReference.child(key).setValue(atfNewsItem);
                } else {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        firebaseFavAtfNewsItem = dataSnapshot1.getValue(FirebaseFavAtfNewsItem.class);
                        if (firebaseFavAtfNewsItem != null && firebaseFavAtfNewsItem.getUrl().equalsIgnoreCase(atfNewsItem.getUrl())) {
                            isFavItemExists = true;
                            break;

                        }
                    }
                    if (!isFavItemExists && !isFavItemAdded) {
                        String key = mUserFirebaseDatabaseReference.push().getKey();
                        mUserFirebaseDatabaseReference.child(key).setValue(atfNewsItem);
                        isFavItemAdded = true;
                        Log.i(TAG, "Record for FavItemNews for this user has been added into Firebase");
                    } else {
                        Log.i(TAG, "Record for FavItemNews for this user already exists in Firebase");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void displayToastMessage(List<AtfNewsItem> atfNewsItemList) {
        if (atfNewsItemList != null && atfNewsItemList.size() > 0 && !Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "Please continue reading atfnews based on your previous online time.", Toast.LENGTH_SHORT).show();
        } else if (atfNewsItemList == null && !Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "You are offline.", Toast.LENGTH_SHORT).show();
        } else if (atfNewsItemList == null && Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


    private void buildAndExecute(String name) {
        String newsUrlType = null;
        if (name != null) {
            newsUrlType = getNewsCountryCode();
        }
        if (name.equals(AtfNewsNavigator.settings.name())) {
            Intent FavNewsintent = new Intent(this, SettingsActivity.class);
            startActivity(FavNewsintent);
        } else if (name.equals(AtfNewsNavigator.trending.name())) {
            if (newsUrlType != null)
                showTrendingNewsView(newsUrlType);
        } else if (name.equals(AtfNewsNavigator.favorite.name())) {
            progressBar.setVisibility(View.VISIBLE);
            Fragment fragment = new AdMobFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.adView, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private String getNewsCountryCode() {
        Log.d(TAG, "Retrieving news country code");
        String newsCountryCode = null;
        if (clickedCountryCode != null && Utils.isNetworkAvailable(this)) newsCountryCode = clickedCountryCode;
        else if (PrefUtils.getUrlNewsType(this) != null)
            newsCountryCode = PrefUtils.getUrlNewsType(this);
        else newsCountryCode = USCOUNTRYCODE;
        PrefUtils.setUrlNewsType(newsCountryCode, this);
        return newsCountryCode;
    }


    private void addToFavWithUser(final int clickedIndex) {
        final String mAtfNewsUrl = mAtfNewsItemList.get(clickedIndex).getUrl();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteAtfNewsItemDAO().insert(new FavoriteAtfNewsItem(getUserEmailId(), mAtfNewsUrl));// emailid and url
                insertIntoFbAndSave(mAtfNewsUrl); // url
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void removeFavWithUser(final int clickedIndex) {
        final String atfnewsItemUrl = mAtfNewsItemList.get(clickedIndex).getUrl();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteAtfNewsItemDAO().deleteFavAtfNewsItem(new FavoriteAtfNewsItem(getUserEmailId(), atfnewsItemUrl));// emailid and url
                removeFromFb(atfnewsItemUrl);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showTrendingNewsView(String atfNewsUrlType) {
        if (atfNewsUrlType != null && Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "You are watching news of " + ATFNewsCountryType.resolve(atfNewsUrlType).getType(), Toast.LENGTH_SHORT).show();
            URL newsApiUrl = NetworkUtils.buildUrl(atfNewsUrlType);
            new AsyncHttpTaskForNewsApiDataDownload().execute(newsApiUrl);
        } else {
            //offline loading
            //Toast.makeText(TopNewsActivity.this, "You are offline. Please continue reading atfnews based on previous last online time", Toast.LENGTH_SHORT).show();
            setUpAtfNewsItemsViewModel();
        }

    }


    @Override
    public void onAtfNewsItemClick(int clickedIndex) {
        //TODO code here for content description display screen
        Log.d(TAG, "onAtfNewsItemClick: Clicked on Index Item:" + clickedIndex);
    }

    @Override
    public void onAddFavAtfNewsItemClickAtAtfNewsActivity(int clickedIndex) {
        clickedItemIndex = clickedIndex;
        addToFavWithUser(clickedIndex);
    }

    @Override
    public void onRemoveFavAtfNewsItemClickAtAtfNewsActivity(AtfNewsItem atfNewsItem, int clickedIndex) {
        removeFavWithUser(clickedIndex);
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