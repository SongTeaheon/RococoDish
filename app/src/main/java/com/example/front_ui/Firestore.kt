package com.example.front_ui

import android.net.Uri
import android.util.Log
import com.example.front_ui.DataModel.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object Firestore {
    private val TAG = "TAG_Firestore_Util"
    private val firebaseAuthInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
                "사용자/${FirebaseAuth.getInstance().uid
                        ?: throw java.lang.NullPointerException("에러발생")}"
        )

    //처음 로그인하는 유저에게 사용되는 메서드
    fun loginFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            //만약 db에 현재 회원가입한 유저가 없다면(새로운 유저라면)
            if (!it.exists()) {
                //DB에 넣어줄 유저의 정보를 만들어준다.(회원가입 당시 프로필 설정과 좋아요 개수는 없으니 NULL과 0으로 초기값 설정)
                val newUser = UserInfo(
                        firebaseAuthInstance.currentUser?.email.toString(),
                        firebaseAuthInstance.currentUser?.displayName.toString(),
                        null,
                        null,
                        null,
                        0
                )
                //이렇게 만든 정보를 파이어스토어에 넣어줌(여기서 파이어스토어는 위에서 언급한 DB와 같음)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    //이 경로로 새로운 유저 정보가 업로드에 성공했으면 onComplete()실행
                    onComplete()//반드시 괄호를 적어줘야합니다.(안적어주면 작동안함)
                }
            } else {//새로운 유저가 아니고 이미 가입된 유저의 경우는 바로 다음창으로 넘겨줌.
                onComplete()
            }
        }
    }
    //마이페이지에서 이미지 업로드하는 메서드
    fun profileImageToFirestore(imageUri : Uri){
        currentUserDocRef.update("profileImage", imageUri.toString()).addOnSuccessListener {
            Log.d(TAG, "사용자의 프로필 사진 변경이 디비에 적용되었습니다.")
        }
    }
    //내가 상대방을 팔로우할 경우
    fun newFollower(){

    }
}