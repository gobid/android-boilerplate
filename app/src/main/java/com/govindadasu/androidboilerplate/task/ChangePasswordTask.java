package com.govindadasu.androidboilerplate.task;

import android.os.AsyncTask;
import android.util.Log;

import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.callback.EmailSignInCallback;
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
    public String parameters="";
    private String sarverURL="";
    int responseCode = -1;
    private EmailSignInCallback emailSignInCallback;

    private String autheanticationTocken=null;



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

    // Makes HttpURLConnection and returns InputStream
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



    public void setEmailSignInCallback(EmailSignInCallback emailSignInCallback) {
        this.emailSignInCallback = emailSignInCallback;
    }

    public String getSarverURL() {
        return sarverURL;
    }

    public void setSarverURL(String sarverURL) {
        this.sarverURL = sarverURL;
    }

    public void setAPIString(String apiURL) {

        sarverURL+="/"+apiURL;
    }

    public void setAutheanticationTocken(String autheanticationTocken) {
        this.autheanticationTocken = autheanticationTocken;
    }



    @Override
    protected String doInBackground(String... params) {
        return getOutputFromUrl(sarverURL);
    }

    @Override
    protected void onPostExecute(String output) {

        emailSignInCallback.onSuccess(output);
        Log.e(App.getTag(), "request output:" + output);
    }
}
