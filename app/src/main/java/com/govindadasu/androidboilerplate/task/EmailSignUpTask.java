package com.govindadasu.androidboilerplate.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.bo.SignUpResponse;
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


public class EmailSignUpTask extends AsyncTask<String, Void, String> {


    int responseCode = -1;
    private ResponseCallBack emailSignInCallback;
    private String userName;
    private String password;

    @Override
    protected String doInBackground(String... urls) {

        return getOutputFromUrl(getServerURL());
    }

    private String getOutputFromUrl(String url_string) {

        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream = getHttpConnection(new URL(url_string));
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


    private InputStream getHttpConnection(URL url)
            throws IOException {
        InputStream stream = null;
        Log.d(Constants.DEBUG_KEY, "Sarver url " + url.toString());
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
            String urlParameters = getParameters();
            Log.d(Constants.DEBUG_KEY,"parameters : "+urlParameters);
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

    @Override
    protected void onPostExecute(String output) {

        Log.e(App.getTag(), "Email Sign up Task output:" + output);
        if(output==null) { emailSignInCallback.onSuccess(output);}
      SignUpResponse signUpResponse=  new Gson().fromJson(output, SignUpResponse.class);


        EmailSignInTask signInTask=new EmailSignInTask();
        signInTask.setPassword(password);
        signInTask.setUserName(signUpResponse.getEmail());
        signInTask.setEmailSignInCallback(emailSignInCallback);
        signInTask.execute();




    }

    public void setEmailSignInCallback(ResponseCallBack emailSignInCallback) {
        this.emailSignInCallback = emailSignInCallback;
    }

    public String getServerURL() {
        return Constants.BASE_SERVER_URL+Constants.NAMESPACE_EMAIL_SIGNUP ;
    }



    public String getParameters() {
        return Constants.KEY_USER_NAME+"="+userName+"&"+Constants.KEY_EMAIL+"="+userName+"&"+Constants.KEY_PASSWORD+"="+password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}