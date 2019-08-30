package com.rococodish.front_ui.Coupon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.R;

public class CouponExplainActivity extends AppCompatActivity {

    final String TAG = "TAGCouponExplainAct";
    TextView tv_storeName;
    TextView tv_couponExplain;
    ImageView backBtn;

    FirebaseFirestore db;
    String storeId;
    String storeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_explain);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        storeName = intent.getStringExtra("storeName");


        tv_storeName = findViewById(R.id.tv_storeName);
        tv_storeName.setText(storeName);

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tv_couponExplain = findViewById(R.id.tv_couponExplain);

        db.collection("가게").document(storeId).collection("쿠폰")
                .document("쿠폰")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getString("내용"));
                                tv_couponExplain.setText(document.getString("내용"));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
        });
    }
}
