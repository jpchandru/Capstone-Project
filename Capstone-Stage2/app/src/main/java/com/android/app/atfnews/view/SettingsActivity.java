package com.android.app.atfnews.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.app.atfnews.utils.PrefUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
