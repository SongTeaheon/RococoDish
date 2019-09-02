package com.rococodish.front_ui.Coupon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.rococodish.front_ui.DataModel.CouponInfo;
import com.rococodish.front_ui.R;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    Context context;
    TextView itemStoreName;
    TextView itemCouponDesc;
    TextView itemValidationDate;
    ImageView iv_useCoupon;
    List<CouponInfo> list;
    ListenerRegistration listener;
    String TAG = "TAGCouponAdapter";
    View.OnClickListener yesClickListener;
    CouponDialog couponDialog;

    public CouponAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
        getCouponData();
    }

    public class CouponViewHolder extends RecyclerView.ViewHolder {
        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            itemStoreName = itemView.findViewById(R.id.tv_storeName_coupon);
            itemCouponDesc = itemView.findViewById(R.id.tv_couponDesc);
            itemValidationDate = itemView.findViewById(R.id.tv_validationDate);
            iv_useCoupon = itemView.findViewById(R.id.iv_userCoupon);
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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, final int position) {
        //가게 이름
        itemStoreName.setText(list.get(position).getStoreName());

        //쿠폰 유효기간
        String toDate = list.get(position).getToDate();
        String fromDate = list.get(position).getFromDate();
        itemValidationDate.setText("유효기간 : " + toDate + " ~ " + fromDate);

        //쿠폰 내용
        itemCouponDesc.setText(list.get(position).getCouponDesc());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : 클릭하면 다음 가게 페이지 쿠폰내용으로 이동
                //TODO : storeId, storeName intent로 이동

            }
        });

        //사용하기 클릭
        iv_useCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String docId = list.get(position).getDocId();
                String title = list.get(position).getCouponDesc();
                yesClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userCouponData(docId);
                    }
                };
                couponDialog = new CouponDialog(context, docId, title, yesClickListener);
                couponDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                couponDialog.show();
            }
        });
    }

    void userCouponData(String docId){
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("쿠폰함")
                .document(docId)
                .update("isCouponUsed", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "쿠폰이 사용되었습니다.", Toast.LENGTH_LONG).show();
                        list.clear();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "쿠폰 사용에 문제가 생겼습니다.", Toast.LENGTH_LONG).show();
                        list.clear();
                        notifyDataSetChanged();
                    }
                });
        couponDialog.dismiss();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void getCouponData(){


        EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, e.getMessage());
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                    list.clear();

                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        CouponInfo couponInfo = snapshot.toObject(CouponInfo.class);

                        list.add(couponInfo);

                    }
                    notifyDataSetChanged();
                }
            }
        };

        listener = FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("쿠폰함")
                .whereEqualTo("isCouponUsed", false)
                .addSnapshotListener(eventListener);


    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        listener.remove();
    }
}
