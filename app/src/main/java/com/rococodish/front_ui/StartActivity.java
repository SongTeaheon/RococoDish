package com.rococodish.front_ui;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    ImageView logo;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        logo = findViewById(R.id.logo_activityStart);
        tv = findViewById(R.id.tv_activityStart);
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_screen);
        logo.startAnimation(splashAnim);
        tv.startAnimation(splashAnim);

        final Intent intent = new Intent(this, SubActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
