package com.android.app.atfnews.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.repository.FavoriteAtfNewsItemDAO;
import com.android.app.atfnews.utils.PrefUtils;

public class FavoriteNewsViewModel extends AndroidViewModel {

    private static final String TAG = FavoriteNewsViewModel.class.getSimpleName();
    private FavoriteAtfNewsItemDAO favoriteAtfNewsItemDAO;

    public FavoriteNewsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving favorite atfnews for specified user from the Database");
        favoriteAtfNewsItemDAO = database.favoriteAtfNewsItemDAO();
    }

    public FavoriteAtfNewsItemDAO getFavoriteAtfNewsItemDAO() {
        return favoriteAtfNewsItemDAO;
    }
}
