package com.govindadasu.androidboilerplate.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;

import com.govindadasu.androidboilerplate.app.App;

import java.security.MessageDigest;

public class Utils {


    public static  boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void printAPKKeyHash(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(App.getTag(), Base64.encodeToString(md.digest(), Base64.DEFAULT)+"           "+md.digest());
            }
        } catch (Exception e) {
        }
    }
}
