package com.example.front_ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.Utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Nullable;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<CommentInfo> list;
    private Context context;



    public CommentAdapter(Context context,
                          ArrayList<CommentInfo> list){

        this.list = list;
        this.context = context;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView question;
        public TextView answer;
        public Long time;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewProfile);
            question = itemView.findViewById(R.id.text_Q);
            answer = itemView.findViewById(R.id.text_A);
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


        //질문 부분
        commentViewHolder.question.setText("Q. " + list.get(i).getQuestion());

        //답변 부분
        commentViewHolder.answer.setText("A. "+ list.get(i).getAnswer());

        //시간형식인데 나중에 쓸라면 적용하자(일단 댓글에 시간 안나와있어서 보류)
//        Long time = System.currentTimeMillis();
//        Date date = new Date(time);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 hh시 mm분 ss초");
//        dateFormat.format(date);



        //질문을 꾸욱 길게 누르면 다이얼로그 뜨게함.
        //내가 쓴 글일 경우와 남이 쓴 글일 경우를 나눔
        commentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(context, "길게 눌렀네요~", Toast.LENGTH_SHORT).show();
                final String[] otherCommentOptions = new String[] {"질문에 답변달기", "닫기"};
                final String[] meCommentOptions = new String[] {"수정하기", "삭제하기", "닫기"};

                //내가 쓴 글일 경우
                if(list.get(i).getCommenWriterId() == FirebaseAuth.getInstance().getUid()){
                    new AlertDialog.Builder(context)
                            .setTitle("내가 쓴 글")
                            .setItems(meCommentOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (meCommentOptions[which]){
                                        case "수정하기":
                                            Toast.makeText(context, "수정합니다.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "삭제하기":
                                            Toast.makeText(context, "삭제합니다.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "닫기":
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            }).show();
                }
                //다른 사람이 쓴 경우
                else{
                    new AlertDialog.Builder(context)
                            .setTitle("남이 쓴 글")
                            .setItems(otherCommentOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (otherCommentOptions[which]){
                                        case "질문에 답변달기":
                                            Toast.makeText(context, "질문에 답변답니다.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "닫기":
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            }).show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
