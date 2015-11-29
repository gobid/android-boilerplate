package com.govindadasu.androidboilerplate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.User;

public class LandingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }


    public void viewProfile(View v) {
        if (User.getLoggedInUser() == null) {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(LandingActivity.this, ProfileActivity.class);
        startActivity(intent);

    }

    public void resetPassword(View view) {
        if (User.getLoggedInUser() == null) {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void logoutUser(View view) {
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.putExtra(LoginSignupActivity.TAG_LOGOUT, true);
        startActivity(intent);
        finish();
    }

    public void otherMenuClicked(View view) {
        Toast.makeText(LandingActivity.this, LandingActivity.this.getString(R.string.msg_other_menu), Toast.LENGTH_SHORT).show();

    }
}
