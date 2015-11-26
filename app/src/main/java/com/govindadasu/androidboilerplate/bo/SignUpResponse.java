package com.govindadasu.androidboilerplate.bo;

/**
 * Created by Ali on 11/27/2015.
 */
public class SignUpResponse {

    private String email;
    private String username;
    private Integer id;

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}