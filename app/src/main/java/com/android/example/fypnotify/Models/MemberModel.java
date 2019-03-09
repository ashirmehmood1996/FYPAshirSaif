package com.android.example.fypnotify.Models;

import java.io.Serializable;

public class MemberModel implements Serializable {
    private int ID; // id in sqlite database
    private String name, //data related
            phoneNumber,
            memberType,
            email,
            userType;
    private boolean selected; // for ui selection purpose
    private boolean isOnWhatsApp = false; //metadata


    public MemberModel(int ID, String name, String phoneNumber, String memberType) {
        this.ID = ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.memberType = memberType;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMemberType() {
        return memberType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnWhatsApp() {
        return isOnWhatsApp;
    }

    public void setIsOnWhatsApp(boolean onWhatsApp) {
        isOnWhatsApp = onWhatsApp;
    }
}
