package com.example.front_ui;

import android.content.Context;
import android.content.Intent;
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
import com.example.front_ui.Utils.LocationUtil;
import com.example.front_ui.Utils.MathUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DoubleRecyAdapter1 extends RecyclerView.Adapter<DoubleRecyAdapter1.DoubleRecyViewHolder1> {

    String TAG = "TAG_DoubleRecyAdapter1";
    Context context;
    List<StoreInfo> parentList;
    GeoPoint myGeoPoint;
//    Location location;

    public DoubleRecyAdapter1(Context context,
                              List<StoreInfo> parentList,
                              GeoPoint myGeoPoint){
        this.context = context;
        this.parentList = parentList;
        this.myGeoPoint = myGeoPoint;
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


        //가게 거리
        double storeLatitude = parentList.get(i).getGeoPoint().getLatitude();
        double storeLongitude = parentList.get(i).getGeoPoint().getLongitude();

        double myLatitude = myGeoPoint.getLatitude();
        double myLongitude = myGeoPoint.getLongitude();
            //거리 계산하기
        double distance = MathUtil.distanceBtwMeAndStore(storeLatitude, storeLongitude, myLatitude, myLongitude);
        String distanceStr = String.valueOf(distance);
        String result;
        if(distanceStr.startsWith("0")){
            result = distanceStr.substring(2, 5) + " m";
        }
        else{
            result = distanceStr.substring(0, 4) + " km";
        }
        doubleRecyViewHolder1.storeDistance.setText(result);


        //가게 주소
        doubleRecyViewHolder1.storeAddress.setText(parentList.get(i).getAddress());


        //별점 평균
        doubleRecyViewHolder1.storeStar.setText(String.valueOf(parentList.get(i).getAver_star()).substring(0, 3));



        /**
         * 수평 리사이클러뷰 설정
         * */
        final List<PostingInfo> childList = new ArrayList<>();
        final DoubleRecyAdapter2 doubleRecyAdapter2 = new DoubleRecyAdapter2(context, childList, parentList.get(i), distance);
        doubleRecyViewHolder1.childRecy.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        doubleRecyViewHolder1.childRecy.setAdapter(doubleRecyAdapter2);


        //가게 내 포스팅들 가져오기
        String docId = parentList.get(i).getStoreId();

        FirebaseFirestore.getInstance()
                .collection("가게")
                .document(docId)
                .collection("포스팅채널")
                .orderBy("postingTime", Query.Direction.DESCENDING)
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


        //클릭 이벤트들

        //StorePage로 이동
        final StoreInfo storeInfo = parentList.get(i);
        doubleRecyViewHolder1.storeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : 가게페이지로 이동
                Intent intent = new Intent(context, StorePageActivity.class);
                intent.putExtra("storeName", storeInfo.getName());
                intent.putExtra("averStar", storeInfo.getAver_star());
                intent.putExtra("documentId", storeInfo.getStoreId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }
}
