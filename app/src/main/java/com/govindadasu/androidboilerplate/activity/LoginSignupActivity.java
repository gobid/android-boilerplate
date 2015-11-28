package com.govindadasu.androidboilerplate.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.bo.ServerAccessToken;
import com.govindadasu.androidboilerplate.constant.Constants;
import com.govindadasu.androidboilerplate.task.EmailSignInTask;
import com.govindadasu.androidboilerplate.task.EmailSignUpTask;
import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.util.Prefs;
import com.govindadasu.androidboilerplate.util.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginSignupActivity extends PlusBaseActivity implements
        LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 3;
    public static final String TAG_LOGOUT = "logout";


    private ProgressDialog progressDialog;

    // login with email
    private AutoCompleteTextView edtEmail;
    private EditText edtPassword;
    private boolean isLoggedOut;
    private Context mContext;
    private String email;
    private String password;


    @Override
    protected void onPlusClientRevokeAccess() {

    }

    @Override
    protected void onPlusClientSignIn() {
        getProfileInformation();
    }

    @Override
    protected void onPlusClientSignOut() {
        startLoginFlow();
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        if (show)
            showProgressDialog(R.string.msg_sigining_in);
        else
            hideProgressDialog();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (isUserLoggedOut()) {
            isLoggedOut = false;
            signOut();
            startLoginFlow();
            return;
        }
        super.onConnected(connectionHint);
    }

    @Override
    protected void updateConnectButtonState() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        mContext = LoginSignupActivity.this;
        setContentView(R.layout.activity_login_signup);
        // email
        initializeEmailLogin();
        // google
        initializeGoogleLogin();
        // facebook
        initializeFacebookLogin();

        isLoggedOut = getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(TAG_LOGOUT, false);
        clearLogoutFlag();

        if (isUserLoggedOut()) {
            onUserLoggedOut();
        } else {
            checkIfUserAlreadyLoggedIn();
        }

    }

    private boolean isUserLoggedOut() {
        return isLoggedOut;// getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(TAG_LOGOUT, false);
    }

    private void clearLogoutFlag() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(TAG_LOGOUT))
            getIntent().getExtras().remove(TAG_LOGOUT);
    }

    private void initializeGoogleLogin() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_login_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoggedOut = false;
                signIn();
            }
        });
    }

    private void checkIfUserAlreadyLoggedIn() {
        String userEmail = Prefs.getString("user_email", "");
        // facebook
        if (AccessToken.getCurrentAccessToken() != null) {
            showProgressDialog(R.string.msg_loading);

            loadFBUserData();
        } else if (!TextUtils.isEmpty(userEmail)) {// check email login here
            ServerAccessToken serverAccessToken = new Gson().fromJson(Prefs.getString(mContext.getString(R.string.key_user_access_token), ""), ServerAccessToken.class);
            if (serverAccessToken != null && serverAccessToken.getAccess_token() != null) {
                User user = new User();
                user.setEmail(email);
                user.setAccessToken(serverAccessToken.getAccess_token());
                user.setLoginType(User.LOGIN_TYPE_EMAIL);
                User.setLoggedInUser(user);
                goToLandingPage();
            } else {
                Prefs.putString("user_email", "");
            }

        } else {
            // check google login, not any direct way
            initiatePlusClientConnect();
        }

    }

    // not logged in already
    private void startLoginFlow() {
        // not logged in
        hideProgressDialog();
        findViewById(R.id.mainView).setVisibility(View.VISIBLE);

    }
    // Generic

    private void onUserLoggedInWithSocialMedia() {
        Prefs.putString("user_email", "");
        if (!TextUtils.isEmpty(Prefs.getString(mContext.getString(R.string.key_user_access_token), ""))) { // Already Logged in
            goToLandingPage();
            return;
        }

        showProgressDialog(R.string.msg_sigining_in);

        // on server login
        Ion.with(this)
                .load(Constants.SERVER_URL + Constants.NAMESPACE_TOKEN_EXCHANGE)
                .setBodyParameter(Constants.KEY_CLIENT_ID, Constants.CLIENT_ID)
                .setBodyParameter(Constants.KEY_CLIENT_SECRITE, Constants.CLIENT_SECRIT)
                .setBodyParameter("backend", User.getLoggedInUser().isFacebookUser() ? "facebook" : "google-oauth2")
                .setBodyParameter("token", User.getLoggedInUser().getAccessToken())
                .setBodyParameter("grant_type", "convert_token").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject token) {
                hideProgressDialog();
                if (e != null) {
                    e.printStackTrace();
                    showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login_with_server);
                    return;
                }
                if (token == null || !token.toString().contains("access_token")) {
                    showAlertDialog(R.string.title_alert, R.string.msg_invalid_token_from_server);
                    return;
                }
                Prefs.putString(mContext.getString(R.string.key_user_access_token), token.toString());
                goToLandingPage();
            }
        });

    }

    private void goToLandingPage() {
        startActivity(new Intent(mContext, LandingActivity.class));
        finish();
    }


    private void onUserLoggedOut() {
        // if (User.getLoggedInUser().getLoginType() == User.LOGIN_TYPE_FACEBOOK) {
        LoginManager.getInstance().logOut();
        //}
        //else if (User.getLoggedInUser().getLoginType() == User.LOGIN_TYPE_GOOGLE) {
        initiatePlusClientConnect();
        //}
        User.setLoggedInUser(null);
        Prefs.putString("user_email", "");
        Prefs.putString(mContext.getString(R.string.key_user_access_token), "");
        startLoginFlow();
    }


    // Email Login
    private void initializeEmailLogin() {

        edtEmail = (AutoCompleteTextView) findViewById(R.id.email);

        populateAutoComplete();

        edtPassword = (EditText) findViewById(R.id.password);
        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLoginOrSignup(true);
                    return true;
                }
                return false;
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.email_login_button);
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLoginOrSignup(true);
            }
        });

        Button signup = (Button) findViewById(R.id.btnSignUp);
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLoginOrSignup(false);
            }
        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(edtEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private boolean validateInputFields() {
        // Reset errors.
        edtEmail.setError(null);
        edtPassword.setError(null);

        // Store values at the time of the login attempt.
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            edtPassword.setError(getString(R.string.error_invalid_password));
            focusView = edtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.error_field_required));
            focusView = edtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            focusView = edtEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }


    public void resetPassword(View view) {
        // Reset errors.
        edtEmail.setError(null);
        edtPassword.setError(null);
        email = edtEmail.getText().toString();


        boolean cancel = false;
        View focusView = null;
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getString(R.string.error_field_required));
            focusView = edtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            edtEmail.setError(getString(R.string.error_invalid_email));
            focusView = edtEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgressDialog(R.string.msg_password_resetting);


            Ion.with(this)
                    .load(Constants.SERVER_URL + Constants.NAMESPACE_PASSWORD_RESET)
                    .setBodyParameter(Constants.KEY_CLIENT_ID, Constants.CLIENT_ID)
                    .setBodyParameter(Constants.KEY_CLIENT_SECRITE, Constants.CLIENT_SECRIT)
                    .setBodyParameter("email", email)
                    .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                @Override
                public void onCompleted(Exception e, Response<String> result) {
                    hideProgressDialog();

                    if (e != null) {
                        e.printStackTrace();
                        showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login_with_server);
                        return;
                    }
                    if (result.getHeaders().code() != 200) {
                        showAlertDialog(R.string.title_alert, R.string.msg_invalid_token_from_server);
                        return;
                    }
                    resetForm();
                    showAlertDialog(R.string.title_alert, R.string.msg_password_reset);

                }
            });
//                @Override
//                public void onCompleted(Exception e, String token) {
//
//
//                    hideProgressDialog();
//                    if (e != null) {
//                        e.printStackTrace();
//                        showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login_with_server);
//                        return;
//                    }
//                    if (token == null ) {
//                        showAlertDialog(R.string.title_alert, R.string.msg_invalid_token_from_server);
//                        return;
//                    }else if(!token.toString().contains("access_token")){
//                        showAlertDialog(R.string.title_alert, R.string.msg_invalid_username);
//                        resetForm();
//                        return;
//                    }
//                    resetForm();
//                    showAlertDialog(R.string.title_alert, R.string.msg_password_reset);
//                }
            //    });
        }

    }

    private void attemptLoginOrSignup(boolean isLoginRequest) {


        if (validateInputFields()) {

            showProgressDialog(isLoginRequest ? R.string.msg_sigining_in : R.string.msg_sigining_up);

            if (isLoginRequest) {
                Ion.with(this)
                        .load(Constants.SERVER_URL + Constants.NAMESPACE_EMAIL_SIGN_IN)
                        .setBodyParameter(Constants.KEY_CLIENT_ID, Constants.CLIENT_ID)
                        .setBodyParameter(Constants.KEY_CLIENT_SECRITE, Constants.CLIENT_SECRIT)
                        .setBodyParameter("username", email)
                        .setBodyParameter("password", password)
                        .setBodyParameter("grant_type", "password").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject token) {
                        hideProgressDialog();
                        if (e != null) {
                            e.printStackTrace();
                            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login_with_server);
                            return;
                        }
                        if (token == null) {
                            showAlertDialog(R.string.title_alert, R.string.msg_invalid_token_from_server);
                            return;
                        } else if (!token.toString().contains("access_token")) {
                            showAlertDialog(R.string.title_alert, R.string.msg_invalid_username);
                            resetForm();
                            return;
                        }
                        resetForm();
                        Prefs.putString("user_email", email);
                        Prefs.putString(mContext.getString(R.string.key_user_access_token), token.toString());
                        ServerAccessToken serverAccessToken = new Gson().fromJson(token, ServerAccessToken.class);

                        User user = new User();
                        user.setEmail(email);
                        user.setAccessToken(serverAccessToken.getAccess_token());
                        user.setLoginType(User.LOGIN_TYPE_EMAIL);
                        User.setLoggedInUser(user);

                        goToLandingPage();
                    }
                });
            } else {
                Ion.with(this)
                        .load(Constants.SERVER_URL + Constants.NAMESPACE_EMAIL_SIGNUP)
                        .setBodyParameter(Constants.KEY_CLIENT_ID, Constants.CLIENT_ID)
                        .setBodyParameter(Constants.KEY_CLIENT_SECRITE, Constants.CLIENT_SECRIT)
                        .setBodyParameter("username", email)
                        .setBodyParameter("email", email)
                        .setBodyParameter("password", password)
                        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject token) {
                        hideProgressDialog();
                        if (e != null) {
                            e.printStackTrace();
                            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login_with_server);
                            return;
                        }
                        if (token == null) {
                            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_sign_up);
                            return;
                        } else if (!token.toString().contains("id")) {
                            showAlertDialog(R.string.title_alert, R.string.msg_provided_email_in_used);
                            return;
                        }
                        resetForm();
                        showAlertDialog(R.string.title_alert, R.string.msg_signup_successful);
                    }
                });


            }

        }
    }

    private void resetForm() {
        edtEmail.setText("");
        edtPassword.setText("");
    }


    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        if (facebookCallbackManager != null)
            facebookCallbackManager.onActivityResult(requestCode, responseCode, intent);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginSignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        edtEmail.setAdapter(adapter);
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(getPlusClient()) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(getPlusClient());

                final User user = new User();
                user.setUserId(currentPerson.getId());
                //user.setAccess_token();

                //String name = currentPerson.getDisplayName();
                user.setGender(currentPerson.getGender() == Person.Gender.MALE ? "Male" : currentPerson.getGender() == Person.Gender.FEMALE ? "Female" : "Other");
                user.setFirst_name(currentPerson.getName().getGivenName());
                user.setLast_name(currentPerson.getName().getFamilyName());
                user.setEmail(Plus.AccountApi.getAccountName(getPlusClient()));
                user.setProfilePictureUrl(currentPerson.getImage().getUrl());
                user.setLoginType(User.LOGIN_TYPE_GOOGLE);


                User.setLoggedInUser(user);

                new RetrieveTokenTask() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        user.setAccessToken(s);
                        onUserLoggedInWithSocialMedia();
                    }
                }.execute(user.getEmail());


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information can not be retrieved", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // initialize facebook login

    private CallbackManager facebookCallbackManager;

    private void initializeFacebookLogin() {
        LoginButton fb_login_button = (LoginButton) findViewById(R.id.fb_login_button);
        fb_login_button.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));
        facebookCallbackManager = CallbackManager.Factory.create();
        fb_login_button.registerCallback(facebookCallbackManager, facebookCallback);
        Utils.printAPKKeyHash(this);

    }

    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(final LoginResult loginResult) {
            loadFBUserData();
        }

        @Override
        public void onCancel() {
            Log.e(App.getTag(), "cancel");
            hideProgressDialog();
            Toast.makeText(LoginSignupActivity.this, "Cancelled by User", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(FacebookException e) {
            Log.e(App.getTag(), "error");
            hideProgressDialog();
            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login);
        }
    };

    private void loadFBUserData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            Log.i(App.getTag(), "error");
                        } else {
//                          // logged in
                            User user = new User();
                            user.setFirst_name(me.optString("first_name"));
                            user.setLast_name(me.optString("last_name"));
                            user.setEmail(me.optString("email"));
                            user.setGender(me.optString("gender"));
                            user.setAccessToken(AccessToken.getCurrentAccessToken().getToken());
                            user.setUserId(AccessToken.getCurrentAccessToken().getUserId());
                            user.setLoginType(User.LOGIN_TYPE_FACEBOOK);
                            user.setProfilePictureUrl("https://graph.facebook.com/"
                                    + user.getUserId() + "/picture?type=large");
                            User.setLoggedInUser(user);

                            onUserLoggedInWithSocialMedia();

                        }
                    }
                });
        showProgressDialog(R.string.msg_sigining_in);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void signInFBToken() {

//        String accessToken= User.getLoggedInUser().getAccessToken();
//        SignInTask signInTask=new SignInTask();
//        signInTask.setSarverURL(Constants.SARVER_URL);
//        signInTask.setAPIString(Constants.API_EXCHANGE_FB_TOCKEN);
//        signInTask.parameters= getFBTokenLoginParameters(accessToken);
//
//        signInTask.execute();
//        signInTask.setEmailSignInCallback(new ResponseCallBack() {
//            @Override
//            public void onSuccess(String response) {
//
//                if (response != null) {
//                    Log.d(Constants.DEBUG_KEY, "GEt Server Token Against FB Token" + response.toString());
//                } else {
//                    Log.d(Constants.DEBUG_KEY, "GEt Server Token Against FB Token  NULL");
//                }
//
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginSignupActivity.this);
//                SharedPreferences.Editor editor = preferences.edit();
//
//                editor.putString(mContext.getString(R.string.key_user_access_token), response).commit();
//
//                hideProgressDialog();
//                onUserLoggedInWithSocialMedia();
//            }
//        });

    }


    public void gotoRegistration(View v) {

        if (validateInputFields()) {
            showProgressDialog(R.string.msg_loading);
            EmailSignUpTask signUpTask = new EmailSignUpTask();
            signUpTask.setEmailSignInCallback(new ResponseCallBack() {
                @Override
                public void onSuccess(String response) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(mContext.getString(R.string.key_user_access_token), response).commit();
                    Log.d(Constants.DEBUG_KEY, "Sign up Access Token " + response);
                    hideProgressDialog();
                    onUserLoggedInWithSocialMedia();
                }
            });
            signUpTask.setUserName(email);
            signUpTask.setPassword(password);
            signUpTask.execute();
        } else {
            Toast.makeText(LoginSignupActivity.this, "Not yet implemented.!", Toast.LENGTH_SHORT).show();
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(App.getTag(), e.getMessage());
            } catch (UserRecoverableAuthException e) {

            } catch (GoogleAuthException e) {
                Log.e(App.getTag(), e.getMessage());
            }
            return token;
        }

    }


}
