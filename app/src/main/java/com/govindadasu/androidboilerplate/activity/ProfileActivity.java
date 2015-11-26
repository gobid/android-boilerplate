package com.govindadasu.androidboilerplate.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.SarverAccessTocken;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.task.LoadProfileImage;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initProfile();



    }

    private void initProfile() {

        TextView txtFirstName,txtLastName,txtGender,txtEmail,txtUserId;
        txtEmail= (TextView) findViewById(R.id.txtEmail);
        txtFirstName= (TextView) findViewById(R.id.txtFirstName);
        txtLastName= (TextView) findViewById(R.id.txtLastName);
        txtGender= (TextView) findViewById(R.id.txtGender);
        txtUserId= (TextView) findViewById(R.id.txtUserId);

        User crruntLoginUser= User.getLoggedInUser();

        txtEmail.setText(crruntLoginUser.getEmail());
        txtGender.setText(crruntLoginUser.getGender());
        txtFirstName.setText(crruntLoginUser.getFirstName());
        txtLastName.setText(crruntLoginUser.getLastName());
        txtUserId.setText(crruntLoginUser.getUserId());


    }

}
