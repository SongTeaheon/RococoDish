package com.example.front_ui.Edit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.SerializableStoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.RatingBarUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
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

    FirebaseFirestore db;
    FirebaseStorage storage;
    Context mContext;

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
        distance = (double) intent.getDoubleExtra("distance", 0.0);
        Log.d(TAG, "거리(미터단위) : " + distance);

        //데이터 세팅.
        imageView = findViewById(R.id.imageView1);
        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        description = findViewById(R.id.hashTag);
        description.setText(postingInfo.getHashTags());


        addressText = findViewById(R.id.textViewAddress);
        addressText.setText(postingInfo.getAddress());

        storeName = findViewById(R.id.tvStore);
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
                //다끝나면 액티비티 종료
                //TODO::가게평점 변경 필요. 데이터가 다시 되돌아가야함..........
                changeData();
                EditActivity.this.finish();
            }
        });
    }

    void changeData(){
        Map<String, Object> data = new HashMap<>();
        data.put("hashTags", description.getText().toString());
        data.put("aver_star", Float.parseFloat(starText.getText().toString()));

        db.collection("포스팅").document(postingInfo.getPostingId())
                .set(data, SetOptions.merge());
        db.collection("가게").document(storeInfo.getStoreId())
                .collection("포스팅채널").document(postingInfo.getPostingId())
                .set(data, SetOptions.merge());

    }
}
