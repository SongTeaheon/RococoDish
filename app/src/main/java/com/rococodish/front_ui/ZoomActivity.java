package com.rococodish.front_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;

public class ZoomActivity extends AppCompatActivity {

    ImageView iv_foodImage;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        //데이터 받기
        Intent intent = getIntent();
        String foodImagePath = intent.getStringExtra("foodImagePath");

        if(foodImagePath == null){
            finish();
        }

        //이미지 세팅
        iv_foodImage = findViewById(R.id.iv_foodImage);
        iv_foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        GlideApp.with(this)
                .load(foodImagePath)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(this))
                .into(iv_foodImage);

        //툴바 세팅
        toolbar = findViewById(R.id.toolbar_zoomActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);

    }
}
