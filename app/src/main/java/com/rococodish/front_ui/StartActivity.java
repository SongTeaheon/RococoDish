package com.rococodish.front_ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    ImageView logo;
    TextView tv;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        checkInternetState();
        logo = findViewById(R.id.logo_activityStart);
        tv = findViewById(R.id.tv_activityStart);
        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_screen);
        logo.startAnimation(splashAnim);
        tv.startAnimation(splashAnim);
    }

    private void checkInternetState(){
        if(!(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())){
            Toast.makeText(this, "인터넷과 연결되어 있지 않습니다. ", Toast.LENGTH_LONG).show();
            finish();
        }else{
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
}
