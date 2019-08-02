package com.rococodish.front_ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.SerializableStoreInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.Utils.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DoubleRecyAdapter2 extends RecyclerView.Adapter<DoubleRecyAdapter2.DoubleRecyViewHolder2>{

    Context context;
    List<PostingInfo> list;
    StoreInfo storeInfo;
    double distance;//부모 어댑터에서 계산한 지금 내 위치와 가게와의 거리
    //todo : 거리추가

    public DoubleRecyAdapter2(Context context,
                              List<PostingInfo> list,
                              StoreInfo storeInfo,
                              double distance){
        this.context = context;
        this.list = list;
        this.storeInfo = storeInfo;
        this.distance = distance;
    }


    public class DoubleRecyViewHolder2 extends RecyclerView.ViewHolder {

        ImageView imageFood;
        public DoubleRecyViewHolder2(@NonNull View itemView) {
            super(itemView);

            imageFood = itemView.findViewById(R.id.imagefood);
        }
    }

    @NonNull
    @Override
    public DoubleRecyViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.polar_style, viewGroup, false);
        DoubleRecyViewHolder2 doubleRecyViewHolder2 = new DoubleRecyViewHolder2(view);
        return doubleRecyViewHolder2;
    }

    @Override
    public void onBindViewHolder(@NonNull DoubleRecyViewHolder2 doubleRecyViewHolder2, int i) {

        //이미지 설정
        StorageReference fileReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(list.get(i).imagePathInStorage);
        GlideApp.with(context)
                .load(fileReference)
                .into(doubleRecyViewHolder2.imageFood);


        /**
         * 포스팅 클릭시 DishView이동
         * **/
        final PostingInfo postingInfo = list.get(i);
        final SerializableStoreInfo serializableStoreInfo = new SerializableStoreInfo(storeInfo);

        doubleRecyViewHolder2.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishView.class);
                intent.putExtra("postingInfo", postingInfo);
                intent.putExtra("storeInfo", serializableStoreInfo);
                intent.putExtra("distance", distance);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
