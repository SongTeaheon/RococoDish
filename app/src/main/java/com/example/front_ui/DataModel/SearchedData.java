package com.example.front_ui.DataModel;


import java.io.Serializable;

/*
* MainSearchActivity에서 필요한 데이터!
* */
public class SearchedData implements Serializable {
    public String place_name;
    public String x;//longitude
    public String y;//lat
    public String address;

    public String getAddress() {
        return address;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }
}
