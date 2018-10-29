package com.android.app.atfnews.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.AtfNewsUrlType;
import com.android.app.atfnews.utils.PrefUtils;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EmailLogoutActivity extends AppCompatActivity {

    private static final String TAG = "EmailLogoutActivity";

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
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(null != this.getSupportActionBar()){
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.setUrlNewsType(AtfNewsUrlType.us.name(), EmailLogoutActivity.this);
                Log.d(TAG, "Successfully reset to default news");
                Toast.makeText(EmailLogoutActivity.this,"You are watching news of USA", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EmailLogoutActivity.this, "Thank You for using ATF News. See you soon.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully signedoff from Firebase");
                Intent i= new Intent(EmailLogoutActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
