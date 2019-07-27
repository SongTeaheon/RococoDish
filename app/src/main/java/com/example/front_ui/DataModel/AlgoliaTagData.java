package com.example.front_ui.DataModel;

public class AlgoliaTagData {
    public String text;
    public int postingNum;
    public String postingIds;

    public AlgoliaTagData(){}

    public AlgoliaTagData(AlgoliaTagData data){
        this.text = data.getText();
        this.postingNum = data.getPostingNum();
        this.postingIds = data.getPostingIds();
    }

    public AlgoliaTagData(String text, int postingNum, String postingIds){
        this.text = text;
        this.postingNum = postingNum;
        this.postingIds = postingIds;
    }

    public AlgoliaTagData(String text, int postingNum, String[] postingIdList){
        this.text = text;
        this.postingNum = postingNum;
        this.postingIds = makeStringFromList(postingIdList);
    }

    public String getText() {
        return text;
    }

    public int getPostingNum() {
        return postingNum;
    }

    public String getPostingIds() {
        return postingIds;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPostingNum(int postingNum) {
        this.postingNum = postingNum;
    }

    public void setPostingIds(String postingIds) {
        this.postingIds = postingIds;
    }

    public void setPostingIds(String[] postingIdList) {
        this.postingIds = makeStringFromList(postingIdList);
    }

    public String[] makePostingIdList(){
        String[] split_ids = this.postingIds.split(", ");
        return split_ids;
    }

    public String makeStringFromList(String[] postingIdList){
        if(postingIdList.length == 0) {
            postingIds = null;
            return null;
        }
        StringBuilder postingIds = new StringBuilder(postingIdList[0]);
        for(int i = 1; i < postingIdList.length; i++){
            postingIds.append(", ").append(postingIdList[i]);
        }
        return postingIds.toString();
    }



}
