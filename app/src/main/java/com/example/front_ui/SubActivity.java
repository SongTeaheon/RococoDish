package com.example.front_ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.PostingInfo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 10002;
    private static final String TAG = "TAGSubActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    RecyclerView my_recycler_view;
    RecyclerView myPage_recyclerview;
    private Location mCurrentLocation;
    private boolean mLocationPermissionGranted = false;
    ImageView imageView;
    TextView myPageTextview;
    private ArrayList<PostingInfo> list;
    private int position;
    private TextView starText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate is called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        my_recycler_view = (RecyclerView) findViewById(R.id.mrecyclerView);
        my_recycler_view.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);

        //마이페이지용 리사이클러 뷰 변수
        myPage_recyclerview = findViewById(R.id.myPage_recyclerview_activitySub);
        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myPage_recyclerview.setAdapter(myPageAdapter);

        //일단 마이페이지 리사이클러뷰에서 0번째는 플러스 기능, 나머지는 게시물 구체적 보기 창으로 이동
//        if(position == 0){
//            myPage_recyclerview.findViewHolderForAdapterPosition(position).itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "이건 처음 아이템입니다.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        else{
//            myPage_recyclerview.findViewHolderForAdapterPosition(0).itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(), "나머지 아이템입니다.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        //마이페이지 글자 누를시 이벤트
        myPageTextview = findViewById(R.id.myPage_textview_activitySub);
        myPageTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MyPage.class);
                startActivity(intent);
            }
        });

        //별점평균 누를시 생기는 이벤트
        starText = findViewById(R.id.storeStar);
        starText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "포스팅 가즈아", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }else{
            getCurrentLocation();
            mLocationPermissionGranted = true;
        }
    }


    private void getCurrentLocation() {
        OnCompleteListener<Location> mCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    mCurrentLocation = task.getResult();
                    Toast.makeText(getApplicationContext(), "lat : " + mCurrentLocation.getLatitude() +
                            "\n lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                }else{
                    Log.e(TAG, "getCurrentLocation Exception"+ task.getException());
                }
            }
        };

        try {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(mCompleteListener);
        }catch(SecurityException e){
            e.printStackTrace();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();

                } else {

                    Toast.makeText(getApplicationContext(), "permission is denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }



}
