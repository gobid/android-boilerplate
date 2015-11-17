package com.govindadasu.androidboilerplate;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private TextView mTextDetails;
    private LoginButton loginButton;
    protected static CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private com.facebook.AccessToken accessToken;
    private  Activity mainActivity = this;

    private FacebookCallback<LoginResult>  mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            if (response.getError() != null) {
                                Log.i(App.getTag(), "error");
                            } else {
                                String email = me.optString("email");
                                String id = me.optString("id");
                                String name = me.optString("name");
                                String message = name + ' ' + email + ' ' + id;
                                Intent intent = new Intent(getBaseContext(), Profile_View.class);
                                intent.putExtra(App.profileInfoText, message);
                                startActivity(intent);
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.e(App.getTag(), "cancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e(App.getTag(), "error");
        }
    };
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTextDetails = (TextView)findViewById(R.id.mTextDetails);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.forge.simpleandroid",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                mTextDetails.setText("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e(App.getTag(), "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            mTextDetails.setText("Error");
        }

        Log.i("Tag", "started program");
        SignInButton btn = (SignInButton)findViewById(R.id.sign_in_button);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginButton fb_login_button = (LoginButton)findViewById(R.id.fb_login_button);
        fb_login_button.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));
        LoginManager.getInstance().logOut();
        mCallbackManager = CallbackManager.Factory.create();
        fb_login_button.registerCallback(mCallbackManager, mCallback);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void goToRegistration(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void goToSignIn(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            startActivity(new Intent(this, GoogleLoginActivity.class));
        }

        // ...
    }



}
