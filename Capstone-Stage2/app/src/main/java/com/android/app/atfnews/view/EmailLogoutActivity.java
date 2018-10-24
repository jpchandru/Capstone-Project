package com.android.app.atfnews.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.utils.PrefUtils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EmailLogoutActivity extends AppCompatActivity {

    private static final String TAG = "EmailLogoutActivity";

    private User user;
    private ImageView profileImage;
    private ProgressDialog progressDialog;
    Bitmap bitmap;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnReset)
    Button resetButton;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_email);
        user= PrefUtils.getCurrentUser(EmailLogoutActivity.this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(null != this.getSupportActionBar()){
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        profileImage= (ImageView) findViewById(R.id.profileImage);
        progressDialog = new ProgressDialog(EmailLogoutActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setUrlNewsType(AtfNewsUrlType.us.name(), EmailLogoutActivity.this);
                Intent i = new Intent(EmailLogoutActivity.this, TopNewsActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(EmailLogoutActivity.this);
                PrefUtils.clearCurrentUserFromFirebase(EmailLogoutActivity.this);
                PrefUtils.clearUrlNewsType(EmailLogoutActivity.this);
                FirebaseAuth.getInstance().signOut();
                // We can logout from facebook by calling following method
                //LoginManager.getInstance().logOut();
                Intent i= new Intent(EmailLogoutActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        progressDialog.dismiss();
    }
}
