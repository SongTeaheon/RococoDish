package com.example.front_ui.Coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.front_ui.DataModel.CouponInfo;
import com.rococodish.front_ui.R;
import java.util.ArrayList;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    Context context;
    TextView itemCouponName;
    TextView itemCouponDate;
    TextView itemCouponAdvantage;
    TextView itemCouponDiscount;
    ImageView imageViewUse;
    List<CouponInfo> list;

    public CouponAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
        getCouponData();
    }

    public class CouponViewHolder extends RecyclerView.ViewHolder {
        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCouponName = itemView.findViewById(R.id.itemCouponName);
            itemCouponDate = itemView.findViewById(R.id.itemCouponDate);
            itemCouponAdvantage = itemView.findViewById(R.id.itemCouponAdvantage);
            itemCouponDiscount = itemView.findViewById(R.id.itemCouponDiscount);
            imageViewUse = itemView.findViewById(R.id.imageViewUse);
        }
    }


    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coupon_page_item, parent, false);
        CouponViewHolder couponViewHolder = new CouponViewHolder(view);
        return couponViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        //쿠폰 이름
        itemCouponName.setText(list.get(position).getTitle());

        //쿠폰 유효기간
        String toDate = list.get(position).getToDate();
        String fromDate = list.get(position).getFromDate();
        itemCouponDate.setText("유효기간 : " + toDate + " ~ " + fromDate);

        //최소금액 표시
        itemCouponAdvantage.setText(list.get(position).getAtLeastPrice());

        //할인금액
        itemCouponDiscount.setText(list.get(position).getDiscountMoney());

        //사용하기 클릭
        imageViewUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "추후예정", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void getCouponData(){

        //일단 더미 데이터로 구동체크
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));
        list.add(new CouponInfo("쿠폰1", "2019월 1월 1일", "2019년 9월 9일", "최소 1만원 이상부터 사용가능", "3000"));

    }

}
