package com.example.front_ui.DataModel;

import java.util.List;
import java.util.Map;

public class PostingInfo {
    public String storeName;
    public String imagePathInStorage;
    public int numLike;
    public Object postingTime;
    public String title;
    public Map<String, Boolean> tag;
    public String description;
    public String writerName;
    public String address;
    public String storeId;
    public String writerId;
    public float aver_star;
    public List<Float> detail_aver_star; //{맛, 가성비, 서비스, 분위기}

    public PostingInfo() {
    }

    public PostingInfo(String storeName, String imagePathInStorage, int numLike, Object postingTime, String title, Map<String, Boolean> tag, String description, String writerName, String address, String storeId, String writerId, float aver_star, List<Float> detail_aver_star) {
        this.storeName = storeName;
        this.imagePathInStorage = imagePathInStorage;
        this.numLike = numLike;
        this.postingTime = postingTime;
        this.title = title;
        this.tag = tag;
        this.description = description;
        this.writerName = writerName;
        this.address = address;
        this.storeId = storeId;
        this.writerId = writerId;
        this.aver_star = aver_star;
        this.detail_aver_star = detail_aver_star;
    }


    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getImagePathInStorage() {
        return imagePathInStorage;
    }

    public void setImagePathInStorage(String imagePathInStorage) {
        this.imagePathInStorage = imagePathInStorage;
    }

    public int getNumLike() {
        return numLike;
    }

    public void setNumLike(int numLike) {
        this.numLike = numLike;
    }

    public Object getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(Object postingTime) {
        this.postingTime = postingTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getTag() {
        return tag;
    }

    public void setTag(Map<String, Boolean> tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public float getAver_star() {
        return aver_star;
    }

    public void setAver_star(float aver_star) {
        this.aver_star = aver_star;
    }

    public List<Float> getDetail_aver_star() {
        return detail_aver_star;
    }

    public void setDetail_aver_star(List<Float> detail_aver_star) {
        this.detail_aver_star = detail_aver_star;
    }
}
