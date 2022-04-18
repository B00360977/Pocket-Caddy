package com.example.golfapp;

/**
 * This is a singleton class that the app uses to store regularly used variables
 */

public class GlobalVariables {

    private String userName, userEmail, uid, clubLocation, recommendation;
    private int roundID;

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

    public String getClubLocation() {
        return clubLocation;
    }

    public String getRecommendation() {
        return recommendation;
    };

    public int getRoundID() {
      return roundID;
    };

    public void setUserName(String string) {
        this.userName = string;
    }

    public void setUserEmail(String string) {
        this.userEmail = string;
    }

    public void setUid(String string) {
        this.uid = string;
    }

    public void setClubLocation(String string) {
        this.clubLocation = string;
    }

    public void setRecommendation(String string) {
        this.recommendation = string;
    }

    public void setRoundID(int round) {
      this.roundID = round;
    };
}

