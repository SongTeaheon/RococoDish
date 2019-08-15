package com.rococodish.front_ui.DataModel;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class PostingInfo implements Serializable {
    public String storeName;
    public String imagePathInStorage;
    public int numLike = 0;
    public Object postingTime;
    public Object editTime;
    public String title;
    public Map<String, Boolean> tag;
    public String description;
    public String writerName;
    public String address;
    public String storeId;
    public String writerId;
    public String postingId;
    public float aver_star;
    public String hashTags;

    private int viewId; //recyclervieadapter에서만 쓰이는 데이터입니다. 신경안쓰셔도 됩니당


    public PostingInfo() {
    }

    public PostingInfo(String storeName,
                       String imagePathInStorage,
                       int numLike,
                       Object postingTime,
                       String title,
                       Map<String, Boolean> tag,
                       String description,
                       String writerName,
                       String address,
                       String storeId,
                       String writerId,
                       float aver_star,
                       String hashTags) {
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
        this.storeId = storeId;
        this.hashTags = hashTags;
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

//    public Date getDate(){
//       return (Date)postingTime;
//    }

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

    public String getPostingId() {
        return postingId;
    }

    public void setPostingId(String postingId) {
        this.postingId = postingId;
    }
    public void setHashTags(String hashTags){
        this.hashTags = hashTags;
    }

    public String getHashTags() {
        return hashTags;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}
