package com.govindadasu.androidboilerplate.bo;

/**
 * Created by mav on 22/11/15.
 */
public class User extends Member {
    private static User loggedInUser;
    public static int LOGIN_TYPE_EMAIL = 1;
    public static int LOGIN_TYPE_GOOGLE = 2;
    public static int LOGIN_TYPE_FACEBOOK = 3;

    private int loginType;
    private String accessToken;
    private String gender;
    private String profilePictureUrl;
    private String userId;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        User.loggedInUser = loggedInUser;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isFacebookUser() {
        return loginType == LOGIN_TYPE_FACEBOOK;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
