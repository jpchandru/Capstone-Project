package com.android.app.atfnews.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.repository.AtfNewsItemDAO;
import com.android.app.atfnews.repository.UserDAO;

import java.util.List;

public class AtfNewsItemViewModel extends AndroidViewModel {

    private static final String TAG = AtfNewsItemViewModel.class.getSimpleName();
    private AtfNewsItemDAO atfNewsItemDAO;

    public AtfNewsItemViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving news from the Database");
        atfNewsItemDAO = database.atfNewsItemDAO();
    }

    public AtfNewsItemDAO getAtfNewsItemsDAO(){   return atfNewsItemDAO; }
}
