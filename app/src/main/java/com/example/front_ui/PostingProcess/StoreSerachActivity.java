package com.example.front_ui.PostingProcess;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.front_ui.R;
import com.example.front_ui.StoreSearchFragVpAdapter;

public class StoreSerachActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    StoreSearchFragVpAdapter storeSearchFragVpAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_store_search);

        tabLayout = findViewById(R.id.tablayout_fragStoreSearch);
        viewPager = findViewById(R.id.viewpager_fragStoreSearch);
        storeSearchFragVpAdapter = new StoreSearchFragVpAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(storeSearchFragVpAdapter);
    }
}
