package com.govindadasu.androidboilerplate.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.facebook.login.widget.ProfilePictureView;
import com.govindadasu.androidboilerplate.R;
import com.govindadasu.androidboilerplate.bo.User;
import com.govindadasu.androidboilerplate.task.LoadProfileImage;

/**
 * Created by mav on 26/11/15.
 */
public class ProfileImageView extends CircleImageView {
    public ProfileImageView(Context context) {
        super(context);
    }
    private boolean imageAdded = false;
    public ProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(imageAdded && (User.getLoggedInUser()==null || User.getLoggedInUser().getProfilePictureUrl()==null)) {
            setImageResource(R.drawable.default_profile);
            imageAdded = false;
        }
        if(!imageAdded && User.getLoggedInUser()!=null && User.getLoggedInUser().getProfilePictureUrl()!=null){
            new LoadProfileImage(this).execute(User.getLoggedInUser().getProfilePictureUrl());
            imageAdded = true;
        }
    }
}
