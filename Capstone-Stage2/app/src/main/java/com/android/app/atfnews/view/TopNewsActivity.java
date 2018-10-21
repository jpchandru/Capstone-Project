package com.android.app.atfnews.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopNewsActivity extends AppCompatActivity implements AtfNewsItmAdapter.AtfNewsItemClickListener {

    private static final String TAG = "TopNewsActivity.class";
    private AppDatabase mDb;
    List<AtfNewsItem> mAtfNewsItemList = new ArrayList<AtfNewsItem>();
    List<AtfNewsItem> mFavAtfNewsItemList = new ArrayList<AtfNewsItem>();
    private AtfNewsItem mAtfNewsItem;
    private AtfNewsItmAdapter mAtfNewsItemAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_newsitem)
    RecyclerView mRecyclerView;
    @BindView(R.id.spinner)
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private FirebaseFavAtfNewsItem firebaseFavAtfNewsItem;
    private ValueEventListener valueEventListener;
    private static final String NEWSTYPE = "top-headlines";
    private static final String COUNTRY = "us";
    private static final String CATEGORY = "business";
    private static final String ATFNEWSITEMLISTKEY = "matfNewsItemListKey";
    private static final String FAVATFNEWSITEMLISTKEY = "mFavAtfNewsItemListKey";
    int clickedItemIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mDb = AppDatabase.getsInstance(getApplicationContext());
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("favatfnewsitem");


        /*if (savedInstanceState != null && savedInstanceState.containsKey(ATFNEWSITEMLISTKEY)) {
            mAtfNewsItemList = Arrays.asList((AtfNewsItem[]) savedInstanceState.getParcelableArray(ATFNEWSITEMLISTKEY));
            Log.d(TAG, "Retrieved mAtfNewsItemList data from SaveInstanceStance");
        }*/
        if (savedInstanceState != null && savedInstanceState.containsKey(FAVATFNEWSITEMLISTKEY)) {
            mFavAtfNewsItemList = Arrays.asList((AtfNewsItem[]) savedInstanceState.getParcelableArray(FAVATFNEWSITEMLISTKEY));
            Log.d(TAG, "Retrieved mFavAtfNewsItemList data from SaveInstanceStance");
        }
        mAtfNewsItemList = new ArrayList<AtfNewsItem>();
        layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        clickedItemIndex = -1;
        progressBar.setVisibility(View.VISIBLE);

        buildAndExecute(AtfNewsNavigator.trending.name());


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


    }

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(ATFNEWSITEMLISTKEY, (Parcelable[]) mAtfNewsItemList.toArray());
        outState.putParcelableArray(FAVATFNEWSITEMLISTKEY, (Parcelable[]) mFavAtfNewsItemList.toArray());
        Log.v(TAG, "Saving the movie item bundle");
    }*/

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

    private void startFavNewsDpIntentActivity() {
        Intent FavNewsintent = new Intent(this, FavNewsDpActivity.class);
        startActivity(FavNewsintent);
    }

    /*private void saveEditor(String url){
        editor.putString(atfNewsItem.getTitle(), "true");
        editor.commit();
    }*/

    /*private void buildAtfNewsItemList(){
        // write code here to call webservices
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BASE_URL+TRENDING_NEWS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++)
                            try {
                                //PARSE JSON ARRAY
                                JSONObject newsJSON = response.getJSONObject(i);
                                JSONArray atfNewsItemJsonArray = newsJSON.getJSONArray(ARTICLES);
                                ArrayList<AtfNewsItem> atfNewsItemList = new ArrayList<>();
                                for (int j = 0; j < atfNewsItemJsonArray.length(); j++) {
                                    JSONObject jsonAtfNewsItem = atfNewsItemJsonArray.getJSONObject(j);
                                    AtfNewsItem atfNewsItem = new AtfNewsItem(jsonAtfNewsItem);
                                    atfNewsItemList.add(atfNewsItem);
                                }

                            }catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error: " + error.toString());
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }*/


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
        } else if (id == R.id.action_trending) {
            buildAndExecute(AtfNewsNavigator.trending.name());
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
                mAtfNewsItemList = JsonUtils.parseNewsApiJson(searchResults, NEWSTYPE, COUNTRY, CATEGORY, getUserEmailId());
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
                //insert datas into fb here

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
        viewModel.getAtfNewsItemsLiveData().observe(this, new Observer<List<AtfNewsItem>>() { //TODO replace the hardcode value with real userid
            @Override
            public void onChanged(@Nullable List<AtfNewsItem> atfNewsItems) {
                Log.d(TAG, "Retrieving LiveData using Rooms in AtfNewsItemViewModel");
                displayToastMessage(atfNewsItems);
                retrieveAllFavoriteNewsData(atfNewsItems);
                //setmTopNewsRecyclerView(atfNewsItems, mFavAtfNewsItemList);
            }
        });
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
                    /*String key = mUserFirebaseDatabaseReference.getKey();
                    mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference().child(key).child(atfFavNewsItem.getTitle());
                    mUserFirebaseDatabaseReference.setValue(atfFavNewsItem);*/
                    //String key = mUserFirebaseDatabaseReference.push().getKey();
                    //mUserFirebaseDatabaseReference.child(key).setValue(atfFavNewsItem);

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
        if (atfNewsItemList != null && !Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "You are offline. Please continue reading atfnews based on your previous online time.", Toast.LENGTH_SHORT).show();
        } else if (atfNewsItemList == null && !Utils.isNetworkAvailable(this)) {
            Toast.makeText(TopNewsActivity.this, "You are offline. You need an active internet connection while using this app for the first time atleast.", Toast.LENGTH_SHORT).show();
        }
    }


    private void buildAndExecute(String name) {
        if (name.equals(AtfNewsNavigator.settings.name())) {
            Intent FavNewsintent = new Intent(this, SettingsActivity.class);
            startActivity(FavNewsintent);
        } else if (name.equals(AtfNewsNavigator.trending.name())) {
            showTrendingNewsView();
        } else if (name.equals(AtfNewsNavigator.favorite.name())) {
            Fragment fragment = new AdMobFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.adView, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            /*Intent intent = new Intent(TopNewsActivity.this, AdMobFragment.class);
            startActivity(intent);*/
            /*Intent FavNewsintent = new Intent(this, FavNewsDpActivity.class);
            startActivity(FavNewsintent);*/
        }
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


       /* UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        viewModel.getUserDAO().findUserWithId(PrefUtils.getCurrentUser(TopNewsActivity.this).id);

        viewModel.getUserDAO().findUserWithId(PrefUtils.getCurrentUser(TopNewsActivity.this).id).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User liveUser) {
                Log.d(TAG, "Retrieving LiveData using Rooms in FavoriteNewsViewModel");
                userId = liveUser.getId();
                addToFavorite(userId, editor, mAtfNewsItemList.get(clickedIndex), mAtfNewsItemList.get(clickedIndex).getUrl());

            }
        });

*/
    }


    public class AsyncDBTaskForFavNewsDeletion extends AsyncTask<AtfNewsItem, Void, FavoriteAtfNewsItem> {
        @Override
        protected FavoriteAtfNewsItem doInBackground(final AtfNewsItem... atfNewsItems) {
            final AtfNewsItem atfNewsItm = atfNewsItems[0];
            if (atfNewsItm != null) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //FavoriteAtfNewsItem mFavAtfNewsItem = mDb.favoriteAtfNewsItemDAO().getFavouriteNewsItemForUser(getUserEmailId(), atfNewsItm.getUrl());
                        //mDb.favoriteAtfNewsItemDAO().deleteFavAtfNewsItem(mFavAtfNewsItem);
                        mDb.favoriteAtfNewsItemDAO().deleteFavAtfNewsItem(new FavoriteAtfNewsItem(getUserEmailId(), atfNewsItm.getUrl()));
                        removeFromFb(atfNewsItm.getUrl());
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
                Toast.makeText(TopNewsActivity.this, "We removed from your favorite section", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TopNewsActivity.this, "Failed to remove favorite due to some interruption. Please try again later.", Toast.LENGTH_SHORT).show();
            }

        }
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

    private void showTrendingNewsView() {
        if (Utils.isNetworkAvailable(this)) {
            URL newsApiUrl = NetworkUtils.buildUrl();
            new AsyncHttpTaskForNewsApiDataDownload().execute(newsApiUrl);
        } else {
            //offline loading
            Toast.makeText(TopNewsActivity.this, "You are offline. Please continue reading atfnews based on previous last online time", Toast.LENGTH_SHORT).show();
            setUpAtfNewsItemsViewModel();
        }

    }


    @Override
    public void onAtfNewsItemClick(int clickedIndex) {
        if (isTablet(this)) {
            Log.d(TAG, "Tablet Mode ON");
            //TODO code here for content description display screen
        }
        Log.d(TAG, "onAtfNewsItemClick: Clicked on Index Item:" + clickedIndex);
    }


    //ImageView favIconInactive, Drawable favIconActive

    @Override
    public void onAddFavAtfNewsItemClickAtAtfNewsActivity(int clickedIndex) {
        Toast.makeText(this, "Clicked at news item number:" + clickedIndex, Toast.LENGTH_SHORT);
        clickedItemIndex = clickedIndex;
        addToFavWithUser(clickedIndex);
       /* Glide.with(this)
                .load(R.drawable.loveenabled)
                .asBitmap()
                .override(160, 120)
                .into(favIconInactive);*/


    }

    @Override
    public void onRemoveFavAtfNewsItemClickAtAtfNewsActivity(AtfNewsItem atfNewsItem, int clickedIndex) {
        removeFavWithUser(clickedIndex);
        //new AsyncDBTaskForFavNewsDeletion().execute(atfNewsItem);
        /*Intent iFavIntent = new Intent(this, FavNewsRmActivity.class);
        startActivity(iFavIntent);*/
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.GONE);
        if (mUserFirebaseDatabaseReference != null && valueEventListener != null) {
            String key = mUserFirebaseDatabaseReference.getKey();
            mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        }
        Glide.get(this).clearMemory();
    }
}

//Settings
//widget
//snackbar