package com.rococodish.front_ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class SettingActivity extends AppCompatActivity {

    KonfettiView animationView;
    CardView cardView;
    int clickNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        animationView = findViewById(R.id.animationView);
        cardView = findViewById(R.id.cardview1);

        //다이얼로그 세팅
        final SettingActivity_Dialog dialog = new SettingActivity_Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        //클릭 10번 누르면 빵빠레
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNum+=1;
                if(clickNum == 10){
                    cardView.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardView.setVisibility(View.VISIBLE);
                        }
                    }, 6800L);
                    animationView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(3000L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(12, 5))
                            .setPosition(-50f, animationView.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 5000L);
                    clickNum = 0;

                    //todo: 바운스 다이얼로그 개발자 소개
                    dialog.show();
                }
            }
        });
    }
}
