package com.rococodish.front_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class FollowActivity extends AppCompatActivity {

    private String TAG = "TAGFollowActivity";

    TextView userName;
    ViewPager followViewPager;
    TabLayout followTabLayout;
    ImageView backBtn;
    FollowViewPagerAdapter followViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Intent intent = getIntent();
        String userUUID = intent.getStringExtra("userUUID");
        int pageNum = intent.getIntExtra("pageNum", 0);

        followViewPager = findViewById(R.id.follow_viewpager_followActivity);
        followTabLayout = findViewById(R.id.follow_tablayout_followActivity);

        initViewPager(userUUID, pageNum);
        initTabLayout();


        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userName = findViewById(R.id.userName);
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists() && documentSnapshot != null){

                            userName.setText(documentSnapshot.get("nickname").toString());
                        }
                    }
                });


    }
    public void initViewPager(String userUUID, int pageNum){
        followViewPagerAdapter = new FollowViewPagerAdapter(getSupportFragmentManager(), userUUID);
        followViewPager.setAdapter(followViewPagerAdapter);
        followViewPager.setCurrentItem(pageNum);
    }
    public void initTabLayout(){
        followTabLayout.setupWithViewPager(followViewPager);
    }
}
