package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CocomentAdapter extends RecyclerView.Adapter<CocomentAdapter.CocomentViewHolder> {

    Context context;
    PostingInfo postingInfo;//도큐먼트 아이디 때문에 필요
    List<CommentInfo> cocomentList;
    private String TAG = "TAGCocommentAdapter";


    //생성자
    public CocomentAdapter(Context context,
                           PostingInfo postingInfo,
                           List<CommentInfo> cocomentList) {
        this.context = context;
        this.postingInfo = postingInfo;
        this.cocomentList = cocomentList;
    }

    //뷰홀더
    public class CocomentViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName;
        TextView desc;
        TextView time;

        public CocomentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.desc = itemView.findViewById(R.id.desc_cocoment);
            this.userImage = itemView.findViewById(R.id.userImage_cocoment);
            this.userName = itemView.findViewById(R.id.userName_cocoment);
            this.time = itemView.findViewById(R.id.time_cocoment);
        }
    }


    @NonNull
    @Override
    public CocomentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(context).inflate(R.layout.recyclerview_cocomment_row, viewGroup, false);
        CocomentViewHolder cocomentViewHolder = new CocomentViewHolder(view);
        return cocomentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CocomentViewHolder cocomentViewHolder, int i) {
        //프로필 이미지 부분
        GlideApp.with(context)
                .load(cocomentList.get(i).getImgPath())
                .into(cocomentViewHolder.userImage);

        //유저이름 부분
        cocomentViewHolder.userName.setText(cocomentList.get(i).getWriterName());

        //댓글 내용부분
        cocomentViewHolder.desc.setText(cocomentList.get(i).getComment());

        //시간 부분
        Long time = cocomentList.get(i).getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 - hh시 mm분 ss초");
        String result = dateFormat.format(date);
        cocomentViewHolder.time.setText(result);
    }

    @Override
    public int getItemCount() {
        return cocomentList.size();
    }
}

