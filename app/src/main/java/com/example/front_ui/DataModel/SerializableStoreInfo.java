package com.example.front_ui.DataModel;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableStoreInfo implements Serializable {
    public String name;
    public String kakaoId;
    public String storeId;
    public double aver_star;
    public int postingNum;
    public String address;
    private ArrayList<PostingInfo> allItemsInSection;
    double lat;//latitude
    double lon;//longtitude

    public SerializableStoreInfo(StoreInfo storeInfo){
        name = storeInfo.getName();
        kakaoId = storeInfo.getKakaoId();
        storeId = storeInfo.getStoreId();
        aver_star = storeInfo.getAver_star();
        postingNum = storeInfo.getPostingNum();
        address = storeInfo.getAddress();
        allItemsInSection = storeInfo.getAllItemsInSection();
        lat = storeInfo.getGeoPoint().getLatitude();
        lon =storeInfo.getGeoPoint().getLongitude();
    }

    public String getName() {
        return name;
    }

    public String getKakaoId() {
        return kakaoId;
    }

    public String getStoreId() {
        return storeId;
    }

    public double getAver_star() {
        return aver_star;
    }

    public int getPostingNum() {
        return postingNum;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<PostingInfo> getAllItemsInSection() {
        return allItemsInSection;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKakaoId(String kakaoId) {
        this.kakaoId = kakaoId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setAver_star(double aver_star) {
        this.aver_star = aver_star;
    }

    public void setPostingNum(int postingNum) {
        this.postingNum = postingNum;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAllItemsInSection(ArrayList<PostingInfo> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
