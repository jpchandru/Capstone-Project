package com.android.app.atfnews;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FacebookLoginActivity extends LoginActivity {

    private static final String TAG = "FacebookLoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ProgressDialog progressDialog;
    String id, name, fnme, lnme, emal, social;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PrefUtils.getCurrentUser(FacebookLoginActivity.this) != null) {
            Intent homeIntent = new Intent(FacebookLoginActivity.this, FacebookLogoutActivity.class);
            startActivity(homeIntent);
            finish();
        }else{
            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays
                    // .asList("email,manage_pages,public_profile,publish_actions,publish_pages,user_friends"));
                    .asList("email,manage_pages,public_profile,publish_pages,user_friends"));

            progressDialog = new ProgressDialog(FacebookLoginActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            loginButton.performClick();
            loginButton.setPressed(true);
            loginButton.invalidate();
            loginButton.registerCallback(callbackManager, mCallBack);
            loginButton.setPressed(false);
            loginButton.invalidate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressDialog.dismiss();
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
                                user = new User();
                                id = object.getString("id");
                                name = object.getString("name");
                                fnme = object
                                        .getString("first_name");
                                lnme = object
                                        .getString("last_name");
                                emal = object.getString("email");
                                social = "2";

                                user.facebookID = object.getString("id").toString();
                                user.email = object.getString("email").toString();
                                user.name = object.getString("name").toString();

                                PrefUtils.setCurrentUser(user, FacebookLoginActivity.this);

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
                            Intent intent = new Intent(FacebookLoginActivity.this, FacebookLogoutActivity.class);
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

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progressDialog.dismiss();
        }
    };

}
