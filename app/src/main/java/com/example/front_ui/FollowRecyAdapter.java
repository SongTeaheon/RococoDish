package com.example.front_ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.FollowInfo;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.GlidePlaceHolder;

import java.util.List;

public class FollowRecyAdapter extends RecyclerView.Adapter<FollowRecyAdapter.FollowViewHolder> {

    Context context;
    List<FollowInfo> followerList;

    public FollowRecyAdapter(Context context, List<FollowInfo> followerList){
        this.context = context;
        this.followerList = followerList;
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView profileUpper;
        TextView profileLower;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profileImage = itemView.findViewById(R.id.profileImage_imageview_followRecy);
            this.profileUpper = itemView.findViewById(R.id.profileUpper_textview_followRecy);
            this.profileLower = itemView.findViewById(R.id.profileLower_textview_followRecy);
        }
    }


    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_follow_item, viewGroup, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder followViewHolder, final int i) {

        if(followerList.get(i).getProfileImagePath() != null){
            GlideApp.with(context)
                    .load(followerList.get(i).getProfileImagePath())
                    .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                    .into(followViewHolder.profileImage);
        }

        followViewHolder.profileUpper.setText(followerList.get(i).getProfileTextUpper());

        followViewHolder.profileLower.setText(followerList.get(i).getProfileTextLower());

        followViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyPage.class);
                intent.putExtra("userUUID", followerList.get(i).getUserUID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }


}
