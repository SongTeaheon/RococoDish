package com.example.front_ui;

import android.content.Context;
import android.graphics.Paint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.front_ui.DataModel.PostingInfo;
//import com.songtaeheon.posting.Utils.GlideApp;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> { // 가로 리사이클러뷰를 위한 어뎁터

    private ArrayList<PostingInfo> list;
    private Context mContext;
    private static final String TAG = "TAGSectionViewAdapter";
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    public SectionListDataAdapter(Context context, String storeId) {
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        list = new ArrayList<>();
        getPostDataFromCloud(storeId);
    }



    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.polar_style, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder");

        final PostingInfo singleItem = list.get(i);
        Log.d(TAG, "downloadImageFromFirebaseStorage : " + singleItem.imagePathInStorage);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeCap(Paint.Cap.ROUND);
        circularProgressDrawable.setCenterRadius(10f);
        circularProgressDrawable.setBackgroundColor(R.color.colorMainSearch);
        circularProgressDrawable.start();

        StorageReference fileReference = storage.getReferenceFromUrl(singleItem.imagePathInStorage);
        GlideApp.with(mContext)
                .load(fileReference)
                .placeholder(circularProgressDrawable)
                .into(holder.imageView);

        //태완태완 이미지 선택시 반응입니다. 여기가 그 각 포스팅1 글 누르면 발생하는 이벤트 부분입니다.
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DishView.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("postingInfo", singleItem);
                intent.putExtras(bundle);

                mContext.startActivity(intent);

                Toast.makeText(v.getContext(), singleItem.title, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
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
                                list.add(post);
                            }
                            //post데이터가 들어오면 리사이클러뷰를 refresh한다.
                            notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

}
