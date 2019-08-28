package com.rococodish.front_ui;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rococodish.front_ui.DataModel.CommentInfo;
import com.rococodish.front_ui.DataModel.NoticeInfo;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.FCM.ApiClient;
import com.rococodish.front_ui.FCM.ApiInterface;
import com.rococodish.front_ui.FCM.NotificationModel;
import com.rococodish.front_ui.FCM.RootModel;
import com.rococodish.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CocomentActivity extends AppCompatActivity {

    ImageView commentImage;
    TextView commentName;
    TextView commentDesc;
    TextView commentTime;
    ImageView cocomentImage;
    EditText cocomentEdit;
    TextView writeBtn;
    private String TAG = "TAG_CocomentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocoment);

        commentImage = findViewById(R.id.commentImage_imageview_commentActivity);
        commentName = findViewById(R.id.commentName_tv_commentActivity);
        commentDesc = findViewById(R.id.commentDesc_tv_commentActivity);
        commentTime = findViewById(R.id.commentTime_tv_commentActivity);
        cocomentImage = findViewById(R.id.cocomentMyImage_iv_cocomentActivity);
        cocomentEdit = findViewById(R.id.cocomentDesc_etv_cocomentActivity);
        writeBtn = findViewById(R.id.write_button_cocomentActivity);


        Intent intent = getIntent();
        final CommentInfo commentInfo = (CommentInfo) intent.getExtras().getSerializable("commentInfo");
        final PostingInfo postingInfo = (PostingInfo) intent.getSerializableExtra("postingInfo");

        //대댓글에 내 이미지 등록
        @Nullable final Map<Integer, String> myImagePath = new HashMap<>();//초기화
        @Nullable final Map<Integer, String> myName = new HashMap<>();//초기화
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists()){

                            @Nullable String imagePath = (String) documentSnapshot.getData().get("profileImage");
                            @Nullable String name = (String) documentSnapshot.getData().get("nickname");

                            myImagePath.put(0, imagePath);
                            myName.put(0, name);

                            GlideApp.with(getApplicationContext())
                                    .load(myImagePath.get(0) != null? imagePath : R.drawable.basic_user_image)
                                    .into(cocomentImage);

                        }
                    }
                });

        //받은 댓글정보가 있을 경우에만 실행
        if(commentInfo != null && postingInfo != null){

            //댓글 정보를 가져와서 붙임 & 대댓글에 내 프로필 이미지 적용
            @Nullable final Map<Integer, String> tokenMap = new HashMap<>();//초기화
            setComment(commentInfo, tokenMap);

            //대댓글 작성을 눌렀을 경우 실행
            final String cocomentUuid = UUID.randomUUID().toString();
            writeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(cocomentEdit.getText().toString().trim())){
                        Toast.makeText(CocomentActivity.this, "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        final String cocoment = cocomentEdit.getText().toString();
                        cocomentEdit.getText().clear();

                        FirebaseFirestore.getInstance()
                                .collection("포스팅")
                                .document(postingInfo.postingId)
                                .collection("댓글")
                                .document(commentInfo.getDocUuid())
                                .collection("대댓글")
                                .document(cocomentUuid)
                                .set(new CommentInfo(cocomentUuid,
                                        FirebaseAuth.getInstance().getUid(),
                                        myName.get(0),
                                        myImagePath.get(0),
                                        cocoment,
                                        System.currentTimeMillis(),
                                        false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "대댓글이 업로드되었습니다.");

                                //todo : fcm으로도 보냄
                                if(commentInfo.getCommentWriterId().equals(FirebaseAuth.getInstance().getUid())){
                                    return;
                                }
                                else{
                                    //나 자신한테는 fcm안보냄.
                                    if(tokenMap.get(0) == null){
                                        return;
                                    }
                                    sendFCMCocoment(tokenMap.get(0), cocoment);
                                    sendToNoticeBox(commentInfo.getCommentWriterId(), cocoment, postingInfo);
                                }
                            }
                        });
                        //키보드 자동으로 닫아줌.
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        finish();
                    }
                }
            });

        }
        else{
            Toast.makeText(this, "이미 삭제된 댓글입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void setComment(CommentInfo commentInfo, final Map map){
        //댓글 이미지
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(commentInfo.getCommentWriterId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists()){

                            @Nullable String imagePath = (String) documentSnapshot.get("profileImage");
                            @Nullable String token = (String) documentSnapshot.get("fcmToken");
                            map.put(0, token);

                            //댓글 이미지
                            GlideApp.with(getApplicationContext())
                                    .load(imagePath != null? imagePath : R.drawable.basic_user_image)
                                    .into(commentImage);

                            //댓글 이름
                            commentName.setText(documentSnapshot.get("nickname").toString());
                        }
                    }
                });
        //댓글 내용
        commentDesc.setText(commentInfo.getComment());
        //댓글의 시간
        Long time = commentInfo.getTime();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm:ss");
        String result = dateFormat.format(date);
        commentTime.setText(result);

    }

    private void sendToNoticeBox(String toUUID, String commentDesc, PostingInfo postingInfo) {
        String docId = UUID.randomUUID().toString();
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(toUUID)
                .collection("알림함")
                .document(docId)
                .set(
                        new NoticeInfo(
                                docId,
                                Objects.requireNonNull(FirebaseAuth.getInstance().getUid()),
                                postingInfo.storeName,
                                "대댓글",
                                commentDesc,
                                System.currentTimeMillis(),
                                postingInfo
                        )
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "성공적으로 상대방의 알림함에 댓글 알림이 도착했습니다.");
            }
        });
    }

    public void sendFCMCocoment(String toToken, String desc){
        if(toToken == null){
            Log.d(TAG, "대댓글 상대방의 FCM토큰이 없습니다.");
        }

        RootModel rootModel = new RootModel(toToken, new NotificationModel("댓글에 대댓글이 달렸습니다.", "대댓글 : "+desc, ".Notice.NoticeActivity"));

        Log.d(TAG, "대댓글 토큰 => "+ rootModel.getToken());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        final retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "대댓글 : 성공적으로 Retrofit으로 메시지를 전달했습니다. => ");

                //todo : 보내는 게 성공하면 상대방 알림 보관함에 데이터베이스에 저장만 하면됨.
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "대댓글 : Retrofit으로 메시지를 전달에 실패했습니다. : "+ t.getMessage());

            }
        });
    }
}
