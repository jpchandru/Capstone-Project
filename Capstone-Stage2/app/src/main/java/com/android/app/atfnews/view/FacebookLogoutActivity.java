package com.android.app.atfnews.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.utils.PrefUtils;
import com.facebook.login.LoginManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FacebookLogoutActivity extends AppCompatActivity {

    private static final String TAG = "FacebookLogoutActivity";
    private ImageView profileImage;
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
        setContentView(R.layout.activity_logout_facebook);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        if (null != this.getSupportActionBar()) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        profileImage = (ImageView) findViewById(R.id.profileImage);
        // fetching facebook's profile picture
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL imageURL = null;
                try {//move user-id to string.xml
                    imageURL = new URL("https://graph.facebook.com/" + "10217301569551287" + "/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setUrlNewsType(AtfNewsUrlType.us.name(), FacebookLogoutActivity.this);
                Log.d(TAG, "Successfully reset to default news");
                Toast.makeText(FacebookLogoutActivity.this, "You are watching news of USA", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(FacebookLogoutActivity.this, TopNewsActivity.class);
                startActivity(i);
                finish();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(FacebookLogoutActivity.this);
                PrefUtils.clearCurrentUserFromFirebase(FacebookLogoutActivity.this);
                PrefUtils.clearUrlNewsType(FacebookLogoutActivity.this);
                // We can logout from facebook by calling following method
                LoginManager.getInstance().logOut();
                Toast.makeText(FacebookLogoutActivity.this, "Thank You for using ATF News. See you soon.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully signedoff from Firebase");
                Intent i = new Intent(FacebookLogoutActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
