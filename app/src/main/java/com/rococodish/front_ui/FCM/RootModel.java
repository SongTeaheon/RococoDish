package com.rococodish.front_ui.FCM;

import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

//    @SerializedName("data")
//    private String data;

    public RootModel(String token,
                     NotificationModel notification){
        this.token = token;
        this.notification = notification;
//        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

//    public String getData() {
//        return data;
//    }
//
//    public void setData(String data) {
//        this.data = data;
//    }
}
