package com.example.front_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.front_ui.R;

public class StorePage extends AppCompatActivity {

    private final String TAG = "TAGStorePage";
    String storeName;
    String storeStar;
    String docId;

    TextView tv_storeName;
    TextView tv_storeStar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_page);

        Intent intent = getIntent();
        storeName = intent.getStringExtra("storeName");
        storeStar = intent.getStringExtra("averStar");
        docId = intent.getStringExtra("documentId");

        Log.d(TAG, "name, star, id :" + storeName + storeStar + docId);
        tv_storeName = findViewById(R.id.storeName);
        tv_storeStar = findViewById(R.id.storeStar);

        tv_storeName.setText(storeName);
        tv_storeName.setText(storeStar);

        //가게 데이터 넣는 코드 필요!!


    }
}
