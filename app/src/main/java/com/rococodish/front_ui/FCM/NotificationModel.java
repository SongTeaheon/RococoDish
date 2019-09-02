package com.rococodish.front_ui.FCM;

public class NotificationModel {

    private String title;
    private String body;
    private String click_action;

    public NotificationModel(String title,
                             String body,
                             String click_action) {
        this.title = title;
        this.body = body;
        this.click_action = click_action;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }
}
