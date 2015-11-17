package com.govindadasu.androidboilerplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class Profile_View extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile__view);
        TextView tv = (TextView)findViewById(R.id.userInfo);
        Intent intent = getIntent();
        String profileInfo = intent.getStringExtra(App.profileInfoText).toString();
        Log.e(App.getTag(), profileInfo);
        tv.setText(profileInfo);

    }

}
