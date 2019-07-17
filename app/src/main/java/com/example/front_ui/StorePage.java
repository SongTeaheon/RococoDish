package com.example.front_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.StorePostInfo;
import com.example.front_ui.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StorePage extends AppCompatActivity {

    private final String TAG = "TAGStorePage";
    String storeName;
    String storeStar;
    String docId;

    TextView tv_storeName;
    TextView tv_storeStar;
    GridView gridView;
    List<StorePostInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_page);

        Intent intent = getIntent();
        storeName = intent.getStringExtra("storeName");
        storeStar = intent.getStringExtra("averStar");
        docId = intent.getStringExtra("documentId");

        Log.d(TAG, "name, star, id :" + storeName + storeStar + docId);
        tv_storeName = findViewById(R.id.tvStore);

        tv_storeName.setText(storeName);

        //그리드 뷰 세팅
        gridView = findViewById(R.id.gridView0);
        final StorePageAdapter storePageAdapter = new StorePageAdapter(this, list);
        gridView.setAdapter(storePageAdapter);

        //가게 해당 게시물 가져오기
        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .whereEqualTo("storeId", docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                            //TODO : postingInfo와 StoreInfo 둘다 가져와야 클릭해서 DishView들어갈 수 있음.
                            String image = dc.getData().get("imagePathInStorage").toString();
                            String numLike =  dc.getData().get("numLike").toString();
                            String numStar = dc.getData().get("aver_star").toString();

                            list.add(new StorePostInfo(image, numLike, numStar));
                            storePageAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }
}
