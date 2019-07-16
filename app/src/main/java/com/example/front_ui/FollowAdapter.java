package com.example.front_ui;

import android.content.Context;
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

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    Context context;
    List<FollowInfo> followerList;

    public FollowAdapter(Context context, List<FollowInfo> followerList){
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
    public void onBindViewHolder(@NonNull FollowViewHolder followViewHolder, int i) {

        if(followerList.get(i).getProfileImagePath() != null){
            GlideApp.with(context)
                    .load(followerList.get(i).getProfileImagePath())
                    .into(followViewHolder.profileImage);
        }

        followViewHolder.profileUpper.setText(followerList.get(i).getProfileTextUpper());

        followViewHolder.profileLower.setText(followerList.get(i).getProfileTextLower());

        followViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "추후에 기능추가예정", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }


}
