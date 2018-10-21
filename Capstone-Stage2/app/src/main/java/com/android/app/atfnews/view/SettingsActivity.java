package com.android.app.atfnews.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.app.atfnews.model.User;
import com.android.app.atfnews.utils.PrefUtils;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private User user;
    private final Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        /*if (savedInstanceState != null && savedInstanceState.containsKey(USER_OBJECT)) {
            user =  savedInstanceState.getParcelable(USER_OBJECT);
            Log.d(TAG, "Retrieved data from SaveInstanceStance");
            if(user != null && null != user.getFacebookID()) {
                Intent intent = new Intent(LoginActivity.this, FacebookLogoutActivity.class);
                // Using Parcelable
                intent.putExtra(USER_OBJECT, user);
                try {
                    LoginActivity.this.mContext.startActivity(intent);
                    finish();
                } catch (RuntimeException e) {
                    Log.d(TAG, e.getMessage());
                }
            }else if(user != null && null != user.getGoogleId()) {
                    Intent homeIntent = new Intent(LoginActivity.this, GoogleLogoutActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
        }*/
        super.onCreate(savedInstanceState);

        if (PrefUtils.getCurrentUser(SettingsActivity.this) != null && PrefUtils.getCurrentUser(SettingsActivity.this).facebookID != null) {
            Intent homeIntent = new Intent(SettingsActivity.this, FacebookLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        } else if (PrefUtils.getCurrentUser(SettingsActivity.this) != null && PrefUtils.getCurrentUser(SettingsActivity.this).googleId != null) {
            Intent homeIntent = new Intent(SettingsActivity.this, GoogleLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        } else if (PrefUtils.getCurrentUser(SettingsActivity.this) != null && PrefUtils.getCurrentUser(SettingsActivity.this).id != null) {
            Intent homeIntent = new Intent(SettingsActivity.this, EmailLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }




}
