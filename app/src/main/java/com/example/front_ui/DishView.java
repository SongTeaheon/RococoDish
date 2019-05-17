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
import com.example.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    FloatingActionButton delete;
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
        final Bundle bundle = intent.getExtras();

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
        //우측 상단의 삭제 버튼을 누를 경우 디비를 삭제시킴(포스팅과 포스팅채널 둘다)
        delete = findViewById(R.id.deletePosting_floatingBtn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String postingUuid = bundle.getString("docId");
                final String storeId = bundle.getString("storeId");
                Log.d(TAG, "받은 도큐먼트 UUID : "+postingUuid);
                Log.d(TAG, "받은 가게 UUID : "+storeId);

                final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                //포스팅 컬렉션에서 삭제
                firestore.collection("포스팅")
                        .document(postingUuid)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                final ProgressDialog progressDialog = ProgressDialog.show(
                                        DishView.this,
                                        "Loading",
                                        "Please wait...");
                                if(task.isSuccessful()){
                                    Log.d(TAG, "해당 포스팅 삭제 완료");
//                                    finish();

                                    firestore.collection("가게")
                                            .document(storeId)
                                            .collection("포스팅채널")
                                            .document(postingUuid)
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "해당 포스팅채널 삭제 완료");
                                            firestore.collection("가게")
                                                    .document(storeId)
                                                    .collection("포스팅채널").get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                int count = 0;
                                                                for(DocumentSnapshot dc : task.getResult()){
                                                                    count++;
                                                                }
                                                                if(count == 0){
                                                                    firestore.collection("가게")
                                                                            .document(storeId).delete();
                                                                }
                                                            }
                                                        }
                                                    });
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "해당 포스팅채널 삭제 실패 : "+e.getMessage());
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "해당 포스팅 삭제 실패"+ e.getMessage());

                            }
                        });
            }
        });
    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
}

