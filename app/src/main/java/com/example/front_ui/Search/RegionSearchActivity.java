package com.example.front_ui.Search;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.front_ui.R;
import com.example.front_ui.RecyclerViewDataAdapter;

public class RegionSearchActivity extends AppCompatActivity {
    private final String TAG = "TAGRegionSearchActivity";
    RecyclerView main_recyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_search);


        Location mSearchLocation = new Location("dummyprovider");
//        mSearchLocation.
//
//        Log.d(TAG, "initRecyclerView");
//        main_recyclerview = findViewById(R.id.mrecyclerView);
//        main_recyclerview.setHasFixedSize(true);
//        RecyclerViewDataAdapter recyclerViewDataAdapter;
//        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, locationCenter, loadingFrame);
//        recyclerViewDataAdapter.setHasStableIds(true); //dataSetChange할 때, blink하는 문제를 해결하기 위해!! getItemId 오버라이드 필요!!
//        main_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        main_recyclerview.setAdapter(recyclerViewDataAdapter);

    }
}
