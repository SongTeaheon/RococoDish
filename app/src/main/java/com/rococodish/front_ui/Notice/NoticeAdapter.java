package com.rococodish.front_ui.Notice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rococodish.front_ui.DataModel.NoticeInfo;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.SerializableStoreInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.DishView;
import com.rococodish.front_ui.Firestore;
import com.rococodish.front_ui.FollowActivity;
import com.rococodish.front_ui.R;
import com.rococodish.front_ui.Utils.DataPassUtils;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewholder> {

    String TAG = "TAGNoticeAdapter";
    Context context;
    List<NoticeInfo> list;
    ImageView iv_personImage;
    @Nullable
    TextView tv_storeName;
    TextView tv_typeOfNotification;
    TextView tv_noticeDesc;
    TextView tv_noticeTime;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public NoticeAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        getNoticeData();
    }

    class NoticeViewholder extends RecyclerView.ViewHolder {
        public NoticeViewholder(@NonNull View itemView) {
            super(itemView);
            iv_personImage = itemView.findViewById(R.id.imageViewPeople);
            tv_storeName = itemView.findViewById(R.id.tv_storeNameOfNotice);
            tv_typeOfNotification = itemView.findViewById(R.id.tv_commentOrCocoment);
            tv_noticeDesc = itemView.findViewById(R.id.tv_commentContents);
            tv_noticeTime = itemView.findViewById(R.id.tv_commentTime);
        }
    }

    @NonNull
    @Override
    public NoticeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_page_item, parent, false);
        NoticeViewholder noticeViewholder = new NoticeViewholder(view);
        return noticeViewholder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewholder holder, final int position) {
        //알림 보낸 사람 프로필 이미지 세팅
        GlideApp.with(context)
                .asBitmap()
                .load(list.get(position).getSenderImagePath() != null ?list.get(position).getSenderImagePath() : R.drawable.basic_user_image)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                .error(R.drawable.ic_error_black_24dp)
                .into(iv_personImage);

        //가게 이름 설정
        assert tv_storeName != null;
        tv_storeName.setText(list.get(position).getStoreName());

        //알림 종류
        tv_typeOfNotification.setText(list.get(position).getType());

        //알림 내용
        tv_noticeDesc.setText(list.get(position).getDesc());

        //시간
        Date _date = new Date(list.get(position).getTime());
        SimpleDateFormat _format = new SimpleDateFormat("MM/dd hh:mm:ss");
        String _result = _format.format(_date);
        tv_noticeTime.setText(_result);


        //알림 클릭
        final String noticeType = list.get(position).getType();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeType.equals("팔로우")) {
                    Intent intent = new Intent(context, FollowActivity.class);
                    intent.putExtra("userUUID", FirebaseAuth.getInstance().getUid());
                    intent.putExtra("pageNum", 0);
                    context.startActivity(intent);
                } else {
                    //댓글,대댓글 클릭했을 경우
                    final Intent intent = new Intent(context, DishView.class);
                    @Nullable final PostingInfo postingInfo = list.get(position).getPostingInfo();
                    if (postingInfo != null) {
                        FirebaseFirestore.getInstance()
                                .collection("가게")
                                .document(postingInfo.storeId)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.d(TAG, e.getMessage());
                                        }
                                        if (documentSnapshot.exists()) {
                                            StoreInfo storeInfo = documentSnapshot.toObject(StoreInfo.class);
                                            SerializableStoreInfo serializableStoreInfo = new SerializableStoreInfo(storeInfo);

                                            intent.putExtra("postingInfo", postingInfo);
                                            intent.putExtra("storeInfo", serializableStoreInfo);
                                            context.startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(context, "삭제된 게시물입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    private void getNoticeData() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("알림을 불러오고 있습니다.");
        progressDialog.show();
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("알림함")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            @Nullable QuerySnapshot snapshotList = task.getResult();

                            if (!snapshotList.isEmpty()) {
                                for (DocumentSnapshot dc : snapshotList.getDocuments()) {
                                    NoticeInfo noticeInfo = dc.toObject(NoticeInfo.class);

                                    list.add(noticeInfo);
                                }
                                notifyDataSetChanged();
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "알림함이 비어있습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
