package com.rococodish.front_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.rococodish.front_ui.DataModel.CommentInfo;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class CocomentAdapter extends RecyclerView.Adapter<CocomentAdapter.CocomentViewHolder> {

    Context context;
    PostingInfo postingInfo;//도큐먼트 아이디 때문에 필요
    List<CommentInfo> cocomentList;
    String commentDocId;
    private String TAG = "TAGCocommentAdapter";


    //생성자
    public CocomentAdapter(Context context,
                           PostingInfo postingInfo,
                           List<CommentInfo> cocomentList,
                           String commentDocId) {
        this.context = context;
        this.postingInfo = postingInfo;
        this.cocomentList = cocomentList;
        this.commentDocId = commentDocId;
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
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_cocomment_row, viewGroup, false);
        CocomentViewHolder cocomentViewHolder = new CocomentViewHolder(view);
        return cocomentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CocomentViewHolder cocomentViewHolder, final int i) {
        //프로필 이미지 부분
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(cocomentList.get(i).getCommentWriterId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, e.getMessage());
                        }
                        if (documentSnapshot.exists()) {

                            @Nullable String imagePath = (String) documentSnapshot.get("profileImage");

                            GlideApp.with(context.getApplicationContext())
                                    .load(imagePath != null ? imagePath : R.drawable.basic_user_image)
                                    .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                                    .into(cocomentViewHolder.userImage);
                        }
                    }
                });

        //유저이름 부분
        cocomentViewHolder.userName.setText(cocomentList.get(i).getWriterName());
        cocomentViewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMyPage(i);
            }
        });
        cocomentViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMyPage(i);
            }
        });
        //댓글 내용부분
        cocomentViewHolder.desc.setText(cocomentList.get(i).getComment());

        //시간 부분
        Long time = cocomentList.get(i).getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss");
        String result = dateFormat.format(date);
        cocomentViewHolder.time.setText(result);


        //TODO : 꾸욱 누르면 대댓글 삭제
        final String[] options = new String[]{"삭제", "닫기"};
        cocomentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (cocomentList.get(i).getCommentWriterId().equals(FirebaseAuth.getInstance().getUid())) {

                    new AlertDialog.Builder(context)
                            .setTitle("내가 쓴 대댓글")
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (options[which]) {

                                        case "삭제":
                                            FirebaseFirestore.getInstance()
                                                    .collection("포스팅")
                                                    .document(postingInfo.postingId)
                                                    .collection("댓글")
                                                    .document(commentDocId)
                                                    .collection("대댓글")
                                                    .document(cocomentList.get(i).getDocUuid())
                                                    .delete();
                                            notifyItemChanged(i);
                                        case "닫기":
                                            dialog.dismiss();
                                    }
                                }
                            }).show();
                }
                return false;
            }
        });

        //댓글 올라오는 애니메이션
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        cocomentViewHolder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return cocomentList.size();
    }

    private void moveToMyPage(int i) {
        Intent intent = new Intent(context, MyPage.class);
        intent.putExtra("userUUID", cocomentList.get(i).getCommentWriterId());
        context.startActivity(intent);
    }
}

