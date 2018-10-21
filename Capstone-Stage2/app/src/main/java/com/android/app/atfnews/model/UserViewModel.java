package com.android.app.atfnews.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;


import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.repository.UserDAO;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserDAO userDAO;
    private static final String TAG = UserViewModel.class.getSimpleName();

    public UserViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving user from the Database");
        userDAO = database.userDAO();
    }

    public UserDAO getUserDAO(){   return userDAO; }
}
