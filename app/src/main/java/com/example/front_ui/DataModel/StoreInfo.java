package com.example.front_ui.DataModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo{
    public String name;
    public String storeId;
    public float aver_star;
    public String address;
    public List<Float> detail_aver_star;
    private ArrayList<PostingInfo> allItemsInSection;
    public GeoPoint geoPoint;


    public StoreInfo() {

    }

    public StoreInfo(String name,
                     float aver_star,
                     String address,
                     List<Float> detail_aver_star,
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

    public float getAver_star() {
        return aver_star;
    }

    public String getAddress() {
        return address;
    }

    public List<Float> getDetail_aver_star() {
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
}