package com.example.cibi.messenger.Model;

import java.io.Serializable;

/**
 * Created by Cibi on 28.3.2016.
 */
public class User implements Serializable{

    private String email;
    private String name;
    private String loginName;
    private String cookie;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

}
