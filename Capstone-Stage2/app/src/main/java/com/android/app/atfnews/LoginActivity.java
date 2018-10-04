package com.android.app.atfnews;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.first)
    ImageView facebookImg;
    @BindView(R.id.second)
    ImageView googleImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PrefUtils.getCurrentUser(LoginActivity.this) != null && PrefUtils.getCurrentUser(LoginActivity.this).facebookID != null) {
            Intent homeIntent = new Intent(LoginActivity.this, FacebookLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        }else if (PrefUtils.getCurrentUser(LoginActivity.this) != null && PrefUtils.getCurrentUser(LoginActivity.this).googleId != null) {
            Intent homeIntent = new Intent(LoginActivity.this, GoogleLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        }


        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        final ImageView background = ButterKnife.findById(this, R.id.scrolling_background);
        int[] screenSize = screenSize();
        //load a very big image and resize it, so it fits our needs
        Glide.with(this)
                .load(R.drawable.busy)
                .asBitmap()
                .override(screenSize[0] * 2, screenSize[1])
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        background.setImageBitmap(resource);

                    }
                });

        facebookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });

        googleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }

    private int[] screenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }


}
