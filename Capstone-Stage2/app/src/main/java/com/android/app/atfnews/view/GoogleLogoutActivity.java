package com.android.app.atfnews.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.utils.PrefUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GoogleLogoutActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleLogoutActivity";
    private GoogleApiClient mGoogleApiClient;
    private User user;
    Bitmap bitmap;
    Context mContext = this;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnReset)
    Button resetButton;
    @BindView(R.id.btnLogout)
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_google);
        ButterKnife.bind(this);
        user = PrefUtils.getCurrentUser(GoogleLogoutActivity.this);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fetching profile picture
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL imageURL = null;
                try {//move user-id to string.xml
                    imageURL = new URL(user.getPhotoUrl());
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
                PrefUtils.setUrlNewsType(AtfNewsUrlType.us.name(), GoogleLogoutActivity.this);
                Toast.makeText(GoogleLogoutActivity.this, "You are watching news of USA", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(GoogleLogoutActivity.this, TopNewsActivity.class);
                startActivity(i);
                finish();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(GoogleLogoutActivity.this);
                PrefUtils.clearUrlNewsType(GoogleLogoutActivity.this);
                PrefUtils.clearCurrentUserFromFirebase(GoogleLogoutActivity.this);
                FirebaseAuth.getInstance().signOut();
                mGoogleApiClient = MyGoogleApiClient_Singleton.getInstance(null).get_GoogleApiClient();
                if (mGoogleApiClient == null) {
                    /*This is necessary if the app has been closed/stooped/uninstalled and then reopened again which would land in authenicated screen.
                      Logging out in this case require the object creation necessary. */
                    configureSignin();
                }
                mGoogleApiClient.connect();
                mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        FirebaseAuth.getInstance().signOut();
                        if (mGoogleApiClient.isConnected()) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if (status.isSuccess()) {
                                        Log.d(TAG, "User Logged out");
                                        Toast.makeText(GoogleLogoutActivity.this, "Thank You for using ATF News. See you soon.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(GoogleLogoutActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Google API Client Connection Suspended");
                    }
                });
            }
        });
    }

    private void configureSignin() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GoogleLogoutActivity.this.getResources().getString(R.string.google_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google API Client Connection Failed");
    }
}
