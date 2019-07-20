package com.example.front_ui.DataModel;

import java.io.Serializable;

public class FragmentTagData implements Serializable {

    private int imageViewTag; //일단 빠르게 작성하려고 int로 해놓았으니 String으로 바꿔주세요 ㅜㅜ
    private String itemTagName;
    private int itemTagPosts;

    public int getImageViewTag() {
        return imageViewTag;
    }

    public void setImageViewTag(int imageViewTag) {
        this.imageViewTag = imageViewTag;
    }

    public String getItemTagName() {
        return itemTagName;
    }

    public void setItemTagName(String itemTagName) {
        this.itemTagName = itemTagName;
    }

    public int getItemTagPosts() {
        return itemTagPosts;
    }

    public void setItemTagPosts(int itemTagPosts) {
        this.itemTagPosts = itemTagPosts;
    }
}
