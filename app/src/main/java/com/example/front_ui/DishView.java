package com.example.front_ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DishView extends AppCompatActivity {

    private final String TAG = "TAGDishView";
    Button buttonToDetail;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        buttonToDetail = (Button) findViewById(R.id.toDetail1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PostingInfo postingInfo = (PostingInfo)bundle.getSerializable("postingInfo");
        Log.d(TAG, "posting Info description " + postingInfo.description +"storage path " + postingInfo.imagePathInStorage);

        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        buttonToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDetail();
            }
        });
    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
}

