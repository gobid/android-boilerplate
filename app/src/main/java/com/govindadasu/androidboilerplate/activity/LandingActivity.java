package com.govindadasu.androidboilerplate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.ServerAccessToken;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.constant.Constants;
import com.govindadasu.androidboilerplate.task.ResetPassword;

public class LandingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }


    public void viewProfile(View v)
    {
        if(User.getLoggedInUser()==null){
            Toast.makeText(this,"Disabled due to activation issue",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent=new Intent(LandingActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();

    }
    public void resetPassword(View view)
    {
//        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
//        String serverAccessTokenStr=preferences.getString(this.getString(R.string.key_user_access_tocken), "");
//        Gson gson=new Gson();
//        ServerAccessToken serverAccessToken = gson.fromJson(serverAccessTokenStr, ServerAccessToken.class);

        if(User.getLoggedInUser()==null){
            Toast.makeText(this,"Disabled due to activation issue",Toast.LENGTH_LONG).show();
            return;
        }
        ResetPassword resetPasswordTask=new ResetPassword();
        resetPasswordTask.setResponseListener(new ResponseCallBack() {
            @Override
            public void onSuccess(String response) {

                Log.d(Constants.DEBUG_KEY, "Change Password Response " + response);
                Toast.makeText(LandingActivity.this, LandingActivity.this.getString(R.string.msg_password_reset), Toast.LENGTH_SHORT).show();
            }
        });
        resetPasswordTask.setUserEmail(User.getLoggedInUser().getEmail());
        resetPasswordTask.execute();

    }

    public void logoutUser(View view) {
        Intent intent = new Intent(this,LoginSignupActivity.class);
        intent.putExtra(LoginSignupActivity.TAG_LOGOUT, true);
        startActivity(intent);
        finish();
    }

}
