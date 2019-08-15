package com.rococodish.front_ui.FCM;

public class DataModel {

    private String postingId;
    private String storeId;

    public DataModel(String postingId, String storeId) {
        this.postingId = postingId;
        this.storeId = storeId;
    }

    public String getPostingId() {
        return postingId;
    }

    public void setPostingId(String postingId) {
        this.postingId = postingId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
