package com.rococodish.front_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private List<CommentInfo> parentList;
    private Context context;
    private String TAG = "TAGCommentAdapter";
    private String myUid = FirebaseAuth.getInstance().getUid();



    public CommentAdapter(Context context,
                          List<CommentInfo> parentList,
                          PostingInfo postingInfo){

        this.parentList = parentList;
        this.context = context;
        this.postingInfo = postingInfo;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView userName;
        public TextView userComment;
        public TextView time;
        public RecyclerView childRecyclerView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewProfile);
            userName = itemView.findViewById(R.id.userName_textview_comment);
            userComment = itemView.findViewById(R.id.userCommnet_textview_comment);
            time = itemView.findViewById(R.id.commentTime_textview_comment);
            childRecyclerView = itemView.findViewById(R.id.cocoment_recyclerview);//대댓글 전용 리사이클러뷰
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_comment_row, viewGroup, false);
        CommentViewHolder cv = new CommentViewHolder(view);
        return cv;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder commentViewHolder, final int i) {

        //프로필 사진 부분
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(parentList.get(i).getCommentWriterId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists()){
                            //프로필 이미지 부분
                            GlideApp.with(context.getApplicationContext())
                                    .load(documentSnapshot.get("profileImage"))
                                    .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                                    .into(commentViewHolder.image);

                            //프로필 이름 부분
                            commentViewHolder.userName.setText(documentSnapshot.get("nickname").toString());
                        }
                    }
                });
        //이미지 누를시 상대방 마이페이지 이동
        commentViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMyPage(i);
            }
        });
        //이름 누를시에도 상대방 마이페이지 이동
        commentViewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMyPage(i);
            }
        });

        //시간 부분
        Long time = parentList.get(i).getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss");
        String result = dateFormat.format(date);
        commentViewHolder.time.setText(result);


        //댓글 부분
        commentViewHolder.userComment.setText(parentList.get(i).getComment());


        /**
         * 대댓 처리 부분
         * **/
        //대댓 리사이클러뷰 설정
        final List<CommentInfo> childList = new ArrayList<>();
        //대댓 리사이클러뷰
        final CocomentAdapter cocomentAdapter;
        cocomentAdapter = new CocomentAdapter(context, postingInfo, childList, parentList.get(i).getDocUuid());
        commentViewHolder.childRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        commentViewHolder.childRecyclerView.setAdapter(cocomentAdapter);

        //짧은 클릭 => 대댓글 볼 수 있게 함.
//        final boolean isExpanded = parentList.get(i).isExpanded();
//        commentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                parentList.get(i).setExpanded(!isExpanded);
//                notifyItemChanged(i);
//            }
//        });
        //isExpanded가 참일 때 리사이클러뷰가 나오게함. 거짓이면 사라짐.
//        commentViewHolder.childRecyclerView.setVisibility(isExpanded? View.VISIBLE : View.GONE);

        final String docId = parentList.get(i).getDocUuid();//해당 댓글에 대해서만 UUID를 가져옴.

        //다이얼로그 옵션 리스트
        final String[] meCommentOptions = new String[] {"대댓글 달기", "삭제", "닫기"};
        final String[] otherCommentOptions = new String[] {"대댓글 달기", "닫기"};

        //댓글 경로
        final DocumentReference commentRef = FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("댓글")
                .document(docId);

        /**
         * 댓글을 길게 누를 경우
         * **/
        commentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //내가 작성한 댓글의 경우
               if(parentList.get(i).getCommentWriterId().equals(myUid)){
                    new AlertDialog.Builder(context)
                            .setTitle("내가 쓴 댓글")
                            .setItems(meCommentOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (meCommentOptions[which]){
                                        case "대댓글 달기":
                                            Intent intent = new Intent(context, CocomentActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("commentInfo", parentList.get(i));
                                            intent.putExtra("postingInfo", postingInfo);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);
                                            break;
                                        case "삭제":
                                            //우선 대댓글 삭제해주고
                                            commentRef
                                                    .collection("대댓글")
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            //대댓이 있다면
                                                            if (!queryDocumentSnapshots.getDocuments().isEmpty()){
                                                                for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                                                    commentRef
                                                                            .collection("대댓글")
                                                                            .document(dc.getId())
                                                                            .delete();
                                                                    //대댓글 삭제 후 댓글 삭제
                                                                    commentRef.delete();

                                                                }
                                                            }
                                                            else{
                                                                //대댓글이 없으면 댓글만 삭제
                                                                commentRef.delete();
                                                                notifyItemChanged(i);
                                                            }
                                                        }
                                                    });

                                            break;
                                        case "취소":
                                            dialog.dismiss();
                                    }
                                }
                            }).show();
                }
                else{
                    new AlertDialog.Builder(context)
                            .setTitle("남이 쓴 댓글")
                            .setItems(otherCommentOptions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (otherCommentOptions[which]){
                                        case "대댓글 달기":
                                            Intent intent = new Intent(context, CocomentActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("commentInfo", parentList.get(i));
                                            intent.putExtra("postingInfo", postingInfo);
                                            intent.putExtras(bundle);
                                            context.startActivity(intent);
                                            break;
                                        case "취소":
                                            dialog.dismiss();
                                    }
                                }
                            }).show();
                }

                return false;
            }
        });


        /**
         * 대댓글을 화면에 띄우기 Cocomments Fetch
         * **/
        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("댓글")
                .document(docId)
                .collection("대댓글")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            childList.clear();

                           for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                               CommentInfo commentInfo = dc.toObject(CommentInfo.class);
                               childList.add(commentInfo);
                               cocomentAdapter.notifyItemChanged(childList.size());
                           }

                           cocomentAdapter.notifyDataSetChanged();

                        }
                        else{
                            childList.clear();
                            cocomentAdapter.notifyDataSetChanged();
                        }
                    }
                });

        //댓글 올라오는 애니메이션
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        commentViewHolder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }

    private void moveToMyPage(int i){
        Intent intent = new Intent(context, MyPage.class);
        intent.putExtra("userUUID", parentList.get(i).getCommentWriterId());
        context.startActivity(intent);
    }
}
