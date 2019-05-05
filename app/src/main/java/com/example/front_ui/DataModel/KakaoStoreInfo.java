package com.example.front_ui.DataModel;

import android.os.Parcel;
import android.os.Parcelable;


//StoreSearch Fragment에서 LastShareFragment로 데이터를 전달하기 위해 parcelable구현
public class KakaoStoreInfo implements Parcelable {

    public String id;
    public String place_name;
    public String category_name;
    public String category_group_name;
    public String category_group_code;
    public String phone;
    public String road_address_name;
    public String address_name;
    public String x;
    public String y;
    public String place_url;

    public KakaoStoreInfo(String id, String place_name, String category_name, String category_group_name, String category_group_code, String phone, String road_address_name, String address_name, String x, String mapx1, String place_url) {
        this.id = id;
        this.place_name = place_name;
        this.category_name = category_name;
        this.category_group_name = category_group_name;
        this.category_group_code = category_group_code;
        this.phone = phone;
        this.road_address_name = road_address_name;
        this.address_name = address_name;
        this.x = x;
        this.y = y;
        this.place_url = place_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(place_name);
        dest.writeString(category_name);
        dest.writeString(category_group_name);
        dest.writeString(category_group_code);
        dest.writeString(phone);
        dest.writeString(road_address_name);
        dest.writeString(address_name);
        dest.writeString(x);
        dest.writeString(y);
        dest.writeString(place_url);

    }

    private void readFromParcel(Parcel in){
        id = in.readString();
        place_name = in.readString();
        category_name = in.readString();
        category_group_name = in.readString();
        category_group_code = in.readString();
        phone = in.readString();
        road_address_name = in.readString();
        address_name = in.readString();
        x = in.readString();
        y = in.readString();
        place_url = in.readString();

    }

    public KakaoStoreInfo(Parcel in) {
        readFromParcel(in);
    }
    public static final Creator CREATOR = new Creator() {
        public KakaoStoreInfo createFromParcel(Parcel in) {
            return new KakaoStoreInfo(in);
        }

        public KakaoStoreInfo[] newArray(int size) {
            return new KakaoStoreInfo[size];
        }
    };


    /*
    public String title;
    public String description;
    public String telephone;
    public String address;
    public String roadAddress;
    public int x;
    public int y;
    public String category;
    public String link;

    public KakaoStoreInfo(String title, String description, String telephone, String address, String roadAddress, int x, int y, String category, String link) {
        this.title = title;
        this.description = description;
        this.telephone = telephone;
        this.address = address;
        this.roadAddress = roadAddress;
        this.x = x;
        this.y = y;
        this.category = category;
        this.link = link;
    }

    public KakaoStoreInfo(String title, String address, String category) {
        this.title = title;
        this.address = address;
        this.category = category;
    }

    public KakaoStoreInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(telephone);
        dest.writeString(address);
        dest.writeString(roadAddress);
        dest.writeString(category);
        dest.writeString(link);
        dest.writeInt(x);
        dest.writeInt(y);

    }

    private void readFromParcel(Parcel in){
        title = in.readString();
        description = in.readString();
        telephone = in.readString();
        address = in.readString();
        address = in.readString();
        roadAddress = in.readString();
        category = in.readString();
        link = in.readString();
        x = in.readInt();
        y = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public KakaoStoreInfo createFromParcel(Parcel in) {
            return new KakaoStoreInfo(in);
        }

        public KakaoStoreInfo[] newArray(int size) {
            return new KakaoStoreInfo[size];
        }
    };
    */
}