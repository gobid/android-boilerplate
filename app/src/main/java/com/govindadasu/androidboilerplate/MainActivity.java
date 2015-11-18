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
import java.util.concurrent.TimeUnit;

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
        public void onSuccess(final LoginResult loginResult) {
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
                                Log.e(App.getTag(), message);
                                Connection_Task task = new Connection_Task();
                                task.parameters =
                                        "grant_type=convert_token&" +
                                                "client_id=TxjxrOBvlhnjcsG7MUSSBoOa0b92EJkg7LR9JxvU&" +
                                                "client_secret=4UrBWZwNcYVhd1y9XTKr2zu9IlZeb67H5vShIxJ4wh26zCXEIMGrmKVPz9Kfni1Y0NfEdug5GMaZaVVmxHjKB54tBHfKCYGTuCFDmDuuQw7l20lE7TWdjCintnIjNpVZ&" +
                                                "backend=facebook&" +
                                                "token=" + loginResult.getAccessToken().getToken();
                                task.execute("https://forge.fwd.wf/auth/convert-token");
                                try {
                                    task.get(10000, TimeUnit.MILLISECONDS);
                                    Intent intent = new Intent(getBaseContext(), Profile_View.class);
                                    intent.putExtra(App.profileInfoText, task.final_output);
                                    startActivity(intent);
                                }
                                catch (Exception e){
                                    Log.e(App.getTag(), "timeout failed to server");
                                }

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
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        mTextDetails = (TextView)findViewById(R.id.mTextDetails);
        Connection_Task task = new Connection_Task();


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.govindadasu.androidboilerplate",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                mTextDetails.setText("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e(App.getTag(), mTextDetails.getText().toString());
            }
        } catch (Exception e) {
            mTextDetails.setText("Error");
        }

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

    }



}
