package com.rococodish.front_ui;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.Edit.BroadcastUtils;
import com.rococodish.front_ui.Utils.DataPassUtils;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;

import java.util.ArrayList;
import java.util.Objects;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> { // 가로 리사이클러뷰를 위한 어뎁터

    private ArrayList<PostingInfo> list;
    private Context mContext;
    private static final String TAG = "TAGSectionViewAdapter";
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    StoreInfo storeInfo;
    double distance;
    private String uuid;
    int index;
    FrameLayout loadingFrame;


    public SectionListDataAdapter(Context context,
                                  StoreInfo storeInfo,
                                  double distance,
                                  int index,
                                  FrameLayout loadingFrame) {
        Log.d(TAG, "SectionListDataAdapter");
        this.mContext = context;
        this.storeInfo = storeInfo;
        this.distance = distance;
        this.index = index;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        list = new ArrayList<>();
        getPostDataFromCloud(storeInfo.getStoreId());
        this.loadingFrame = loadingFrame;
    }



    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.polar_style, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {
        Log.d(TAG, "onBindViewHolder");

        final PostingInfo singleItem = list.get(i);
        Log.d(TAG, "downloadImageFromFirebaseStorage : " + singleItem.imagePathInStorage);


        StorageReference fileReference = storage.getReferenceFromUrl(singleItem.imagePathInStorage);
        GlideApp.with(mContext)
                .load(fileReference)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(mContext))
                .into(holder.imageView);



        //태완태완 이미지 선택시 반응입니다. 여기가 그 각 포스팅1 글 누르면 발생하는 이벤트 부분입니다.
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //데이터 수정이 일어난 데이터가 클릭되면 수정된 데이터를 여기에서 반영해야함1!! receive 필터 아이디는 postingId
                LocalBroadcastManager
                        .getInstance(mContext)
                        .registerReceiver(BroadcastUtils.getBrdCastReceiver_posting(singleItem),
                                new IntentFilter(singleItem.getPostingId()));

                //데이터 전달
                Intent intent = new Intent(mContext, DishView.class);
                DataPassUtils.makeIntentForData(intent, singleItem, storeInfo);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        PostingInfo postingInfo = list.get(position);
        return postingInfo.getViewId();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        View view;

        public SingleItemRowHolder(View view) {
            super(view);
            this.view = view;
            Log.d(TAG, "singleItemRowHolder");
            this.imageView = (ImageView) view.findViewById(R.id.imagefood);
        }

    }


    //posting Info를 가져온다.
    private void getPostDataFromCloud(String storeId) {

        Log.d(TAG, "getDataFromFirestore");
        db.collection("가게").document(storeId).collection("포스팅채널")
                .orderBy("postingTime", Query.Direction.DESCENDING)//가게별로 데이터를 불러올 때 시간순으로 불러올 수 있게 수정했습니다.(테헌님이 쓴 코드, 태완 수정완료)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //조건에 해당하는게 없는지 확인
                            if (task.getResult().isEmpty())
                                Log.d(TAG, "task.getResult : " + task.getResult().isEmpty());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                PostingInfo post = document.toObject(PostingInfo.class);
                                post.setViewId(list.size()+1);
                                list.add(post);
                            }
                            //post데이터가 들어오면 리사이클러뷰를 refresh한다.
                            Log.d(TAG, "notifyDataSetChanged!!!!!!!!!");
                            notifyDataSetChanged();

                            if(index == 3)//다이얼로그 끝!
                                loadingFrame.setVisibility(View.GONE);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

}
