package com.android.app.atfnews.view;


import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.app.atfnews.R;
import com.android.app.atfnews.model.FirebaseAtfNewsUser;
import com.android.app.atfnews.model.User;
import com.android.app.atfnews.model.UserViewModel;
import com.android.app.atfnews.repository.AppDatabase;
import com.android.app.atfnews.utils.AppExecutors;
import com.android.app.atfnews.utils.PrefUtils;
import com.android.app.atfnews.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.annotation.Nullable;

public class GoogleLoginActivity extends LoginActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "GoogleLoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AppDatabase mDb;
    private User user;
    private FirebaseAtfNewsUser fbUser;
    private FirebaseDatabase mUserFirebaseDatabase;
    private DatabaseReference mUserFirebaseDatabaseReference;
    private final Context mContext = this;
    private static final String USER_OBJECT = "USER_OBJECT";
    private static final int RC_SIGN_IN = 9001;
   // private ProgressDialog progressDialog;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (savedInstanceState != null && savedInstanceState.containsKey(USER_OBJECT)) {
            user =  savedInstanceState.getParcelable(USER_OBJECT);
            Log.d(TAG, "Retrieved data from SaveInstanceStance");
            Intent intent = new Intent(GoogleLoginActivity.this, FacebookLogoutActivity.class);
            // Using Parcelable
            intent.putExtra(USER_OBJECT, user);
            try {
                GoogleLoginActivity.this.mContext.startActivity(intent);
                finish();
            } catch (RuntimeException e) {
                Log.d(TAG, e.getMessage());
            }*/
        if (PrefUtils.getCurrentUser(GoogleLoginActivity.this) != null) {
            Intent homeIntent = new Intent(GoogleLoginActivity.this, GoogleLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        } else {
            /*progressDialog = new ProgressDialog(GoogleLoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();*/
            getIntentFromLogin();
            mDb = AppDatabase.getsInstance(getApplicationContext());
            // Configure sign-in to request the user's basic profile like name and email
            mAuth = FirebaseAuth.getInstance();
            //this is where we start the Auth state Listener to listen for whether the user is signed in or not
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    // Get signedIn user
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    //if user is signed in, we call a helper method to save the user details to Firebase
                    if (user != null) {
                        // User is signed in

                    /*Intent intent = getIntent();
                    if (intent != null && intent.hasExtra("user")) {
                        user = intent.getParcelableExtra("user");
                    }*/
                        //createUserInFirebaseHelper();
                        Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.e(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };

            mUserFirebaseDatabase = FirebaseDatabase.getInstance();
            mUserFirebaseDatabaseReference = mUserFirebaseDatabase.getReference("users");
            configureSignin();
            signIn();
        }
    }

    private void configureSignin() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GoogleLoginActivity.this.getResources().getString(R.string.google_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
        MyGoogleApiClient_Singleton.getInstance(mGoogleApiClient);

    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.second) {
            if (Utils.isNetworkAvailable(mContext)) {
                signIn();
            } else {
                Toast.makeText(GoogleLoginActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("userObject", user);
        Log.v(TAG, "Saving the user");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                String idToken = account.getIdToken();
                user = new User(idToken,
                        account.getDisplayName(),
                        account.getEmail(),
                        null,
                        idToken,
                        null,
                        account.getPhotoUrl().toString()
                );
                PrefUtils.setCurrentUser(user, GoogleLoginActivity.this);
                insertOrUpdateLocalDbAtfNewsUser();
                insertOrUpdateFirebaseAtfNewsUser();
                /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        mDb.userDAO().insertUser(user);
                    }
                });*/
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

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


        /*viewModel.getUserDAO().findUserWithEmail(user.email).observe(this, new Observer<User>() {
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

    private void getIntentFromLogin() {
        Intent i = getIntent();
        countryCode = i.getStringExtra("countryCode");
        clickedCountryCode = i.getStringExtra("clickedCountryCode");
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
        String key = mUserFirebaseDatabaseReference.getKey();
        if(valueEventListener != null) mUserFirebaseDatabaseReference.child(key).removeEventListener(valueEventListener);
        Glide.get(this).clearMemory();
    }


    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(GoogleLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // createUserInFirebaseHelper();
                            Toast.makeText(GoogleLoginActivity.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(GoogleLoginActivity.this, "welcome " + user.name, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(GoogleLoginActivity.this, TopNewsActivity.class);
                            //getIntentFromWidget();
                            intent.putExtra("countryCode", countryCode);
                            intent.putExtra("clickedCountryCode", clickedCountryCode);
                            intent.putExtra(USER_OBJECT, user);
                            startActivity(intent);
                            finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e(TAG, "current user retrieved ................");
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(progressBar != null)progressBar.setVisibility(View.GONE);
    }
}
