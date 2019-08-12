package com.rococodish.front_ui.DataModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class StoreInfo {
    public String name;
    public String kakaoId;
    public String storeId;
    public double aver_star;
    public int postingNum;
    public String address;
    private ArrayList<PostingInfo> allItemsInSection;
    public GeoPoint geoPoint;

    private int viewId; //recyclervieadapter에서만 쓰이는 데이터입니다. 신경안쓰셔도 됩니당


    public StoreInfo() {

    }

    public StoreInfo(String name,
                     float aver_star,
                     String address,
                     GeoPoint geoPoint
                     ) {
        this.name = name;
        this.aver_star = aver_star;
        this.address = address;
        this.geoPoint = geoPoint;

    }


    public String getName() {
        return name;
    }

    public double getAver_star() {
        return aver_star;
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PostingInfo> getAllItemsInSection() {

        if(allItemsInSection == null)
            allItemsInSection = new ArrayList<>();
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<PostingInfo> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getPostingNum() {
        return postingNum;
    }

    public void setPostingNum(int postingNum) {
        this.postingNum = postingNum;
    }

    public void setAver_star(double aver_star) {
        this.aver_star = aver_star;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}