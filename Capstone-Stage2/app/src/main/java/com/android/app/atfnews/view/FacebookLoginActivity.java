package com.android.app.atfnews.view;


import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Arrays;

import javax.annotation.Nullable;

import butterknife.BindView;

public class FacebookLoginActivity extends LoginActivity {

    private static final String TAG = "FacebookLoginActivity";
    private CallbackManager callbackManager;
    private AppDatabase mDb;
    //private ProgressDialog progressDialog;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private User user;
    FirebaseAtfNewsUser fbUser;
    private static final String USER_OBJECT = "USER_OBJECT";
    String id, name, fnme, lnme, emal, social;
    private ValueEventListener valueEventListener;
    String email = null;
    String emailFromLocalDb = null;
    @BindView(R.id.login_button)
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PrefUtils.getCurrentUser(FacebookLoginActivity.this) != null) {
            Intent homeIntent = new Intent(FacebookLoginActivity.this, FacebookLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        } else {
            getIntentFromLogin();
            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays
                    // .asList("email,manage_pages,public_profile,publish_actions,publish_pages,user_friends"));
                    .asList("email,manage_pages,public_profile,publish_pages,user_friends"));

            /*progressDialog = new ProgressDialog(FacebookLoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();*/
            loginButton.performClick();
            loginButton.setPressed(true);
            loginButton.invalidate();
            loginButton.registerCallback(callbackManager, mCallBack);
            mDb = AppDatabase.getsInstance(getApplicationContext());
            mUserFirebaseDatabase = FirebaseDatabase.getInstance();
            mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("users");
            loginButton.setPressed(false);
            loginButton.invalidate();
        }

    }

    private void getIntentFromLogin() {
        Intent i = getIntent();
        countryCode = i.getStringExtra("countryCode");
        clickedCountryCode = i.getStringExtra("clickedCountryCode");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressBar.setVisibility(View.GONE);
            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            try {
                                id = object.getString("id");
                                email = object.getString("email").toString();
                                user = new User(id,
                                        object.getString("name").toString(),
                                        email,
                                        id,
                                        null,
                                        null,
                                        "https://graph.facebook.com/" + id + "/picture?type=large"
                                );
                                PrefUtils.setCurrentUser(user, FacebookLoginActivity.this);
                                insertOrUpdateLocalDbAtfNewsUser();
                                insertOrUpdateFirebaseAtfNewsUser();

                                    /*new GraphRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/" + id + "/friends",
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {
                                                    Log.e("response", "" + response);
                                                }
                                            }
                                    ).executeAsync();
*/


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(FacebookLoginActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(FacebookLoginActivity.this, TopNewsActivity.class);
                            //getIntentFromWidget();
                            intent.putExtra("countryCode", countryCode);
                            intent.putExtra("clickedCountryCode", clickedCountryCode);
                            intent.putExtra(USER_OBJECT, user);
                            startActivity(intent);
                            finish();

                        }

                    });

            Bundle parameters = new Bundle();
            //parameters.putString("fields",  "id,name,email,first_name,last_name,friends,accounts{access_token,id,name,likes}");
            parameters.putString("fields", "id,name,email,first_name,last_name");
            request.setParameters(parameters);
            request.executeAsync();
        }





        private void insertOrUpdateFirebaseAtfNewsUser() {
            valueEventListener = (new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String key = dataSnapshot1.getKey();
                        fbUser = dataSnapshot1.getValue(FirebaseAtfNewsUser.class); // This is a member variable
                        if (fbUser != null && fbUser.email.equalsIgnoreCase(user.email)) {
                            //mUserFirebaseDatabaseReference.orderByChild("email").equalTo(fbUser.getEmail()).addValueEventListener(listener);
                            createFbUserObjectValues(key);
                        } else {
                            fbUser = null;
                        }
                    }
                    if (fbUser == null) {
                        String key = mUserFirebaseDatabaseReference.push().getKey();
                        createFbUserObjectValues(key);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mUserFirebaseDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
        }

        @Override
        public void onCancel() {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onError(FacebookException e) {
            progressBar.setVisibility(View.GONE);
        }
    };


    private void insertOrUpdateLocalDbAtfNewsUser() {
        UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        final User userFromDb = viewModel.getUserDAO().findUserWithEmail(user.email);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(userFromDb != null)
                    mDb.userDAO().updateUser(user.id, user.name, user.email, user.facebookID, user.googleId, user.photoUrl);
                else
                    mDb.userDAO().insertUser(user);
            }
        });

        /*UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.getUserDAO().findUserWithEmail(user.email).observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User liveUser) {
                Log.d(TAG, "Retrieving LiveData using Rooms in UserViewModel");
                if (liveUser != null)
                    emailFromLocalDb = liveUser.getEmail();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (emailFromLocalDb != null)
                            mDb.userDAO().updateUser(user.id, user.name, user.email, user.facebookID, user.googleId, user.photoUrl);
                        else
                            mDb.userDAO().insertUser(user);
                    }


                });

            }
        });*/
    }

    private void createFbUserObjectValues(String key) {
        fbUser = new FirebaseAtfNewsUser();
        fbUser.id = user.id;
        fbUser.name = user.name;
        fbUser.email = user.email;
        fbUser.facebookID = user.facebookID;
        fbUser.googleId = user.googleId;
        fbUser.photoUrl = user.photoUrl;

        mUserFirebaseDatabaseReference.child(key).setValue(fbUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressBar != null) progressBar.setVisibility(View.GONE);
        if (mUserFirebaseDatabaseReference != null && valueEventListener != null) {
            String key = mUserFirebaseDatabaseReference.getKey();
            mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        }
        Glide.get(this).clearMemory();
    }

}
