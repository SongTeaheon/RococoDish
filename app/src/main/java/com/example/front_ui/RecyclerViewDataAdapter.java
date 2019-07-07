package com.example.front_ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.Utils.MathUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.PostingInfo;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder> { // 세로 리사이클러 뷰를 위한 어뎁터

    private ArrayList<StoreInfo> list;
    private Context mContext;
    private static final String TAG = "TAGRecyclerViewAdapter";
    FirebaseFirestore db;
    SectionListDataAdapter itemListDataAdapter;
    private Location mCurrentLocation;
    private ShimmerFrameLayout shimmerFrameLayout;




    public RecyclerViewDataAdapter(Context context, Location cLocation) {
        Log.d(TAG, "adpater constructor called");
        Log.d(TAG, "x, y : " + cLocation.getLongitude() + " " + cLocation.getLatitude());
        list= new ArrayList<>();
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
        mCurrentLocation = cLocation;
        getCloseStoreIdAndGetData();
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vertical_items, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {
        Log.d(TAG, "onBindViewHolder");
        Log.d(TAG, "storeId : " + list.get(i).getStoreId());
        final String sectionName = list.get(i).getName();
        final double sectionStar = list.get(i).aver_star;
        double distance = getDistanceFromMe(list.get(i).getGeoPoint());//내 위치로부터의 거리 측정.
        Log.d(TAG, "get distance : " + distance);

        //텍스트 세팅 부분
        itemRowHolder.storeName.setText(sectionName);
        itemRowHolder.storeStar.setText(Double.toString(sectionStar));
        //to do : distance 표시

        itemListDataAdapter = new SectionListDataAdapter(mContext, list.get(i), distance);

        itemRowHolder.recycler_view_list.setHasFixedSize(true);
        itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);




        //가게 정보 부분 선택 리스너!!
        itemRowHolder.touchStore.setOnClickListener(new View.OnClickListener() { // 각 가게별 상단 바를 터치 했을 때 이벤트 설정
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StorePage.class);

                Log.d(TAG, "name : " + list.get(i).getName() + " averStar : " + list.get(i).getAver_star() + " docId : " + list.get(i).getStoreId());
                intent.putExtra("storeName", list.get(i).getName());
                intent.putExtra("averStar", list.get(i).getAver_star());
                intent.putExtra("documentId", list.get(i).getStoreId());

                mContext.startActivity(intent);
                Toast.makeText(v.getContext(), "click event on more, "+sectionName , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        public CardView touchStore;
        public TextView storeName;
        public TextView storeStar;
        public RecyclerView recycler_view_list;



        //세로부분 리사이클러 뷰 적용!
        public ItemRowHolder(View view) {
            super(view);

            this.touchStore = (CardView) view.findViewById(R.id.touchStore);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recyclerView);
            storeName = (TextView) view.findViewById(R.id.storeName);
            storeStar = (TextView) view.findViewById(R.id.storeStar);


        }

    }




    //내 위치 주변 10km 가게 찾기.
    private int radius = 5;
    HashSet<String> dcSet = new HashSet<>(); //중복x
    private void getCloseStoreIdAndGetData() {

        CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("가게");
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        final GeoPoint myPoint = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //내 위치에서 radius(km)이내에 있는 값을 찾아라
        final GeoQuery geoQuery = geoFirestore.queryAtLocation(myPoint, radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            //내 위치에서 radius만큼 떨어진 곳에 가게들이 있을 떄! -> 데이터를 가져온다.
            @Override
            public void onKeyEntered(String documentID, GeoPoint location) {
                if(!dcSet.contains(documentID) && (radius < 50 && dcSet.size() < 50)) {
                    Log.d(TAG, String.format("Document %s entered the searc area at [%f,%f] (radius : %d)", documentID, location.getLatitude(), location.getLongitude(), radius));
                    dcSet.add(documentID);
                    getStoreDataFromCloud(documentID, radius);

                }
            }

            @Override
            public void onKeyExited(String documentID) {
                System.out.println(String.format("Document %s is no longer in the search area", documentID));
            }

            @Override
            public void onKeyMoved(String documentID, GeoPoint location) {
                System.out.println(String.format("Document %s moved within the search area to [%f,%f]", documentID, location.getLatitude(), location.getLongitude()));
            }

            //내 위치에서 radius만큼 떨어진 곳을 다 찾았을 때 더 찾으려면 여기에!
            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "All initial data has been loaded and events have been fired!" + radius +" size : "+ dcSet.size()+"mypoint "+myPoint.getLatitude()+" "+ myPoint.getLongitude());
                //가게 수가 50개가 넘거나 반경이 100km가 넘으면 STOP
                if(dcSet.size() < 50 && radius < 50) {
                    radius += 5;
                    getCloseStoreIdAndGetData();
                }
            }

            @Override
            public void onGeoQueryError(Exception exception) {
                System.err.println("There was an error with this query: " + exception.getLocalizedMessage());
            }
        });
    }



    private void getStoreDataFromCloud(final String documentID, final int radius) {
        Log.d(TAG, "getDataFromFirestore, documentID : " + documentID);

        db.collection("가게").document(documentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            //store 정보를 가져오고, id를 따로 저장한다.
                            if(document.getData() != null) {
                                StoreInfo storeInfo = document.toObject(StoreInfo.class);
                                storeInfo.setStoreId(documentID);
                                //해당 가게 정보의 post데이터를 가져온다.
                                //소수점 한자리로 반올림.
                                storeInfo.aver_star = MathUtil.roundOnePlace(storeInfo.aver_star);
                                Log.d(TAG, "가게이름 : " + storeInfo.name + " radius : " + radius);
                                list.add(storeInfo);
                                notifyDataSetChanged();
//                                notifyItemChanged(list.size()+1);//이 부분 수정하니까 깜빡이는 부분 사라짐.
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private double getDistanceFromMe(GeoPoint geoPoint){
        double distance;
        Location location = new Location("dummyprovider");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());

        distance = mCurrentLocation.distanceTo(location);

        return distance;
    }

}