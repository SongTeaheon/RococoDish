package com.example.front_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.StorePostInfo;
import com.example.front_ui.Utils.GlideApp;

import java.util.List;

public class StorePageAdapter extends BaseAdapter {

    Context context;
    List<StorePostInfo> list;
    ImageView postImage;
    TextView starNum;
    TextView likeNum;

    public StorePageAdapter(Context context, List<StorePostInfo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.store_page_item, parent, false);

        postImage = convertView.findViewById(R.id.imagefood0);
        starNum = convertView.findViewById(R.id.storePageScore);
        likeNum = convertView.findViewById(R.id.storePageLike);

        //게시물 사진
        GlideApp.with(context)
                .load(list.get(position).getPostImagePath())
                .into(postImage);

        //게시물당 별점
        starNum.setText(list.get(position).getStarNum());

        //게시물당 좋아요 개수
        likeNum.setText(list.get(position).getLikeNum());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "추후 추가 예정", Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }
}
