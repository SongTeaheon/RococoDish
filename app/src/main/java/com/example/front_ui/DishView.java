package com.example.front_ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.DishViewUtils;
import com.example.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collection;

public class DishView extends AppCompatActivity {

    private final String TAG = "TAGDishView";
    Button buttonToDetail;
    Button deleteButton;
    ImageView imageView;
    FirebaseFirestore db;
    FirebaseStorage storage;
    PostingInfo postingInfo;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        buttonToDetail = (Button) findViewById(R.id.toDetail1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();

        postingInfo = (PostingInfo)bundle.getSerializable("postingInfo");
        Log.d(TAG, "posting Info description " + postingInfo.description +"storage path " + postingInfo.imagePathInStorage
        + " storeId : " + postingInfo.getStoreId() +" postingid : " + postingInfo.postingId);

        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        buttonToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDetail();
            }
        });
        //우측 상단의 삭제 버튼을 누를 경우 디비를 삭제시킴(포스팅과 포스팅채널 둘다)
        if(postingInfo.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Log.d(TAG, "내 게시물!!!");
            deleteButton = findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "delete button clicked");
                    String storeId = postingInfo.getStoreId();
                    String postingId = postingInfo.getPostingId();
                    String imagePath = postingInfo.getImagePathInStorage();
                    double postingAverStar = postingInfo.getAver_star();
                    DishViewUtils.deletePosting(db, storage, storeId, postingId, imagePath, postingAverStar);

                }
            });
        }

    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
}

