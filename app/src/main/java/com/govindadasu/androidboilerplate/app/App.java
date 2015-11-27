package com.govindadasu.androidboilerplate.app;

import android.app.Application;
import android.content.ContextWrapper;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.govindadasu.androidboilerplate.util.Prefs;

public class App extends Application{
    private LoginButton loginButton;
    protected static CallbackManager mCallbackManager;
    protected static ProfileTracker mProfileTracker;
    private com.facebook.AccessToken accessToken;
    public static String profileInfoText = "Profile Info: ";

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static String getTag() {
        String tag = "SimpleAndroid";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = ste[i + 1].getClassName() + "_" + ste[i + 1].getLineNumber();
            }
        }
        return tag;
    }
}
