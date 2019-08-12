package com.rococodish.front_ui.Interface;

import com.google.firebase.firestore.DocumentSnapshot;

public interface FirebasePredicate {
    void afterGetData(DocumentSnapshot document);//데이터를 하나 가져온 경우 가져온 데이터를 처리한다.
}
