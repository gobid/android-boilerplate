package com.govindadasu.androidboilerplate;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Ali on 11/22/2015.
 */
public class Utils {


    public static  boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
