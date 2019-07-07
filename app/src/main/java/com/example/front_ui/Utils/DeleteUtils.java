package com.example.front_ui.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.algolia.search.saas.Index;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.Interface.AlgoliaSearchPredicate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class DeleteUtils {

    public static String TAG = "TAGDishViewUtilsForFirebase";
    static boolean isDeleteDone = false;

    //포스팅 삭제 과정!!!
    public static void deletePosting(Context context,
                                     final FirebaseFirestore db,
                                     FirebaseStorage storage,
                                     final String storeDocId,
                                     String postingDocId,
                                     final String imagePath,
                                     final double postingAverStar){

        /*
        * 함수 개요
        * 1. posting collection에서 삭제
        * 2. storage에서 사진 삭제
        * 3. 가게 컬렉션 내부 포스팅 삭제 -> 가게데이터삭제(포스팅게시글이 없는경우), 평균 별점 업데이트
        * 4. posting collection의 서브 컬렉션 삭제.
        * */

        Log.d(TAG, "firebase delete posting");

        //posting colletion에서 삭제
        db.collection("포스팅").document(postingDocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot in posting collection successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document in posting collection", e);
                    }
                });

        //TODO: 포스팅 경로아래에 좋아요와 댓글 서브컬렉션 삭제도 필요

        //storage에서 삭제.
        StorageReference deleteRef = storage.getReferenceFromUrl(imagePath);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "파일 삭제 성공. imagePath : " + imagePath);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "파일 삭제 실패 " + exception.toString());
            }
        });


        //가게 컬렉션 내부 포스팅 삭제
        db.collection("가게")
                .document(storeDocId)
                .collection("포스팅채널")
                .document(postingDocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkStoreDataDelete(db, storeDocId);//store도 같이 지워야하는지 확인!
                        changeStarAndDeleteStoreData(db, postingAverStar, storeDocId );//평점 바꾼다.
                        Log.d(TAG, "Posting Data DocumentSnapshot in store collection successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting posting data document in store collection", e);
                    }
                });

        //사용자 posting Num 변경.
        subtractUserPostingNum(db);
        ((Activity)context).finish();

    }

    //해당 가게에 posting 데이터가 한 개뿐이었다가 지워지는 경우, store데이터도 같이 지워준다.(deleteStoreData()
    private static void checkStoreDataDelete(final FirebaseFirestore db, final String storeDocId){
        //가게 컬렉션 데이터 개수 확인 1개면 store data 삭제
        db.collection("가게")
                .document(storeDocId)
                .collection("포스팅채널")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int size = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                size++;
                                if(size >= 1 )
                                    break;
                            }
                            Log.d(TAG, "컬렉션 데이터 사이즈 : " + size);

                            if(size <= 0 ) {
                                //가게 데이터 삭제
                                deleteStoreData(db, storeDocId);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //가게 데이터 삭제
    private static void deleteStoreData(FirebaseFirestore db, final String storeDocId){
        Log.d(TAG, "가게 데이터 삭제");
        db.collection("가게")
                .document(storeDocId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Store Data DocumentSnapshot in store collection successfully deleted!");
                        //삭제하면서 algolia에서 id로 검색해서 지워준다.
                        deleteStoreInAlgolia(storeDocId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting store data document in store collection", e);
                    }
                });
    }

    //algolia에서 가게 데이터를 지운다.
    private static void deleteStoreInAlgolia(String storeFirebaseId){

        final Index index = AlgoliaUtils.client.getIndex("store");

        //1. 먼저 가게 데이터를 찾는다.
        AlgoliaUtils.searchStore("store", "storeId", storeFirebaseId, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                for(int k = 0; k < jsonArray.length(); k++){
                    String jsonStr = null;
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        String idStr = jsonObject.getString("objectID");
                        Log.d(TAG, "algolia 삭제! objectId" + idStr );
                        index.deleteObjectAsync(idStr, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Gson gson = new Gson();
                    StoreInfo storeInfo = gson.fromJson(jsonStr, StoreInfo.class);

                }
            }
        });
    }


    //postingInfo의 별점을 storeInfo에 넣어준다.
    //평점 바꾸어주는 코드 필요. 데이터 write까지
    private static void changeStarAndDeleteStoreData(FirebaseFirestore db, final double postingAverStar, final String storeId) {
        Log.d(TAG, "changeStarAndUpdateStoreData.");
        //트랜잭션으로 원자적으로 사용. 데이터 가져와서 세팅!
        final DocumentReference storeRef = db.collection("가게").document(storeId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object> storeInfo = transaction.get(storeRef).getData();

                long postingNum = (long)storeInfo.get("postingNum");
                double aver_star = (double)storeInfo.get("aver_star");

                // Compute new number of ratings
                long newNumRatings = postingNum - 1;

                // Compute new average rating
                double oldRatingTotal = aver_star * postingNum;
                double newAvgRating = (oldRatingTotal - postingAverStar) / newNumRatings;

                // Set new restaurant info
                storeInfo.put("postingNum", newNumRatings);
                storeInfo.put("aver_star", newAvgRating);

                // Update restaurant
                transaction.set(storeRef, storeInfo);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    //postingNum--
    private static void subtractUserPostingNum(final FirebaseFirestore db){

        final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "updateUserPosting num. user uid : " + userUid);
        db.collection("사용자")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long postingNum = (long)documentSnapshot.get("postingNum") - 1;
                        Log.d(TAG, "updateUserPostingNum. new num : " + postingNum);

                        Map<String, Long> data = new HashMap<>();
                        data.put("postingNum", postingNum);

                        db.collection("사용자").document(userUid)
                                .set(data, SetOptions.merge());
                    }
                });




    }
}
