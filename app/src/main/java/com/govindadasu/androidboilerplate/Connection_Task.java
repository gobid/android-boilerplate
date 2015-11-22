package com.govindadasu.androidboilerplate;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by abhishekgarg on 11/17/15.
 */
public class Connection_Task extends AsyncTask<String, Void, String> {
    public String parameters="";
    private String sarverURL="";
    public String final_output = "nothing right now";
    int responseCode = -1;
    private CallBackListener responseListener;

    @Override
    protected String doInBackground(String... urls) {
        String output = null;

            output = getOutputFromUrl(sarverURL);

        final_output = output;
        return output;
    }

    private String getOutputFromUrl(String url_string) {
        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream = getHttpConnection(new URL(url_string));
            if(stream==null) return final_output;
            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(stream));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.append(s);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if(output.toString().contains("username")&&output.toString().contains("email"))
            return "Registered Successfully";
        return output.toString();
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(URL url)
            throws IOException {
        InputStream stream = null;
        Log.d(Constents.DEBUG_KEY,"Sarver url "+url.toString());
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            //post

            OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
            String urlParameters = parameters;
            Log.d(Constents.DEBUG_KEY,"parameters : "+parameters);
            Log.e(App.getTag(), urlParameters);
            writer.write(urlParameters);
            writer.flush();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK || httpConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED || httpConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                Log.e(App.getTag(), "HTTP_OK");
                stream = httpConnection.getInputStream();
                responseCode = httpConnection.getResponseCode();
            }
            else{final_output="Already Registered";
                Log.e(App.getTag(), "couldn't connect code: " + httpConnection.getResponseCode());
                Log.d(Constents.DEBUG_KEY, "couldn't connect code: " + httpConnection.getResponseCode());
            }
            writer.close();
        } catch (Exception ex) {
            Log.d(Constents.DEBUG_KEY,ex.getMessage());
            ex.printStackTrace();
        }
        return stream;
    }

    @Override
    protected void onPostExecute(String output) {
        responseListener.onSuccess(output);
        Log.e(App.getTag(), "request output:" + output);


    }

    public void setResponseListener(CallBackListener responseListener) {
        this.responseListener = responseListener;
    }

    public String getSarverURL() {
        return sarverURL;
    }

    public void setSarverURL(String sarverURL) {
        this.sarverURL = sarverURL;
    }
}