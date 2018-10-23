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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.R;
import com.android.app.atfnews.model.User;
import com.facebook.login.LoginManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FacebookLogoutActivity extends AppCompatActivity {

    private static final String TAG = "FacebookLogoutActivity";

    private TextView btnLogout;
    private User user;
    private ImageView profileImage;
    private ProgressDialog progressDialog;
    Bitmap bitmap;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_facebook);
        user= PrefUtils.getCurrentUser(FacebookLogoutActivity.this);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        if(null != this.getSupportActionBar()){
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        profileImage= (ImageView) findViewById(R.id.profileImage);
        progressDialog = new ProgressDialog(FacebookLogoutActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        // fetching facebook's profile picture
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                URL imageURL = null;
                try {//move user-id to string.xml
                    imageURL = new URL("https://graph.facebook.com/" + "10217301569551287" + "/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                 bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                profileImage.setImageBitmap(bitmap);
            }
        }.execute();


        btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(FacebookLogoutActivity.this);
                PrefUtils.clearCurrentUserFromFirebase(FacebookLogoutActivity.this);
                // We can logout from facebook by calling following method
                LoginManager.getInstance().logOut();
                Intent i= new Intent(FacebookLogoutActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        progressDialog.dismiss();
    }
}
