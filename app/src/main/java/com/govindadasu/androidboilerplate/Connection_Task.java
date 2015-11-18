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
    @Override
    protected String doInBackground(String... urls) {
        String output = null;
        for (String url : urls) {
            output = getOutputFromUrl(url);
        }
        return output;
    }

    private String getOutputFromUrl(String url_string) {
        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream = getHttpConnection(new URL(url_string));
            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(stream));
            String s = "";
            while ((s = buffer.readLine()) != null)
                output.append(s);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return output.toString();
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(URL url)
            throws IOException {
        InputStream stream = null;
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            //post

            OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
            String urlParameters = parameters;
            writer.write(urlParameters);
            writer.flush();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e(App.getTag(), "HTTP_OK");
                stream = httpConnection.getInputStream();
            }
            else{
                Log.e(App.getTag(), "couldn't connect code" + httpConnection.getResponseCode());
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    @Override
    protected void onPostExecute(String output) {
        Log.e(App.getTag(), "request output:" + output);
    }
}