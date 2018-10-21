package com.android.app.atfnews.view;


import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
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
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private String emailFromLocalDb = null;

    private AppDatabase mDb;
    @BindView(R.id.login_with_email)
    ImageView emailImg;
    String emailLoginTxt;
    String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mDb = AppDatabase.getsInstance(getApplicationContext());
        // mAuth = FirebaseAuth.getInstance();
        mUserFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("users");
        progressDialog = new ProgressDialog(EmailLoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        /*try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLoginTxt.getText().toString(), "atfnewsuser");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while signing in");
        }*/
        executeUserDBOperationViaLocal();
       /* emailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(EmailLoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                try {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLoginTxt.getText().toString(), "atfnewsuser");
                } catch (Exception e) {
                    Log.e(TAG, "Error occurred while signing in");
                }
                executeUserDBOperationViaLocal();
                *//*if (Utils.isNetworkAvailable(EmailLoginActivity.this) && mUserFirebaseDatabaseReference != null)
                    findAndExecuteUserExistence(null);
                    //executeUserDBOperationViaFirebase();
                else
                    executeUserDBOperationViaLocal();*//*
            }
        });*/

    }

    private void executeUserDBOperationViaLocal() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("emailLoginText")) {
            emailLoginTxt = intent.getStringExtra("emailLoginText");
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
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailLoginTxt, "atfnewsuser");
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while signing in");
        }

        insertOrUpdateLocalDbAtfNewsUser();
        insertOrUpdateFirebaseAtfNewsUser();

        Toast.makeText(EmailLoginActivity.this, "welcome " + localUser.name, Toast.LENGTH_LONG).show();
        Intent i = new Intent(EmailLoginActivity.this, TopNewsActivity.class);
        startActivity(i);
        finish();
        progressDialog.dismiss();
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

        /*UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getUserDAO().findUserWithEmail(localUser.email).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User liveUser) {
                Log.d(TAG, "Retrieving LiveData using Rooms in UserViewModel");
                if (liveUser != null)
                    emailFromLocalDb = liveUser.getEmail();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (emailFromLocalDb != null)
                            mDb.userDAO().updateUser(localUser.id, localUser.name, localUser.email, localUser.facebookID, localUser.googleId, localUser.photoUrl);
                        else
                            mDb.userDAO().insertUser(localUser);
                    }


                });

            }
        });*/
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
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
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
        progressDialog.dismiss();
        String key = mUserFirebaseDatabaseReference.getKey();
        mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        Glide.get(this).clearMemory();
    }

    @NonNull
    private String generateRandomId() {
        Random rnd = new Random();
        int randomId = 100000 + rnd.nextInt(900000);
        return String.valueOf(randomId);
    }

}
