package com.rococodish.front_ui.Coupon;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rococodish.front_ui.RecyclerDecoration;
import com.rococodish.front_ui.R;

public class CouponActivity extends AppCompatActivity {

    TextView tv_numOfCoupon;
    ImageView iv_numOfCouponUsage;
    ImageView iv_backBtn;
    RecyclerView recy_coupons;
    com.rococodish.front_ui.Coupon.CouponAdapter couponAdapter;
    RecyclerDecoration spaceDecoration = new RecyclerDecoration(4);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_page);

        //요소들 세팅
        tv_numOfCoupon = findViewById(R.id.numOfCoupon);
        iv_numOfCouponUsage = findViewById(R.id.numOfCouponUsage);
        recy_coupons = findViewById(R.id.rv_notice);
        iv_backBtn = findViewById(R.id.backButton);

        couponAdapter = new CouponAdapter(this);


        //리사이클러뷰 세팅
        recy_coupons.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recy_coupons.setHasFixedSize(true);
        recy_coupons.setAdapter(couponAdapter);
        recy_coupons.addItemDecoration(spaceDecoration);

        //쿠폰 개수 넣기
        tv_numOfCoupon.setText(couponAdapter.getItemCount() + " 장");

        //뒤로가기 버튼
        iv_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
