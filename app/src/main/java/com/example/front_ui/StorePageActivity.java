package com.example.front_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.StorePostInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class StorePageActivity extends AppCompatActivity {

    private final String TAG = "TAGStorePage";
    String storeName;
    String storeStar;
    String docId;

    TextView tv_storeName;
    TextView tv_storeStar;
    ImageView backBtn;
    GridView gridView;
    ProgressBar progressBar;
    List<StorePostInfo> list = new ArrayList<>();
    StoreInfo storeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_page);

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        storeName = intent.getStringExtra("storeName");
        storeStar = intent.getStringExtra("averStar");
        docId = intent.getStringExtra("documentId");

        Log.d(TAG, "name, star, id :" + storeName + storeStar + docId);
        tv_storeName = findViewById(R.id.tvStore);

        tv_storeName.setText(storeName);

        //그리드 뷰 세팅
        progressBar = findViewById(R.id.progressbar_storeActivity);
        gridView = findViewById(R.id.gridView0);
        final StorePageAdapter storePageAdapter = new StorePageAdapter(this, list);
        gridView.setAdapter(storePageAdapter);

        gridView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        //가게 해당 게시물 가져오기
        //TODO : 시간순으로 정렬하는거 해결하기

        //가게 도큐먼트로 가서 객체로 가져와야함.
        FirebaseFirestore.getInstance()
                .collection("가게")
                .document(docId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null)
                            Log.d(TAG, e.getMessage());
                        assert documentSnapshot != null && documentSnapshot.exists();
                        storeInfo = documentSnapshot.toObject(StoreInfo.class);
                    }
                });

        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .whereEqualTo("storeId", docId)
//                .orderBy("postingTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                            for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                                if(dc.getType() == DocumentChange.Type.ADDED){
                                    //게시물의 이미지, 별점, 좋아요 개수가져옴.
                                    final String image = dc.getDocument().getData().get("imagePathInStorage").toString();
                                    final String numLike =  dc.getDocument().getData().get("numLike").toString();
                                    final String numStar = dc.getDocument().getData().get("aver_star").toString();

                                    final PostingInfo postingInfo = dc.getDocument().toObject(PostingInfo.class);

                                    list.add(new StorePostInfo(image, numLike, numStar, postingInfo, storeInfo));
                                }
                            }
                            storePageAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
//                            for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){
//
//                                //게시물의 이미지, 별점, 좋아요 개수가져옴.
//                                final String image = dc.getData().get("imagePathInStorage").toString();
//                                final String numLike =  dc.getData().get("numLike").toString();
//                                final String numStar = dc.getData().get("aver_star").toString();
//
//                                final PostingInfo postingInfo = dc.toObject(PostingInfo.class);
//
//                                list.add(new StorePostInfo(image, numLike, numStar, postingInfo, storeInfo));
//
//                                storePageAdapter.notifyDataSetChanged();
//                                progressBar.setVisibility(View.GONE);
//                                gridView.setVisibility(View.VISIBLE);
//
//                            }


                        }
                        else{
                            Log.d(TAG, "해당 가게에 게시물이 없습니다.");
                        }
                    }
                });

    }
}
