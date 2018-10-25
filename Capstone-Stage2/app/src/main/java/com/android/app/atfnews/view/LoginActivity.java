package com.android.app.atfnews.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String USER_OBJECT = "USER_OBJECT";
    private User user;
    private final Context mContext = this;
    @BindView(R.id.first)
    Button facebookImg;
    @BindView(R.id.second)
    Button googleImg;
    @BindView(R.id.login_with_email)
    Button emailImg;
    @BindView(R.id.email_login_text)
    EditText emailLoginTxt;
    @BindView(R.id.spinner)
    ProgressBar progressBar;
    String countryCode, clickedCountryCode = null;


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


        if (PrefUtils.getCurrentUser(LoginActivity.this) != null) {
            getIntentFromWidgetForLogin();
            Intent homeIntent = new Intent(LoginActivity.this, TopNewsActivity.class);
            homeIntent.putExtra("countryCode", countryCode);
            homeIntent.putExtra("clickedCountryCode", clickedCountryCode);
            startActivity(homeIntent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Utils.isNetworkAvailable(mContext)) {
            facebookImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent homeIntent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                    getIntentFromWidgetForLogin();
                    homeIntent.putExtra("countryCode", countryCode);
                    homeIntent.putExtra("clickedCountryCode", clickedCountryCode);
                    progressBar.setVisibility(View.VISIBLE);
                    startActivity(homeIntent);
                    finish();
                }
            });

            googleImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
                    getIntentFromWidgetForLogin();
                    homeIntent.putExtra("countryCode", countryCode);
                    homeIntent.putExtra("clickedCountryCode", clickedCountryCode);
                    progressBar.setVisibility(View.VISIBLE);
                    startActivity(homeIntent);
                    finish();
                }
            });

            emailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEmailValid(emailLoginTxt.getText().toString())) {
                        emailLoginTxt.setError("Invalid Email Address");
                    } else {
                        Intent homeIntent = new Intent(LoginActivity.this, EmailLoginActivity.class);
                        getIntentFromWidgetForLogin();
                        homeIntent.putExtra("countryCode", countryCode);
                        homeIntent.putExtra("clickedCountryCode", clickedCountryCode);
                        homeIntent.putExtra("emailLoginText", emailLoginTxt.getText().toString());
                        progressBar.setVisibility(View.VISIBLE);
                        startActivity(homeIntent);
                        finish();
                    }

                }
            });

        } else {
            facebookImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginActivity.this, "Oops! no internet connection! Please try Email SignIn option only.", Toast.LENGTH_SHORT).show();
                }
            });
            googleImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginActivity.this, "Oops! no internet connection! Please try Email SignIn option only.", Toast.LENGTH_SHORT).show();
                }
            });

            emailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(LoginActivity.this, EmailLoginActivity.class);
                    getIntentFromWidgetForLogin();
                    homeIntent.putExtra("countryCode", countryCode);
                    homeIntent.putExtra("clickedCountryCode", clickedCountryCode);
                    homeIntent.putExtra("emailLoginText", emailLoginTxt.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    startActivity(homeIntent);
                    finish();
                }
            });

        }

        progressBar.setVisibility(View.GONE);


        /*final ImageView background = ButterKnife.findById(this, R.id.scrolling_background);
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
                });*/

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void getIntentFromWidgetForLogin() {
        Intent i = getIntent();
        countryCode = i.getStringExtra("country_code");
        clickedCountryCode = i.getStringExtra("clicked_country_code");
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /*private int[] screenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }*/


}
