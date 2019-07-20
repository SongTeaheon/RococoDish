package com.example.front_ui.DataModel;

import java.io.Serializable;

public class FragmentPeopleData implements Serializable {

    private int imageViewPeople; //일단 빠르게 작성하려고 int로 해놓았으니 String으로 바꿔주세요 ㅜㅜ
    private String itemPeopleName;
    private int itemPeoplePosts;

    public int getImageViewPeople() {
        return imageViewPeople;
    }

    public void setImageViewPeople(int imageViewPeople) {
        this.imageViewPeople = imageViewPeople;
    }

    public String getItemPeopleName() {
        return itemPeopleName;
    }

    public void setItemPeopleName(String itemPeopleName) {
        this.itemPeopleName = itemPeopleName;
    }

    public int getItemPeoplePosts() {
        return itemPeoplePosts;
    }

    public void setItemPeoplePosts(int itemPeoplePosts) {
        this.itemPeoplePosts = itemPeoplePosts;
    }
}
