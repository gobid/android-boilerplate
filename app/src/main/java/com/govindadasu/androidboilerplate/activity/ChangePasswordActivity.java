package com.govindadasu.androidboilerplate.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.SarverAccessTocken;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.task.LoadProfileImage;

public class ChangePasswordActivity extends ActionBarActivity {

    private Context mContext;
    SarverAccessTocken sarverAccessTocken;
    User crruntLogindUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext=ChangePasswordActivity.this;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String sarverAccessTockenStr=preferences.getString(mContext.getString(R.string.key_user_access_tocken), "");
        Gson gson=new Gson();
        sarverAccessTocken=  gson.fromJson(sarverAccessTockenStr,SarverAccessTocken.class);
        crruntLogindUser=User.getLoggedInUser();

        EditText etxtEmail,etxtNewPasswordl;
        etxtEmail= (EditText) findViewById(R.id.etxtEmail);
        etxtNewPasswordl= (EditText) findViewById(R.id.etxtPassword);

        etxtEmail.setText(crruntLogindUser.getEmail());


        initView();
    }

    private void initView() {

        if(User.getLoggedInUser().getLoginType()==User.LOGIN_TYPE_FACEBOOK){
            ProfilePictureView pv = (ProfilePictureView) findViewById(R.id.fb_profile_pic_view);
            pv.setVisibility(View.VISIBLE);
            pv.setProfileId(User.getLoggedInUser().getUserId());
        } else if(User.getLoggedInUser().getLoginType()==User.LOGIN_TYPE_GOOGLE){
            ImageView pv = (ImageView) findViewById(R.id.google_profile_pic);
            new LoadProfileImage(pv).execute(User.getLoggedInUser().getProfilePictureUrl());
        }

    }


    public void changePassword(View view) {
        // hit change password
    }
}
