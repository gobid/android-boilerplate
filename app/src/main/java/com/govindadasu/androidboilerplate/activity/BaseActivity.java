package com.govindadasu.androidboilerplate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 */
public class BaseActivity extends Activity{
    private ProgressDialog progressDialog;

    protected void showProgressDialog(int messageResource) {
        try {
            hideKeyboard();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(messageResource));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }catch (Exception e){}
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void showAlertDialog(int title_res, int msg_res) {
        showAlertDialog(getString(title_res), getString(msg_res));
    }

    protected void showAlertDialog(String title, int msg_res) {
        showAlertDialog(title, getString(msg_res));
    }

    protected void showAlertDialog(int title_res, String msg) {
        showAlertDialog(getString(title_res), msg);
    }

    protected void showAlertDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    protected void hideKeyboard(){
        try{
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){}
    }
}
