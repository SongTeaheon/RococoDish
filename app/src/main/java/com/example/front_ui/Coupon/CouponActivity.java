package com.example.front_ui.Coupon;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.front_ui.R;
import com.example.front_ui.RecyclerDecoration;

public class CouponActivity extends AppCompatActivity {

    TextView tv_numOfCoupon;
    ImageView iv_numOfCouponUsage;
    RecyclerView recy_coupons;
    CouponAdapter couponAdapter;
    RecyclerDecoration spaceDecoration = new RecyclerDecoration(4);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_page);

        //요소들 세팅
        tv_numOfCoupon = findViewById(R.id.numOfCoupon);
        iv_numOfCouponUsage = findViewById(R.id.numOfCouponUsage);
        recy_coupons = findViewById(R.id.recyclerView2);
        couponAdapter = new CouponAdapter(this);


        //리사이클러뷰 세팅
        recy_coupons.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recy_coupons.setHasFixedSize(true);
        recy_coupons.setAdapter(couponAdapter);
        recy_coupons.addItemDecoration(spaceDecoration);

        //쿠폰 개수 넣기
        tv_numOfCoupon.setText(couponAdapter.getItemCount() + " 장");
    }
}
