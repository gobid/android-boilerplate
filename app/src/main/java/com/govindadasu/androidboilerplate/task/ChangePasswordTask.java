package com.govindadasu.androidboilerplate.task;

import android.os.AsyncTask;
import android.util.Log;

import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.constant.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *   on 11/26/2015.
 */
public class ChangePasswordTask extends AsyncTask<String, Void, String> {

    int responseCode = -1;
    private ResponseCallBack responseListener;

    private String userEmail;
    private String userId;
    private String accessTocken;
    private String newPassword;


    private String getOutputFromUrl(String url_string,String parameters) {

        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream = getHttpConnection(new URL(url_string),parameters);
            if(stream==null) return null;
            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(stream));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.append(s);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return output.toString();
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(URL url,String parameters)
            throws IOException {
        InputStream stream = null;
        Log.d(Constants.DEBUG_KEY, "Sarver url " + url.toString());
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            //post

            OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
            String urlParameters = parameters;
            Log.d(Constants.DEBUG_KEY,"parameters : "+parameters);
            Log.e(App.getTag(), urlParameters);
            writer.write(urlParameters);
            writer.flush();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK || httpConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED || httpConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                Log.e(App.getTag(), "HTTP_OK");
                stream = httpConnection.getInputStream();
                responseCode = httpConnection.getResponseCode();
            }

            writer.close();
        } catch (Exception ex) {
            Log.d(Constants.DEBUG_KEY,ex.getMessage());
            ex.printStackTrace();
        }
        return stream;
    }



    public void setResponseListener(ResponseCallBack responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    protected String doInBackground(String... params) {

         getOutputFromUrl(getRsetURL(),getRestParameters());
       return getOutputFromUrl(getConfirmURL(),getConfirmParameters());
    }

    @Override
    protected void onPostExecute(String output) {

        responseListener.onSuccess(output);
        Log.e(App.getTag(), "request output:" + output);
    }

    public String getRsetURL() {
        return Constants.BASE_SARVER_URL+Constants.NAMESPACE_PASSWORD_RESET;
    }

    public String getConfirmURL() {
        return Constants.SARVER_URL + Constants.NAMESPACE_PASSWORD_RESET_CONFIRM;
    }

    public String getRestParameters() {
        return Constants.KEY_EMAIL+"="+userEmail;
    }

    public String getConfirmParameters() {
        return Constants.KEY_USER_ID+"="+userId+"&"+Constants.KEY_TOCKEN+"="+accessTocken+"&"+Constants.KEY_NEW_PASSWORD+"="+newPassword;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccessTocken(String accessTocken) {
        this.accessTocken = accessTocken;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
