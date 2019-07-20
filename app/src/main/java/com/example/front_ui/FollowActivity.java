package com.example.front_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class FollowActivity extends AppCompatActivity {

    ViewPager followViewPager;
    TabLayout followTabLayout;
    FollowViewPagerAdapter followViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Intent intent = getIntent();
        String userUUID = intent.getStringExtra("userUUID");

        followViewPager = findViewById(R.id.follow_viewpager_followActivity);
        followTabLayout = findViewById(R.id.follow_tablayout_followActivity);

        initViewPager(userUUID);
        initTabLayout();


    }
    public void initViewPager(String userUUID){
        followViewPagerAdapter = new FollowViewPagerAdapter(getSupportFragmentManager(), userUUID);
        followViewPager.setAdapter(followViewPagerAdapter);
    }
    public void initTabLayout(){
        followTabLayout.setupWithViewPager(followViewPager);
        followTabLayout.getTabAt(0).setIcon(R.drawable.basic_user_image);
        followTabLayout.getTabAt(1).setIcon(R.drawable.basic_user_image);
    }
}
