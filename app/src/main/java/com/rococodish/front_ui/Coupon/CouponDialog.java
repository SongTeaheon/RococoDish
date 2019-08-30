package com.rococodish.front_ui.Coupon;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rococodish.front_ui.R;

public class CouponDialog extends Dialog {

    TextView tv_ok;
    TextView tv_close;
    TextView tv_couponName;
    Context context;
    String docId;
    String couponTitle;
    View.OnClickListener yesClickListener;

    public CouponDialog(@NonNull Context context,
                        String docId,
                        String couponTitle,
                        View.OnClickListener yesClickListener) {
        super(context);
        this.context = context;
        this.docId = docId;
        this.couponTitle = couponTitle;
        this.yesClickListener = yesClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_page_dialog);


        tv_ok = findViewById(R.id.yes_btn_couponDialog);
        tv_close = findViewById(R.id.no_btn_couponDialog);
        tv_couponName = findViewById(R.id.tv_couponName);

        //쿠폰제목
        tv_couponName.setText(couponTitle);

        //쿠폰 닫기
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //쿠폰 사용하기
        tv_ok.setOnClickListener(yesClickListener);
    }
}
