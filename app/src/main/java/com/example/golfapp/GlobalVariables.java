package com.example.golfapp;

public class GlobalVariables {

    private String userName, userEmail, uid;

    private static final GlobalVariables instance = new GlobalVariables();
    public static GlobalVariables getInstance() {
        return instance;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUserName(String string) {
        this.userName = string;
    }

    public void setUserEmail(String string) {
        this.userEmail = string;
    }

    public void setUid(String string) {
        this.uid = string;
    }
}

