package com.example.front_ui;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DoubleRecyAdapter1 extends RecyclerView.Adapter<DoubleRecyAdapter1.DoubleRecyViewHolder1> {

    String TAG = "TAG_DoubleRecyAdapter1";
    Context context;
    List<StoreInfo> parentList;
//    Location location;

    public DoubleRecyAdapter1(Context context,
                              List<StoreInfo> parentList){
        this.context = context;
        this.parentList = parentList;
//        this.location = location;
    }

    public class DoubleRecyViewHolder1 extends RecyclerView.ViewHolder {

        TextView storeName;
        TextView storeDistance;
        TextView storeAddress;
        TextView storeStar;
        RecyclerView childRecy;

        public DoubleRecyViewHolder1(@NonNull View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.storeName);
            storeDistance = itemView.findViewById(R.id.storeDistance);
            storeAddress = itemView.findViewById(R.id.storeAddress);
            storeStar = itemView.findViewById(R.id.storeStar);
            childRecy = itemView.findViewById(R.id.recyclerView);
        }
    }

    @NonNull
    @Override
    public DoubleRecyViewHolder1 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_items, viewGroup, false);
        DoubleRecyViewHolder1 viewHolder1 = new DoubleRecyViewHolder1(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(@NonNull DoubleRecyViewHolder1 doubleRecyViewHolder1, int i) {

        //가게이름
        doubleRecyViewHolder1.storeName.setText(parentList.get(i).getName());

//        //가게 거리
//        double distance = LocationUtil.getDistanceFromMe(location, list.get(i).getGeoPoint());
//        String distanceStr = MathUtil.adjustedDistance(distance);
//        doubleRecyViewHolder1.storeDistance.setText(distanceStr);

        //가게 주소
        doubleRecyViewHolder1.storeAddress.setText(parentList.get(i).getAddress());

        //별점 평균
        doubleRecyViewHolder1.storeStar.setText(Double.toString(parentList.get(i).getAver_star()));



        /**
         * 수평 리사이클러뷰
         * */
        final List<PostingInfo> childList = new ArrayList<>();
        final DoubleRecyAdapter2 doubleRecyAdapter2 = new DoubleRecyAdapter2(context, childList);
        doubleRecyViewHolder1.childRecy.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        doubleRecyViewHolder1.childRecy.setAdapter(doubleRecyAdapter2);

        String docId = parentList.get(i).getStoreId();

        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .whereEqualTo("storeId", docId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if(!queryDocumentSnapshots.isEmpty()){

                            for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                PostingInfo postingInfo = dc.toObject(PostingInfo.class);

                                childList.add(postingInfo);

                                doubleRecyAdapter2.notifyItemChanged(childList.size());
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }



}
