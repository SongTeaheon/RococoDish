package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;

import java.util.List;

public class DoubleRecyAdapter2 extends RecyclerView.Adapter<DoubleRecyAdapter2.DoubleRecyViewHolder2>{

    Context context;
    List<PostingInfo> list;

    public DoubleRecyAdapter2(Context context,
                              List<PostingInfo> list){
        this.context = context;
        this.list = list;
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
        GlideApp.with(context)
                .load(list.get(i).getImagePathInStorage())
                .into(doubleRecyViewHolder2.imageFood);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
