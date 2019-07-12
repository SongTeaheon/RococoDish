package com.example.front_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.firestore.DocumentChange;
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
    private commentAdapterToDishView commentAdapterToDishView;//2. 인터페이스를 변수로 가져옴.

    //3. DishView에서 사용할 수 있는 리스너를 만들어줌.(데이터를 받아야하니까)
    public void getDocIdListener(commentAdapterToDishView _commentAdapterToDishView){
        commentAdapterToDishView = _commentAdapterToDishView;//어댑터에서 보낸 데이터를 다른 변수로 받는다는 소리.
    }


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
        GlideApp.with(context)
                .load(parentList.get(i).getImgPath())
                .into(commentViewHolder.image);

        //시간 부분
        Long time = parentList.get(i).getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 / hh:mm / ss초");
        String result = dateFormat.format(date);
        commentViewHolder.time.setText(result);


        //유저 이름 부분
        commentViewHolder.userName.setText(parentList.get(i).getWriterName());

        //댓글 부분
        commentViewHolder.userComment.setText(parentList.get(i).getComment());


        /**
         * 대댓 처리 부분
         * **/
        //대댓 리사이클러뷰 설정
        final List<CommentInfo> childList = new ArrayList<>();
        //대댓 리사이클러뷰
        final CocomentAdapter cocomentAdapter;
        cocomentAdapter = new CocomentAdapter(context, postingInfo, childList);
        commentViewHolder.childRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        commentViewHolder.childRecyclerView.setAdapter(cocomentAdapter);

        //짧은 클릭 => 대댓글 볼 수 있게 함.
        final boolean isExpanded = parentList.get(i).isExpanded();
        commentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentList.get(i).setExpanded(!isExpanded);
                notifyItemChanged(i);
            }
        });
        //isExpanded가 참일 때 리사이클러뷰가 나오게함. 거짓이면 사라짐.
        commentViewHolder.childRecyclerView.setVisibility(isExpanded? View.VISIBLE : View.GONE);


        //긴 클릭 => 대댓 적을 수 있게함.(여기선 파이어스토어에 업로드만 함. 패치는 DishView에서 실시간으로 하면 자동으로 추가됨.)
        final String docId = parentList.get(i).getDocUuid();//해당 댓글에 대해서만 UUID를 가져옴.
        commentViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //댓글 서브컬렉션의 서브컬렉션 만들기
                //TODO: 글을 작성할 수 있는 부분 설정
                //TODO : 작성 버튼을 누를시 상단 docId를 통해 파이어스토어에 저장.
                new AlertDialog.Builder(context)
                        .setMessage("대댓을 작성하시겠습니까?")
                        .setPositiveButton("작성", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //4. 특정 시점에서 인터페이스 내 메서드를 실행.(데이터를 보냄.)
                                //인터페이스 자체를 변수로 가져와서 그 안에 메서드를 실행, 파라미터(보낼 데이터)로 댓글 도큐먼트 아이디 가져옴.
                                commentAdapterToDishView.sendGetCommentDocId(docId);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        //대댓글 가져오기
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
                        if(queryDocumentSnapshots != null){

                            for(DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                //대댓이 있는 경우만 불러옴.
                                if(dc.getDocument().exists()){

                                    //데이터가 추가되는 경우만 고려함.
                                    switch (dc.getType()){
                                        case ADDED:
                                            CommentInfo commentInfo = dc.getDocument().toObject(CommentInfo.class);
                                            childList.add(commentInfo);
                                            cocomentAdapter.notifyItemChanged(childList.size());
                                    }
                                }
                            }

                        }
                    }
                });

//        //대댓 더미 데이터
//        childList.add(new CommentInfo("", "", "유저1", "R.drawable.basic_user_image", "안녕 난 대댓이야", 0, false));
//        childList.add(new CommentInfo("", "", "유저2", "R.drawable.basic_user_image", "안녕 난 대댓이야", 0, false));
//        childList.add(new CommentInfo("", "", "유저3", "R.drawable.basic_user_image", "안녕 난 대댓이야", 0, false));

        //댓글 올라오는 애니메이션
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        commentViewHolder.itemView.startAnimation(animation);


    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }
}
