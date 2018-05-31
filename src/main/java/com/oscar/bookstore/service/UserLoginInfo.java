package com.oscar.bookstore.service;

import com.auth0.jwt.interfaces.DecodedJWT;

public class UserLoginInfo {

    private static UserLoginInfo userLoginInfo = new UserLoginInfo();
    private Integer userId;

    private UserLoginInfo(){

    }

    public static UserLoginInfo getInstance() {
        return userLoginInfo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
