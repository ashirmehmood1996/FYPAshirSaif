package com.android.example.fypnotify.Models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private int memberID;
    private String title, message, timeStamp, recievers,uriCSV;


    public NotificationModel(int memberID, String title, String message, String timeStamp, String recievers,String uriCSV) {
        this.memberID = memberID;
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
        this.recievers = recievers;
        this.uriCSV=uriCSV;

    }

    public int getMemberID() {
        return memberID;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getRecievers() {
        return recievers;
    }

    public String getUriCSV() {
        return uriCSV;
    }
}
