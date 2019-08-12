package com.rococodish.front_ui.DataModel;

import java.io.Serializable;

public class FragmentTagData implements Serializable {

    private String itemTagName;
    private int itemTagPosts;

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
