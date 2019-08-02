package com.rococodish.front_ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.Utils.DataPassUtils;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SubViewPagerAdapter extends PagerAdapter {

    private String TAG = "TAGSubViewPagerAdapter";
    private ArrayList<PostingInfo> list;
    private Context context;

    ImageView image;

    //생성자
    public SubViewPagerAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
        getRandomData();//랜덤으로 데이터 가져오는 메서드
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_sub_viewpager_page, container, false);

        image = view.findViewById(R.id.image_viewpager_activity_sub);

        //이미지 세팅
        StorageReference filePath = FirebaseStorage.getInstance()
                .getReferenceFromUrl(list.get(position).imagePathInStorage);
        GlideApp.with(context)
                .load(filePath)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                .into(image);

        //뷰 클릭시 DishView에 데이터 전달하면서 이동동
       final PostingInfo selectedPostingInfo = list.get(position);
        String selectedStoreId = list.get(position).storeId;

        final Map<Integer, StoreInfo> storeInfoMap = new HashMap<>();

        FirebaseFirestore.getInstance()
                .collection("가게")
                .whereEqualTo("storeId", selectedStoreId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                storeInfoMap.put(0, dc.toObject(StoreInfo.class));
                            }
                        }
                    }
                });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishView.class);
                DataPassUtils.makeIntentForData(intent, selectedPostingInfo, storeInfoMap.get(0), 0.0);
                context.startActivity(intent);
            }
        });

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }


    //랜덤으로 포스팅 가져오는 메서드
    private void getRandomData(){

        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .orderBy("postingTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        list.clear();

                       if(!queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                PostingInfo postingInfo = dc.toObject(PostingInfo.class);

                                list.add(postingInfo);

                                notifyDataSetChanged();
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
    }
}
