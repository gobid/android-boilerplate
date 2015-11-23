package com.govindadasu.androidboilerplate.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.task.EmailSiginInTask;
import com.govindadasu.androidboilerplate.callback.EmailSignInCallback;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.util.Utils;

import org.json.JSONObject;

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


    @Override
    protected void onPlusClientRevokeAccess() {

    }

    @Override
    protected void onPlusClientSignIn() {
        getProfileInformation();
    }

    @Override
    protected void onPlusClientSignOut() {

    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        if (show)
            showProgressDialog(R.string.msg_sigining_in);
        else
            hideProgressDialog();
    }

    @Override
    protected void updateConnectButtonState() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login_signup);
        // email
        initializeEmailLogin();
        // google
        initializeGoogleLogin();
        // facebook
        initializeFacebookLogin();

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(TAG_LOGOUT, false)) {
            onUserLoggedOut();
        } else {
            checkIfUserAlreadyLoggedIn();
        }
    }

    private void initializeGoogleLogin() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_login_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void checkIfUserAlreadyLoggedIn() {
        // facebook
        if (AccessToken.getCurrentAccessToken() != null) {
            showProgressDialog(R.string.msg_loading);

            loadFBUserData();
        } else if (false) {// ceheck email login here #TODO


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

    private void onUserLoggedIn() {
        startActivity(new Intent(this, Profile_View.class));
        finish();
    }


    private void onUserLoggedOut() {
        // if (User.getLoggedInUser().getLoginType() == User.LOGIN_TYPE_FACEBOOK) {
        LoginManager.getInstance().logOut();
        //} else if (User.getLoggedInUser().getLoginType() == User.LOGIN_TYPE_GOOGLE) {
        signOut();
        // }
        User.setLoggedInUser(null);
        startLoginFlow();
    }

    private void showProgressDialog(int messageResource) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(messageResource));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private void showAlertDialog(int title_res, int msg_res) {
        showAlertDialog(getString(title_res), getString(msg_res));
    }

    private void showAlertDialog(String title, int msg_res) {
        showAlertDialog(title, getString(msg_res));
    }

    private void showAlertDialog(int title_res, String msg) {
        showAlertDialog(getString(title_res), msg);
    }

    private void showAlertDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.email_login_button);
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
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


    private void attemptLogin() {

        // Reset errors.
        edtEmail.setError(null);
        edtPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgressDialog(R.string.msg_sigining_in);

            final EmailSiginInTask emailSiginInTask = new EmailSiginInTask();
            emailSiginInTask.parameters =
                    "grant_type=password&" +
                            "client_id=TxjxrOBvlhnjcsG7MUSSBoOa0b92EJkg7LR9JxvU&" +
                            "client_secret=4UrBWZwNcYVhd1y9XTKr2zu9IlZeb67H5vShIxJ4wh26zCXEIMGrmKVPz9Kfni1Y0NfEdug5GMaZaVVmxHjKB54tBHfKCYGTuCFDmDuuQw7l20lE7TWdjCintnIjNpVZ&" +
                            "username=" + email +
                            "&password=" + password;

            emailSiginInTask.setEmailSignInCallback(new EmailSignInCallback() {
                @Override
                public void onSuccess(String response) {

                    if (response == null) {
                        showAlertDialog(R.string.title_alert, R.string.msg_unable_to_login);
                        return;
                    }
                    if (response.toString().contains("username") && response.toString().contains("email")) {

                        Intent intent = new Intent(getBaseContext(), Profile_View.class);
                        intent.putExtra(App.profileInfoText, response);
                        startActivity(intent);
                    } else {
                        showAlertDialog(R.string.title_alert, response);
                    }
                }

            });

            emailSiginInTask.execute(R.string.rest_api_url + "/auth/token");

        }
    }


    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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

                User user = new User();
                user.setUserId(currentPerson.getId());
                //user.setAccessToken();
                user.setFirstName(currentPerson.getDisplayName());
                user.setEmail(Plus.AccountApi.getAccountName(getPlusClient()));
                user.setProfilePictureUrl(currentPerson.getImage().getUrl());
                user.setLoginType(User.LOGIN_TYPE_GOOGLE);
                User.setLoggedInUser(user);
                onUserLoggedIn();


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
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
                            user.setFirstName(me.optString("first_name"));
                            user.setLastName(me.optString("last_name"));
                            user.setEmail(me.optString("email"));
                            user.setGender(me.optString("gender"));
                            user.setAccessToken(AccessToken.getCurrentAccessToken().getToken());
                            user.setUserId(AccessToken.getCurrentAccessToken().getUserId());
                            user.setLoginType(User.LOGIN_TYPE_FACEBOOK);
                            User.setLoggedInUser(user);
                            hideProgressDialog();
                            onUserLoggedIn();
                        }
                    }
                });
        showProgressDialog(R.string.msg_sigining_in);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }


}