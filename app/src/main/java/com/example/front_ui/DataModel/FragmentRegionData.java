package com.example.front_ui.DataModel;

import java.io.Serializable;

public class FragmentRegionData implements Serializable {

    private int imageViewRegion; //일단 빠르게 작성하려고 int로 해놓았으니 String으로 바꿔주세요 ㅜㅜ
    private String itemRegionName;
    private float itemRegionDistance;
    private String itemRegionAddress;
    private int itemRegionPosts;

    public int getImageViewRegion() {
        return imageViewRegion;
    }

    public void setImageViewRegion(int imageViewRegion) {
        this.imageViewRegion = imageViewRegion;
    }

    public String getItemRegionName() {
        return itemRegionName;
    }

    public void setItemRegionName(String itemRegionName) {
        this.itemRegionName = itemRegionName;
    }

    public float getItemRegionDistance() {
        return itemRegionDistance;
    }

    public void setItemRegionDistance(float itemRegionDistance) {
        this.itemRegionDistance = itemRegionDistance;
    }

    public String getItemRegionAddress() {
        return itemRegionAddress;
    }

    public void setItemRegionAddress(String itemRegionAddress) {
        this.itemRegionAddress = itemRegionAddress;
    }

    public int getItemRegionPosts() {
        return itemRegionPosts;
    }

    public void setItemRegionPosts(int itemRegionPosts) {
        this.itemRegionPosts = itemRegionPosts;
    }
}
