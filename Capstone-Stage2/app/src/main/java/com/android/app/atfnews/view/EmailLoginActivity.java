package com.android.app.atfnews.view;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.FirebaseAtfNewsUser;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.model.UserViewModel;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailLoginActivity extends LoginActivity {

    private static final String TAG = "EmailLoginActivity";
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    FirebaseAtfNewsUser fbUser;
    User localUser;
    private ValueEventListener valueEventListener;
    private AppDatabase mDb;
    @BindView(R.id.login_with_email)
    Button emailImg;
    String emailLoginTxt;
    private static final String EMAILLOGINTEXT = "emailLoginText";
    private static final String COUNTRYCODE = "countryCode";
    private static final String CLICKEDCOUNTRYCODE = "clickedCountryCode";
    private static final String EMAILUSERPASSWORD = "atfnewsuser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getIntentFromLogin();
        mDb = AppDatabase.getsInstance(getApplicationContext());
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("users");
        executeUserDBOperationViaLocal();
    }

    private void executeUserDBOperationViaLocal() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EMAILLOGINTEXT)) {
            emailLoginTxt = intent.getStringExtra(EMAILLOGINTEXT);
        }
        localUser = new User(generateRandomId(),
                "atfuser",
                emailLoginTxt,
                null,
                null,
                null,
                null
        );
        PrefUtils.setCurrentUser(localUser, EmailLoginActivity.this);
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLoginTxt, EMAILUSERPASSWORD);
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while signing in");
        }
        insertOrUpdateLocalDbAtfNewsUser();
        insertOrUpdateFirebaseAtfNewsUser();
        progressBar.setVisibility(View.GONE);
        Toast.makeText(EmailLoginActivity.this, "welcome " + localUser.name, Toast.LENGTH_LONG).show();
        Intent i = new Intent(EmailLoginActivity.this, TopNewsActivity.class);
        i.putExtra(COUNTRYCODE, countryCode);
        i.putExtra(CLICKEDCOUNTRYCODE, clickedCountryCode);
        startActivity(i);
        finish();
    }


    private void insertOrUpdateLocalDbAtfNewsUser() {
        UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        final User userFromDb = viewModel.getUserDAO().findUserWithEmail(localUser.email);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (userFromDb != null)
                    mDb.userDAO().updateUser(localUser.id, localUser.name, localUser.email, localUser.facebookID, localUser.googleId, localUser.photoUrl);
                else
                    mDb.userDAO().insertUser(localUser);
            }
        });
    }

    private void insertOrUpdateFirebaseAtfNewsUser() {
        valueEventListener = (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    fbUser = dataSnapshot1.getValue(FirebaseAtfNewsUser.class); // This is a member variable
                    if (fbUser != null && fbUser.email.equalsIgnoreCase(localUser.email)) {
                        createFbUserObjectValues(key);
                    } else {
                        fbUser = null;
                    }
                }
                if (fbUser == null) {
                    String key = mUserFirebaseDatabaseReference.push().getKey();
                    createFbUserObjectValues(key);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getIntentFromLogin() {
        Intent i = getIntent();
        countryCode = i.getStringExtra(COUNTRYCODE);
        clickedCountryCode = i.getStringExtra(CLICKEDCOUNTRYCODE);
    }


    private void createFbUserObjectValues(String key) {
        fbUser = new FirebaseAtfNewsUser();
        fbUser.id = localUser.id;
        fbUser.name = localUser.name;
        fbUser.email = localUser.email;
        fbUser.facebookID = localUser.facebookID;
        fbUser.googleId = localUser.googleId;
        fbUser.photoUrl = localUser.photoUrl;
        mUserFirebaseDatabaseReference.child(key).setValue(fbUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (mUserFirebaseDatabaseReference != null && valueEventListener != null) {
            String key = mUserFirebaseDatabaseReference.getKey();
            mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        }
        Glide.get(this).clearMemory();
    }

    @NonNull
    private String generateRandomId() {
        Random rnd = new Random();
        int randomId = 100000 + rnd.nextInt(900000);
        return String.valueOf(randomId);
    }

}
