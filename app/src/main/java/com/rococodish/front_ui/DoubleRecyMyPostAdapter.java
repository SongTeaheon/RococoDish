package com.rococodish.front_ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.Utils.GlideApp;

import java.util.List;

public class DoubleRecyMyPostAdapter  extends RecyclerView.Adapter<DoubleRecyMyPostAdapter.DoubleRecyMyPostViewHolder>{

    Context context;
    List<PostingInfo> list;

    public DoubleRecyMyPostAdapter(Context context,
                                   List<PostingInfo> list){
        this.context = context;
        this.list = list;
    }



    public class DoubleRecyMyPostViewHolder extends RecyclerView.ViewHolder {
        ImageView myPostImg;
        public DoubleRecyMyPostViewHolder(@NonNull View itemView) {
            super(itemView);

            myPostImg = itemView.findViewById(R.id.imagefood);
        }
    }

    @NonNull
    @Override
    public DoubleRecyMyPostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.polar_style, viewGroup, false);
        DoubleRecyMyPostViewHolder viewHolder = new DoubleRecyMyPostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoubleRecyMyPostViewHolder doubleRecyMyPostViewHolder, int i) {

        GlideApp.with(context)
                .load(list.get(i).getImagePathInStorage())
                .into(doubleRecyMyPostViewHolder.myPostImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
