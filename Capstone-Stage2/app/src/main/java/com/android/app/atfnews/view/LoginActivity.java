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
import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

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
    private static final String COUNTRYCODE = "countryCode";
    private static final String CLICKEDCOUNTRYCODE = "clickedCountryCode";
    private static final String EMAILLOGINTEXT = "emailLoginText";
    private static final String COUNTRYCODEFROMWIDGET = "country_code";
    private static final String CLICKEDCOUNTRYCODEFROMWIDGET = "clicked_country_code";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (PrefUtils.getCurrentUser(LoginActivity.this) != null) {
            Intent homeIntent = new Intent(LoginActivity.this, TopNewsActivity.class);
            createIntent(homeIntent);
        }

        if (Utils.isNetworkAvailable(mContext)) {
            facebookImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                    createIntent(homeIntent);
                }
            });

            googleImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
                    createIntent(homeIntent);
                }
            });

            emailImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEmailValid(emailLoginTxt.getText().toString())) {
                        emailLoginTxt.setError("Invalid Email Address");
                    } else {
                        Intent homeIntent = new Intent(LoginActivity.this, EmailLoginActivity.class);
                        homeIntent.putExtra(EMAILLOGINTEXT, emailLoginTxt.getText().toString());
                        createIntent(homeIntent);
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
                    homeIntent.putExtra(EMAILLOGINTEXT, emailLoginTxt.getText().toString());
                    createIntent(homeIntent);
                }
            });
        }
        progressBar.setVisibility(View.GONE);
    }

    private void createIntent(Intent homeIntent) {
        getIntentFromWidgetForLogin();
        homeIntent.putExtra(COUNTRYCODE, countryCode);
        homeIntent.putExtra(CLICKEDCOUNTRYCODE, clickedCountryCode);
        progressBar.setVisibility(View.VISIBLE);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void getIntentFromWidgetForLogin() {
        Intent i = getIntent();
        countryCode = i.getStringExtra(COUNTRYCODEFROMWIDGET);
        clickedCountryCode = i.getStringExtra(CLICKEDCOUNTRYCODEFROMWIDGET);
    }


    private static boolean isEmailValid(String email) {
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
}
