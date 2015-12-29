package com.govindadasu.androidboilerplate.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * on 11/26/2015.
 */
public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public LoadProfileImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String pictureUrl = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(pictureUrl).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (bmImage != null && result != null) {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}