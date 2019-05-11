package com.example.front_ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DishView extends AppCompatActivity {

    Button buttonToDetail;
    ImageView imageView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        buttonToDetail = (Button) findViewById(R.id.toDetail1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        buttonToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDetail();
            }
        });
    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
}

