package com.govindadasu.androidboilerplate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

public class Profile_View extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile__view);
        TextView tv = (TextView)findViewById(R.id.userInfo);
        Intent intent = getIntent();
        String profileInfo = intent.getStringExtra(App.profileInfoText).toString();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            JSONObject jsonObj = new JSONObject(profileInfo);
            editor.putString("access_token", jsonObj.get("access_token").toString());
            editor.commit();
        }
        catch (Exception e){
            Log.e(App.getTag(), "error parsing json");
        }
        Log.e(App.getTag(), profileInfo);
        tv.setText(profileInfo);

    }

}
