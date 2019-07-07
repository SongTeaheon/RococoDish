package com.example.front_ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;


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



//        //답변 부분 꾸욱 누르면 답변 수정할 수 있는 다이얼로그 창이 나옴.
//        FirebaseFirestore.getInstance().collection("포스팅")
//                .document(postingInfo.postingId)
//                .collection("댓글")
//                .orderBy("time", Query.Direction.ASCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
////                            final String commentDocId = queryDocumentSnapshots.getDocuments().get(i).getId();
//                        DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(i);
//                            final String commentDocId = ds.getId();
//
//                            commentViewHolder.answer.setOnLongClickListener(new View.OnLongClickListener() {
//                                @Override
//                                public boolean onLongClick(View v) {
//                                    Dialog_Answer dialog_answer = new Dialog_Answer(context, postingInfo, commentDocId);
//                                    dialog_answer.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                    dialog_answer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    dialog_answer.show();
//                                    return false;
//                                }
//                            });
//                    }
//                });


        //질문을 꾸욱 길게 누르면 다이얼로그 뜨게함.
        //내가 쓴 글일 경우와 남이 쓴 글일 경우를 나눔
//        commentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {

//                final String[] otherCommentOptions = new String[] {"질문에 답변달기", "닫기"};
//                final String[] meCommentOptions = new String[] {"수정하기", "삭제하기", "닫기"};
//
//                String commentWriterId = list.get(commentViewHolder.getLayoutPosition()).getCommentWriterId();
//
//                Log.d(TAG, list.get(commentViewHolder.getLayoutPosition()).getCommentWriterId());
//
//                //내가 쓴 글일 경우
//                if(commentWriterId.equals(FirebaseAuth.getInstance().getUid())){
//                    new AlertDialog.Builder(context)
//                            .setTitle("내가 쓴 글")
//                            .setItems(meCommentOptions, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    switch (meCommentOptions[which]){
//                                        case "수정하기":
//                                            Toast.makeText(context, "수정합니다.", Toast.LENGTH_SHORT).show();
//                                            break;
//                                        case "삭제하기":
//                                            Toast.makeText(context, "삭제합니다.", Toast.LENGTH_SHORT).show();
//                                            break;
//                                        case "닫기":
//                                            dialog.dismiss();
//                                            break;
//                                    }
//                                }
//                            }).show();
//                }
//                //다른 사람이 쓴 경우
//                else if(commentWriterId != FirebaseAuth.getInstance().getUid()){
//                    new AlertDialog.Builder(context)
//                            .setTitle("남이 쓴 글")
//                            .setItems(otherCommentOptions, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    switch (otherCommentOptions[which]){
//                                        case "질문에 답변달기":
//                                            Toast.makeText(context, "질문에 답변답니다.", Toast.LENGTH_SHORT).show();
//                                            break;
//                                        case "닫기":
//                                            dialog.dismiss();
//                                            break;
//                                    }
//                                }
//                            }).show();
//                }
//
//                return true;
//            }
//        });


        //댓글 올라오는 애니메이션
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        commentViewHolder.itemView.startAnimation(animation);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
