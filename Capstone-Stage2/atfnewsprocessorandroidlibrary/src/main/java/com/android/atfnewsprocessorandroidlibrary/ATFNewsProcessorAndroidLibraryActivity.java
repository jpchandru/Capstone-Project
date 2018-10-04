package com.android.atfnewsprocessorandroidlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


/**
 * Created by cj on 9/19/18.
 */

public class ATFNewsProcessorAndroidLibraryActivity extends AppCompatActivity {

    private static final String RANDOM_JOKES = "RANDOM_JOKES";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bib_activity_android_library);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        mTextView = findViewById(R.id.random_joke);
        String x = i.getStringExtra(RANDOM_JOKES);
        mTextView.setText(x);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
