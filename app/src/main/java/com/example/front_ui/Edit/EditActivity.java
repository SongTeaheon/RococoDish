package com.example.front_ui.Edit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.SerializableStoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.RatingBarUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private final String TAG = "TAGEditActivity";

    ImageView imageView;
    EditText description;
    TextView starText;
    TextView addressText;
    ImageView backButton;
    RatingBar mRatingBar;
    TextView storeName;

    PostingInfo postingInfo;
    SerializableStoreInfo storeInfo;
    double distance;
    float oldAverStar;

    FirebaseFirestore db;
    FirebaseStorage storage;
    Context mContext;
    HashTagHelper editHashTagHelper;
    Map<String, Boolean> oldTagMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_last_share);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mContext = this;

        //데이터를 가져온다
        Intent intent = this.getIntent();

        postingInfo = (PostingInfo) intent.getSerializableExtra("postingInfo");
        Log.d(TAG, "postingID : " + postingInfo.getPostingId());
        Log.d(TAG, "posting Info description " + postingInfo.getHashTags() + "storage path " + postingInfo.imagePathInStorage
                + " storeId : " + postingInfo.getStoreId() + " postingid : " + postingInfo.postingId);
        storeInfo = (SerializableStoreInfo) intent.getSerializableExtra("storeInfo");
        Log.d(TAG, "store Info id : " + storeInfo.getStoreId() + " store name : " + storeInfo.getName() + " store map :" + storeInfo.getLat() + ", " + storeInfo.getLon() +
                " star :  " + storeInfo.getAver_star());
        oldAverStar = postingInfo.getAver_star();
        oldTagMap = postingInfo.getTag();

        //데이터 세팅.
        imageView = findViewById(R.id.imageView1);
        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        description = findViewById(R.id.hashTag);
        description.setText(postingInfo.getHashTags());
        editHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.MainColor), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
            }
        });
        editHashTagHelper.handle(description);

        //tag데이터 업데이트
        //1. firebase 업데이트.
        //2. 알골리아 업데이트.


        addressText = findViewById(R.id.textViewAddress);
        addressText.setText(postingInfo.getAddress());

        storeName = findViewById(R.id.mainText);
        storeName.setText(storeInfo.getName());

        starText = findViewById(R.id.starText);
        starText.setText(Float.toString(postingInfo.getAver_star()));

        mRatingBar = findViewById(R.id.ratingBar);
        RatingBarUtils.setupStarRatingBar(mRatingBar, starText);

        //backButton
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //share 버튼
        ImageView completeButton = findViewById(R.id.imageViewComplete);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : share!!!!!");
                //태그

                //tag 바꾸기
                List<String> allHashTag = editHashTagHelper.getAllHashTags();
                Map<String, Boolean> tagMap = new HashMap<>();
                for(String i : allHashTag){
                    tagMap.put(i, true);
                }
                postingInfo.tag = tagMap;

                //다끝나면 액티비티 종료
                sendPostingDataToLocalBrdcast();  //local broad cast을 통해 수정사항을 전달한다.
                changeDataOnFirebase();    //firebase에 변경 사항 변경
                changeTagOnAlgolia();
                //평점 변경 및 데이터 뿌리기.
                changeStarAndUpdateStoreData(Float.parseFloat(starText.getText().toString()), storeInfo.getStoreId());
                EditActivity.this.finish();
            }
        });
    }

    //firebase에 변경 사항 변경
    void changeDataOnFirebase(){
        postingInfo.setHashTags(description.getText().toString());
        postingInfo.setAver_star(Float.parseFloat(starText.getText().toString()));

        db.collection("포스팅").document(postingInfo.getPostingId())
                .set(postingInfo);
        db.collection("가게").document(storeInfo.getStoreId())
                .collection("포스팅채널").document(postingInfo.getPostingId())
                .set(postingInfo);
    }

    void changeTagOnAlgolia(){
        //1. old태그 삭제
        //oldTag를 돌면서 oldTag중에 newTAG에 없는 걸 찾아서 지운다.
        for(final String oldTag : oldTagMap.keySet()){
            boolean isExist = false;
            for(final String newTag : postingInfo.getTag().keySet()){
                if(oldTag.equals(newTag)) {
                    isExist = true;break;
                }
            }
            if(!isExist) {
                Log.d(TAG, "delete tag : " + oldTag);
                AlgoliaUtils.deleteTagInAlgolia(oldTag, postingInfo.getPostingId());
            }
        }

        //newTag를 돌면서 newTag중에 oldTag에 없는 건 추가한다.
        for(final String newTag : postingInfo.getTag().keySet()){
            boolean isExist = false;
            for(final String oldTag : oldTagMap.keySet()){
                if(newTag.equals(oldTag)) {
                    isExist = true;break;
                }
            }
            if(!isExist) {
                Log.d(TAG, "add tag : " + newTag);
                AlgoliaUtils.addTagData(newTag, postingInfo.getPostingId());
            }
        }
    }
    //local broad cast을 통해 수정사항을 전달한다.
    void sendPostingDataToLocalBrdcast(){
        Log.d(TAG, "sendPostingDataToLocalBrdcast : " + postingInfo.getPostingId());
        Intent intent = new Intent(postingInfo.getPostingId());//intent filter는 postingId!!!
        intent.putExtra("hashTags", description.getText().toString());
        intent.putExtra("aver_star", Float.parseFloat(starText.getText().toString()));
        intent.putExtra("tag", (HashMap)postingInfo.getTag());


        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        //받는 곳.
        //1. subactivity에서 dishview가 켜졌으면, SectionListDataAdapter와 dishView
        //2. MyPage에서 dishView가 켜졌으면, SectionListDataAdapter와 myPage와 dishView
        //3. 가게페이지에서 켜졌으면, SectionListDataAdapter와 storePage와 dishView

    }

    //postingInfo의 별점을 storeInfo에 넣어준다.
    //평점 바꾸어주는 코드 필요. 데이터 write까지
    //FIXME: 리팩토링 lastShareFragment와 중복된 코드!!!
    private void changeStarAndUpdateStoreData(final float newStar, final String storeId) {
        Log.d(TAG, "changeStarAndUpdateStoreData.");
        //트랜잭션으로 원자적으로 사용. 데이터 가져와서 세팅!
        final DocumentReference storeRef = db.collection("가게").document(storeId);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object> storeInfo = transaction.get(storeRef).getData();


                long postingNum = (long)storeInfo.get("postingNum");
                double aver_star = (double)storeInfo.get("aver_star");

                // Compute new average rating
                double oldRatingTotal = aver_star * postingNum;
                double newAverStar = (oldRatingTotal - oldAverStar + newStar) / postingNum;

                // Set new restaurant info
                storeInfo.put("aver_star", newAverStar);

                // Update restaurant
                transaction.set(storeRef, storeInfo);
//                sendStoreDataToLocalBrdcast(newAverStar);
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

}
