package com.example.front_ui.DataModel;

import java.io.Serializable;

public class FragmentRegionData implements Serializable {

    private String itemRegionName;
    private String itemRegionAddress;

    public String getItemRegionName() {
        return itemRegionName;
    }

    public void setItemRegionName(String itemRegionName) {
        this.itemRegionName = itemRegionName;
    }

    public String getItemRegionAddress() {
        return itemRegionAddress;
    }

    public void setItemRegionAddress(String itemRegionAddress) {
        this.itemRegionAddress = itemRegionAddress;
    }
}
