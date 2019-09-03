package com.rococodish.front_ui.Search;

import android.content.Intent;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rococodish.front_ui.R;
import com.rococodish.front_ui.RecyclerViewDataAdapter;

public class RegionSearchActivity extends AppCompatActivity {
    private final String TAG = "TAGRegionSearchActivity";
    RecyclerView main_recyclerview;
    FrameLayout loadingFrame;
    RecyclerViewDataAdapter recyclerViewDataAdapter;
    TextView searchWord;
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_search);

        //필요한 데이터를 받아온다.
        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        double lon = Double.parseDouble(intent.getStringExtra("longtitude"));
        double lat = Double.parseDouble(intent.getStringExtra("latitude"));

        //intent로 와야하는 데이터 . 검색 지명 이름, 좌표.
        Location mSearchLocation = new Location("dummyprovider");
        mSearchLocation.setLongitude(lon);
        mSearchLocation.setLatitude(lat);
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchWord = findViewById(R.id.tv_storeName);
        searchWord.setText(placeName);
        main_recyclerview = findViewById(R.id.mrecyclerView);
        initRecyclerView(mSearchLocation);

    }

    //주변 가게 recyclerviewt세팅!
    //TODO: REFACTORING 서브액티비티와 같은 함수! 합치면 좋을 듯
    public void initRecyclerView(Location locationCenter) {
        loadingFrame = findViewById(R.id.loadingFrame);
        loadingFrame.setVisibility(View.VISIBLE);

        Log.d(TAG, "initRecyclerView");
        main_recyclerview.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, locationCenter, loadingFrame, 1);
        recyclerViewDataAdapter.setHasStableIds(true); //dataSetChange할 때, blink하는 문제를 해결하기 위해!! getItemId 오버라이드 필요!!
        main_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        main_recyclerview.setAdapter(recyclerViewDataAdapter);
    }

}
