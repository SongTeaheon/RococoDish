package com.example.front_ui.DataModel;

import java.io.Serializable;

public class FragmentSotreData implements Serializable {

    private String storeName;
    private float storeDistance;
    private String storeAddress;
    private int storePosts;
    private float storeScore;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public float getStoreDistance() {
        return storeDistance;
    }

    public void setStoreDistance(float storeDistance) {
        this.storeDistance = storeDistance;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public int getStorePosts() {
        return storePosts;
    }

    public void setStorePosts(int storePosts) {
        this.storePosts = storePosts;
    }

    public float getStoreScore() {
        return storeScore;
    }

    public void setStoreScore(float storeScore) {
        this.storeScore = storeScore;
    }
}
