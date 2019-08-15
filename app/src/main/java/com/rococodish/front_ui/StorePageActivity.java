package com.rococodish.front_ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.DataModel.StorePostInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
        docId = intent.getStringExtra("documentId");

        Log.d(TAG, "name, star, id :" + storeName + storeStar + docId);
        tv_storeName = findViewById(R.id.tv_storeName);

        tv_storeName.setText(storeName);

        //그리드 뷰 세팅
        progressBar = findViewById(R.id.progressbar_storeActivity);
        gridView = findViewById(R.id.gridView0);
        final StorePageAdapter storePageAdapter = new StorePageAdapter(this, list);
        gridView.setAdapter(storePageAdapter);

        gridView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);



        //가게 해당 게시물 가져오기
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
                .collection("가게")
                .document(docId)
                .collection("포스팅채널")
                .orderBy("postingTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.getDocuments().isEmpty()){

                            list.clear();

                            for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                final String image = dc.getData().get("imagePathInStorage").toString();
                                final String numLike =  dc.getData().get("numLike").toString();
                                final String numStar = dc.getData().get("aver_star").toString();

                                final PostingInfo postingInfo = dc.toObject(PostingInfo.class);

                                list.add(new StorePostInfo(image, numLike, numStar, postingInfo, storeInfo));

                            }
                            storePageAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                        }
                        else{
                            Log.d(TAG, "해당 가게에 게시물이 없습니다.");
                        }
                    }
                });

    }
}
