package com.govindadasu.androidboilerplate.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.SarverAccessTocken;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.bo.UserProfileInfoAgaistTocken;
import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.constant.Constants;
import com.govindadasu.androidboilerplate.task.ChangePasswordTask;
import com.govindadasu.androidboilerplate.task.LoadProfileImage;

public class ChangePasswordActivity extends ActionBarActivity {

    private Context mContext;
    SarverAccessTocken sarverAccessTocken;
    UserProfileInfoAgaistTocken userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext=ChangePasswordActivity.this;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String sarverAccessTockenStr=preferences.getString(mContext.getString(R.string.key_user_access_tocken), "");
        Gson gson=new Gson();
        sarverAccessTocken=  gson.fromJson(sarverAccessTockenStr,SarverAccessTocken.class);

        String userInfo=preferences.getString(mContext.getString(R.string.key_user_info_from_tocken), "");
         userData =new  Gson().fromJson(userInfo,UserProfileInfoAgaistTocken.class);


        EditText etxtEmail,etxtNewPasswordl;
        etxtEmail= (EditText) findViewById(R.id.etxtEmail);
        etxtNewPasswordl= (EditText) findViewById(R.id.etxtPassword);

        etxtEmail.setText(userData.getEmail());


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
        String newPassword="abc";
        ChangePasswordTask changePasswordTask=new ChangePasswordTask();
        changePasswordTask.setResponseListener(new ResponseCallBack() {
            @Override
            public void onSuccess(String response) {

                Log.d(Constants.DEBUG_KEY, "Change Password Response " + response);
              //  Toast.makeText(ChangePasswordActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        changePasswordTask.setAccessTocken(sarverAccessTocken.getAccess_token());
        changePasswordTask.setNewPassword(newPassword);
        changePasswordTask.setUserEmail(userData.getEmail());
        changePasswordTask.setUserId(userData.getId()+"");
        changePasswordTask.execute();
    }
}
