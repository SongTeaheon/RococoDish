package com.example.front_ui.DataModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoreInfo{
    public String name;
    public String kakaoId;
    public String storeId;
    public double aver_star;
    public int postingNum;
    public String address;
    public List<Double> detail_aver_star;
    private ArrayList<PostingInfo> allItemsInSection;
    public GeoPoint geoPoint;


    public StoreInfo() {

    }

    public StoreInfo(String name,
                     float aver_star,
                     String address,
                     List<Double> detail_aver_star,
                     GeoPoint geoPoint
                     ) {
        this.name = name;
        this.aver_star = aver_star;
        this.address = address;
        this.detail_aver_star = detail_aver_star;
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

    public List<Double> getDetail_aver_star() {
        return detail_aver_star;
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

    public void setDetail_aver_star(List<Double> detail_aver_star) {
        this.detail_aver_star = detail_aver_star;
    }
}