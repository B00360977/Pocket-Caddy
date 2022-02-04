package com.example.golfapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class GlobalVariablesTest {

    GlobalVariables instance = new GlobalVariables();

    @Test
    public void check_instance() {
        assertNotNull(GlobalVariables.getInstance());
    }

    @Test
    public void check_set_UID() {
        String UID = "testNumber";
        instance.setUid(UID);
        assertEquals("Check UID is correct", instance.getUid(), UID);
    }

    @Test
    public void check_set_Username() {
        String userName = "Test Account";
        instance.setUserName(userName);
        assertEquals("Check Username is correct", instance.getUserName(), userName);
    }

    @Test
    public void check_set_Email() {
        String email = "testEmail@gmail.com";
        instance.setUserEmail(email);
        assertEquals("Check Email is correct", instance.getUserEmail(), email);
    }

    @Test
    public void check_set_ClubLocation() {
        String clubLocation = "Test Club Location";
        instance.setClubLocation(clubLocation);
        assertEquals("Check Club Location is correct", instance.getClubLocation(), clubLocation);
    }

}