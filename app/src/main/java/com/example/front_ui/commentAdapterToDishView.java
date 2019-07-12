package com.example.front_ui;

public interface commentAdapterToDishView {
    //1. 메서드 생성(보내고자 하는 데이터를 파라미터로)
    //댓글 어댑터에서  DishView로 댓글 도큐먼트 아이디를 보내는 메서드
    void sendGetCommentDocId(String docId);
}
