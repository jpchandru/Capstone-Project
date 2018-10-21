package com.android.app.atfnews.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.utils.PrefUtils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class EmailLogoutActivity extends Activity {

    private static final String TAG = "EmailLogoutActivity";

    private TextView btnLogout;
    private User user;
    private ImageView profileImage;
    private ProgressDialog progressDialog;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_email);
        user= PrefUtils.getCurrentUser(EmailLogoutActivity.this);
        profileImage= (ImageView) findViewById(R.id.profileImage);
        progressDialog = new ProgressDialog(EmailLogoutActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();




        btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(EmailLogoutActivity.this);
                PrefUtils.clearCurrentUserFromFirebase(EmailLogoutActivity.this);
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
