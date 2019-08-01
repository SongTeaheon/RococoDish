package com.example.front_ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.front_ui.Utils.LocationUtil;
import com.example.front_ui.Utils.MathUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.example.front_ui.DataModel.StoreInfo;


import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryDataEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder> { // 세로 리사이클러 뷰를 위한 어뎁터



    private ArrayList<StoreInfo> list;
    private Context mContext;
    private static final String TAG = "TAGRecyclerViewAdapter";
    FirebaseFirestore db;
    SectionListDataAdapter itemListDataAdapter;
    private Location mCurrentLocation;
    int isCalled;//onBindView가 다음으로 몇 번째가 불릴 건지 센다. - 중복해서 불리는 건 세지 않는다.
    FrameLayout loadingFrame;



    public RecyclerViewDataAdapter(Context context,
                                   Location cLocation,
                                   FrameLayout loadingFrame) {
        Log.d(TAG, "adpater constructor called");
        Log.d(TAG, "x, y : " + cLocation.getLongitude() + " " + cLocation.getLatitude());
        list= new ArrayList<>();
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
        mCurrentLocation = cLocation;
        getCloseStoreIdAndGetData();
        isCalled = 0;
        this.loadingFrame = loadingFrame;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder ");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vertical_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {



        Log.d(TAG, "onBindViewHolder : " + i);
        Log.d(TAG, "storeName : " + list.get(i).getName());
        Log.d(TAG, "storeId : " + list.get(i).getStoreId());
        final StoreInfo singleItem = list.get(i);

        final String sectionName = singleItem.getName();
        final double sectionStar = singleItem.aver_star;
        Log.d(TAG, "data check : aver_store_star : " + sectionStar);

        String address = singleItem.getAddress();
        double distance = LocationUtil.getDistanceFromMe(mCurrentLocation, singleItem.getGeoPoint());//내 위치로부터의 거리 측정.
        String distanceStr = MathUtil.adjustedDistance(distance);
        Log.d(TAG, "get distance : " + distance);

        //텍스트 세팅 부분
        itemRowHolder.storeName.setText(sectionName);
        itemRowHolder.storeStar.setText(Double.toString(sectionStar));
        itemRowHolder.storeDistance.setText(distanceStr);
//        itemRowHolder.storeAddress.setText(address);
        //to do : distance 표시

        if(isCalled <= i) {

            itemListDataAdapter = new SectionListDataAdapter(mContext, singleItem, distance, i, loadingFrame);
            itemListDataAdapter.setHasStableIds(true); //dataSetChange할 때, blink하는 문제를 해결하기 위해!! getItemId 오버라이드 필요!!
            itemRowHolder.recycler_view_list.setHasFixedSize(true);
            itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
            isCalled = i+1;
        }



        //가게 정보 부분 선택 리스너!!
        itemRowHolder.touchStore.setOnClickListener(new View.OnClickListener() { // 각 가게별 상단 바를 터치 했을 때 이벤트 설정
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StorePageActivity.class);

                Log.d(TAG, "name : " + singleItem.getName() + " averStar : " + singleItem.getAver_star() + " docId : " + singleItem.getStoreId());
                intent.putExtra("storeName", singleItem.getName());
                intent.putExtra("averStar", singleItem.getAver_star());
                intent.putExtra("documentId", singleItem.getStoreId());

                mContext.startActivity(intent);
//                Toast.makeText(v.getContext(), "click event on more, "+sectionName , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        StoreInfo storeInfo = list.get(position);
        return storeInfo.getViewId();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        public CardView touchStore;
        public TextView storeName;
        public TextView storeStar;
        public TextView storeDistance;
        public TextView storeAddress;
        public RecyclerView recycler_view_list;



        //세로부분 리사이클러 뷰 적용!
        public ItemRowHolder(View view) {
            super(view);

            this.touchStore = (CardView) view.findViewById(R.id.touchStore);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recyclerView);
            storeName = (TextView) view.findViewById(R.id.storeName);
            storeStar = (TextView) view.findViewById(R.id.storeStar);
            storeDistance = view.findViewById(R.id.storeDistance);
//            storeAddress = view.findViewById(R.id.storeAddress);


        }

    }




    //내 위치 주변 10km 가게 찾기.
    private int radius = 2;
    HashSet<String> dcSet = new HashSet<>(); //중복x
    private void getCloseStoreIdAndGetData() {

        CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("가게");
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        final GeoPoint myPoint = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //내 위치에서 radius(km)이내에 있는 값을 찾아라
        final GeoQuery geoQuery = geoFirestore.queryAtLocation(myPoint, radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                String documentId = documentSnapshot.getId();
                if(!dcSet.contains(documentId) && (radius <= 1000 /*&& dcSet.size() < 50*/)) {
                    StoreInfo storeInfo = documentSnapshot.toObject(StoreInfo.class);
                    storeInfo.aver_star = MathUtil.roundOnePlace(storeInfo.aver_star);
                    storeInfo.setStoreId(documentId);
                    storeInfo.setViewId(list.size()+1);//size가 storeInfo의 viewId가 된다.
                    Log.d(TAG, "geoquery 결과 가게이름 : " + storeInfo.name + " radius : " + radius + " listSize : "+ list.size());

                    list.add(storeInfo);
                    dcSet.add(documentId);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onDocumentExited(DocumentSnapshot documentSnapshot) {

            }

            @Override
            public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

            }

            @Override
            public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "All initial data has been loaded and events have been fired!" + radius +" size : "+ dcSet.size()+"mypoint "+myPoint.getLatitude()+" "+ myPoint.getLongitude());
                //가게 수가 50개가 넘거나 반경이 100km가 넘으면 STOP
                if(radius < 10){
                    radius += 2;
                    getCloseStoreIdAndGetData();
                }else if( radius < 50){
                    radius += 10;
                    getCloseStoreIdAndGetData();
                }else if( radius < 1000){
                    radius = 1000;
                    getCloseStoreIdAndGetData();
                }
            }

            @Override
            public void onGeoQueryError(Exception e) {
                Log.e(TAG,"onGeoQueryError : " + e.toString());
            }
        });
    }



}