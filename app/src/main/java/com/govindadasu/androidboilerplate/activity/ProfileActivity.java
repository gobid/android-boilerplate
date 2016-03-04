package com.govindadasu.androidboilerplate.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.Member;
import com.govindadasu.androidboilerplate.bo.ServerAccessToken;
import com.govindadasu.androidboilerplate.constant.Constants;
import com.govindadasu.androidboilerplate.util.Prefs;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ProfileActivity extends BaseActivity {

    private TextView txtFirstName, txtLastName, txtGender, txtEmail, txtUserId;
    private int djangoUserId = -1;
    private ServerAccessToken accessToken;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initProfile();

    }

    private void initProfile() {


        accessToken = gson.fromJson(Prefs.getString(getString(R.string.key_user_access_token), ""), ServerAccessToken.class);
        if (accessToken != null) {
            showProgressDialog(R.string.processing);
            Ion.with(this)
                    .load("GET", Constants.SERVER_URL + Constants.NAMESPACE_ME).addHeader("Authorization", " Django " + accessToken.getAccess_token())
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject response) {
                    if (e != null) {
                        hideProgressDialog();

                        e.printStackTrace();
                        showAlertDialog(R.string.title_alert, R.string.msg_unable_to_get_user_data);
                        return;
                    }
                    if (response.has("id")) {
                        djangoUserId = response.get("id").getAsInt();

                        Ion.with(ProfileActivity.this)
                                .load("GET", Constants.SERVER_URL + Constants.NAMESPACE_ME_INFO.replace("#", djangoUserId + "")).addHeader("Authorization", " Django " + accessToken.getAccess_token())
                                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject response) {
                                hideProgressDialog();
                                if (e != null) {
                                    e.printStackTrace();
                                    showAlertDialog(R.string.title_alert, R.string.msg_unable_to_get_user_data);
                                    return;
                                }
                                Member member = gson.fromJson(response, Member.class);
                                if (member != null) {
                                    populateUserData(member);
                                } else {
                                    showAlertDialog(R.string.title_alert, R.string.msg_unable_to_get_user_data);
                                }
                            }
                        });
                    } else {
                        hideProgressDialog();
                        showAlertDialog(R.string.title_alert, R.string.msg_no_user_id_in_response);
                    }
                }
            });

        }


    }

    private void populateUserData(Member member) {

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtFirstName = (TextView) findViewById(R.id.txtFirstName);
        txtLastName = (TextView) findViewById(R.id.txtLastName);
        txtGender = (TextView) findViewById(R.id.txtGender);
        txtUserId = (TextView) findViewById(R.id.txtUserId);


        txtEmail.setText(member.getEmail());
        txtFirstName.setText(member.getFirst_name());
        txtLastName.setText(member.getLast_name());
        txtUserId.setText(member.getUsername());
        txtUserId.setEnabled(false);
        txtEmail.setEnabled(false);
    }


    public void updateProfile(View view) {
        if (txtFirstName != null && txtLastName != null) {
            String firstName = txtFirstName.getText().toString();
            String lastName = txtLastName.getText().toString();
            if (TextUtils.isEmpty(firstName)) {
                txtFirstName.setError(getString(R.string.error_field_required));
            } else if (TextUtils.isEmpty(lastName)) {
                txtLastName.setError(getString(R.string.error_field_required));
            } else {
                showProgressDialog(R.string.processing);
                Ion.with(ProfileActivity.this)
                        .load("PATCH", Constants.SERVER_URL + Constants.NAMESPACE_ME_INFO.replace("#", djangoUserId + "")).addHeader("Authorization", " Django " + accessToken.getAccess_token())
                        .setBodyParameter("first_name", firstName)
                        .setBodyParameter("last_name", lastName)
                        .setBodyParameter(Constants.KEY_CLIENT_SECRITE, Constants.CLIENT_SECRIT)
                        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject response) {
                        hideProgressDialog();
                        if (e != null) {
                            e.printStackTrace();
                            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_get_user_data);
                            return;
                        }
                        Member member = gson.fromJson(response, Member.class);
                        if (member != null) {
                            populateUserData(member);
                        } else {
                            showAlertDialog(R.string.title_alert, R.string.msg_unable_to_get_user_data);
                        }
                    }
                });
            }

        }
    }
}
