package com.govindadasu.androidboilerplate.task;

import android.os.AsyncTask;
import android.util.Log;

import com.govindadasu.androidboilerplate.callback.ResponseCallBack;
import com.govindadasu.androidboilerplate.app.App;
import com.govindadasu.androidboilerplate.constant.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SignInTask extends AsyncTask<String, Void, String> {
    public String parameters="";
    private String serverURL="";
    int responseCode = -1;
    private ResponseCallBack emailSignInCallback;

    private String authenticationToken=null;

    @Override
    protected String doInBackground(String... urls) {
        return getOutputFromUrl(serverURL);
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

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(URL url)
            throws IOException {
        InputStream stream = null;
        Log.d(Constants.DEBUG_KEY,"Sarver url "+url.toString());
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            if(authenticationToken!=null)
            { httpConnection.setRequestProperty("Authorization", authenticationToken);
                httpConnection.setRequestMethod("GET");
            }
            else
            { httpConnection.setRequestMethod("POST");}

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
//            else{final_output="Already Registered";
//                Log.e(App.getTag(), "couldn't connect code: " + httpConnection.getResponseCode());
//                Log.d(Constents.DEBUG_KEY, "couldn't connect code: " + httpConnection.getResponseCode());
//            }
            writer.close();
        } catch (Exception ex) {
            Log.d(Constants.DEBUG_KEY,ex.getMessage());
            ex.printStackTrace();
        }
        return stream;
    }

    @Override
    protected void onPostExecute(String output) {
        emailSignInCallback.onSuccess(output);
        Log.e(App.getTag(), "request output:" + output);


    }

    public void setEmailSignInCallback(ResponseCallBack emailSignInCallback) {
        this.emailSignInCallback = emailSignInCallback;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String sarverURL) {
        this.serverURL = sarverURL;
    }

    public void setAPIString(String apiURL) {

        serverURL+="/"+apiURL;
    }


}