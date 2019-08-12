package com.rococodish.front_ui

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object Firestore {
    private val TAG = "TAG_Firestore_Util"
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
                "사용자/${FirebaseAuth.getInstance().uid
                        ?: throw java.lang.NullPointerException("에러발생")}"
        )

    //마이페이지에서 이미지 업로드하는 메서드
    fun profileImageToFirestore(imageUri : Uri){
        currentUserDocRef.update("profileImage", imageUri.toString()).addOnSuccessListener {
            Log.d(TAG, "사용자의 프로필 사진 변경이 디비에 적용되었습니다.")
        }
    }
}