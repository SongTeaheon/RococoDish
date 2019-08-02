package com.example.front_ui.DataModel;

public class UserInfo {
    public String userId;
    public String eMail;
    public String nickname;
    public String profileImage;
    public String follower;
    public String following;
    public int postingNum;
    //망할 깃헙 테스트


    public UserInfo(String userId,
                    String eMail,
                    String nickName,
                    String profileImage,
                    String follower,
                    String following,
                    int postingNum) {
        this.userId = userId;
        this.eMail = eMail;
        this.nickname = nickName;
        this.profileImage = profileImage;
        this.follower = follower;
        this.following = following;
        this.postingNum = postingNum;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPostingNum() {
        return postingNum;
    }

    public UserInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}