package com.rococodish.front_ui.Coupon;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.rococodish.front_ui.R;

public class CouponDialog extends Dialog {

    public CouponDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_page_dialog);


    }
}
