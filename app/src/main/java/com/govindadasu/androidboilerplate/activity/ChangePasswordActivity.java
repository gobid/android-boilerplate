package com.govindadasu.androidboilerplate.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.ServerAccessToken;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.bo.UserProfileInfoAgainstToken;
import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.constant.Constants;
import com.govindadasu.androidboilerplate.task.ResetPassword;
import com.govindadasu.androidboilerplate.task.LoadProfileImage;

public class ChangePasswordActivity extends ActionBarActivity {

    private Context mContext;
    private ServerAccessToken serverAccessToken;
    private UserProfileInfoAgainstToken userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext=ChangePasswordActivity.this;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String serverAccessTokenStr=preferences.getString(mContext.getString(R.string.key_user_access_tocken), "");
        Gson gson=new Gson();
        serverAccessToken =  gson.fromJson(serverAccessTokenStr,ServerAccessToken.class);
//        String userInfo=preferences.getString(mContext.getString(R.string.key_user_info_from_tocken), "");
//         userData =new  Gson().fromJson(userInfo,UserProfileInfoAgainstToken.class);
//
//
//        EditText edtEmail,extNewPassword;
//        edtEmail= (EditText) findViewById(R.id.etxtEmail);
//        extNewPassword= (EditText) findViewById(R.id.etxtPassword);
//
//        edtEmail.setText(userData.getEmail());


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
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String serverAccessTokenStr=preferences.getString(mContext.getString(R.string.key_user_access_tocken), "");
        Gson gson=new Gson();
        serverAccessToken =  gson.fromJson(serverAccessTokenStr,ServerAccessToken.class);

        String newPassword="";
        ResetPassword changePasswordTask=new ResetPassword();
        changePasswordTask.setResponseListener(new ResponseCallBack() {
            @Override
            public void onSuccess(String response) {

                Log.d(Constants.DEBUG_KEY, "Change Password Response " + response);
              Toast.makeText(ChangePasswordActivity.this, mContext.getString(R.string.msg_password_reset), Toast.LENGTH_SHORT).show();
            }
        });
//        changePasswordTask.setAccessTocken(serverAccessToken.getAccess_token());
//        changePasswordTask.setNewPassword(newPassword);
//        changePasswordTask.setUserEmail(userData.getEmail());
//        changePasswordTask.setUserId(userData.getId()+"");
        changePasswordTask.execute();
    }
}
