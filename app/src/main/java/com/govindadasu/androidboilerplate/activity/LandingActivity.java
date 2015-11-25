package com.govindadasu.androidboilerplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.govindadasu.androidboilerplate.R;

public class LandingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }


    public void viewProfile(View v)
    {
        Intent intent=new Intent(LandingActivity.this,ProfileActivity.class);
        startActivity(intent);

    }
    public void resetPassword(View view)
    {

        Intent intent=new Intent(LandingActivity.this,ChangePasswordActivity.class);
        startActivity(intent);
    }

}
