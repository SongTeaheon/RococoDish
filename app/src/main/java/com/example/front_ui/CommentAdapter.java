package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private PostingInfo postingInfo;
    private List<CommentInfo> list;
    private Context context;
    private String TAG = "TAGCommentAdapter";




    public CommentAdapter(Context context,
                          List<CommentInfo> list,
                          PostingInfo postingInfo){

        this.list = list;
        this.context = context;
        this.postingInfo = postingInfo;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView userName;
        public TextView userComment;
        public TextView time;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewProfile);
            userName = itemView.findViewById(R.id.userName_textview_comment);
            userComment = itemView.findViewById(R.id.userCommnet_textview_comment);
            time = itemView.findViewById(R.id.commentTime_textview_comment);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_qna_row, null);
        CommentViewHolder cv = new CommentViewHolder(view);
        return cv;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder commentViewHolder, final int i) {

        //프로필 사진 부분
        GlideApp.with(context)
                .load(list.get(i).getImgPath())
                .into(commentViewHolder.image);

        //시간 부분
        Long time = list.get(i).getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 / hh:mm / ss초");
        String result = dateFormat.format(date);
        commentViewHolder.time.setText(result);


        //유저 이름 부분
        commentViewHolder.userName.setText(list.get(i).getWriterName());

        //댓글 부분
        commentViewHolder.userComment.setText(list.get(i).getComment());


        //TODO : 대댓글 부분





        //댓글 올라오는 애니메이션
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        commentViewHolder.itemView.startAnimation(animation);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
