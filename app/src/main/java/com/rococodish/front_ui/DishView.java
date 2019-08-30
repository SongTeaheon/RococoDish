package com.rococodish.front_ui;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;
import com.rococodish.front_ui.DataModel.CommentInfo;
import com.rococodish.front_ui.DataModel.NoticeInfo;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.SerializableStoreInfo;
import com.rococodish.front_ui.Edit.EditActivity;
import com.rococodish.front_ui.FCM.ApiClient;
import com.rococodish.front_ui.FCM.ApiInterface;
import com.rococodish.front_ui.FCM.NotificationModel;
import com.rococodish.front_ui.FCM.RootModel;
import com.rococodish.front_ui.Utils.DeleteUtils;
import com.rococodish.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.rococodish.front_ui.Utils.KakaoUtils;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DishView extends AppCompatActivity {

    private final String TAG = "TAGDishView";
    private final int KAKAOMAP_REQUEST = 1008;

    TextView deleteButton;
    TextView editButton;
    TextView tvAddress;
    ImageView backButton;
    TextView tvStoreName;
    ImageView shareButton;
    TextView postTime;
    HashTagHelper textHashTagHelper;

    ImageView imageView;
    FirebaseFirestore db;
    FirebaseStorage storage;
    PostingInfo postingInfo;
    SerializableStoreInfo storeInfo;

    ImageView profileImage;
    TextView profileName;
    @Nullable
    String userImage;
    Context mContext;
    RecyclerView commentRecy;
    CommentAdapter commentAdapter;
    Boolean isLiked;
    ImageView commentProfile;
    List<CommentInfo> commentList = new ArrayList<>();
    final String myUid = FirebaseAuth.getInstance().getUid();
    final String myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    TextView hashTagText;
    TextView tvScore;
    TextView tvDistance;
    TextView hasNoComments;
    EditText commentEdit;//댓글 작성창
    ImageView commentSend;//댓글 업로드 버튼
    int gloabal_like;
    DishViewLikeNumPass dishViewLikeNumPass;

    ListenerRegistration listenerRegistration_comment;
    EventListener<QuerySnapshot> eventListener_comment;


    //좋아요 개수 받는 리스너
    public void OnLikeNumListener(DishViewLikeNumPass _dishViewLikeNumPass) {
        dishViewLikeNumPass = _dishViewLikeNumPass;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        mContext = this;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageView = (ImageView) findViewById(R.id.iv_main);

        /*
         * backbutton 뒤로가기 버튼
         * */
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /**
         앱 내 직전 뷰 에서 singleitem 을 받음
         또는
         카카오 메시지를 통해서 들어올 수 있음.
         * **/
        //LastFragmentShare에서 받은 아이템 정보를 갖고옴.
        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "intent is null");
        }

        //dist
        postingInfo = (PostingInfo) intent.getSerializableExtra("postingInfo");
        Log.d(TAG, "postingID : " + postingInfo.getPostingId());
        Log.d(TAG, "posting Info description " + postingInfo.description + "storage path " + postingInfo.imagePathInStorage
                + " storeId : " + postingInfo.getStoreId() + " postingid : " + postingInfo.postingId);
        storeInfo = (SerializableStoreInfo) intent.getSerializableExtra("storeInfo");
        Log.d(TAG, "store Info id : " + storeInfo.getStoreId() + " store name : " + storeInfo.getName() + " store map :" + storeInfo.getLat() + ", " + storeInfo.getLon() +
                " star :  " + storeInfo.getAver_star());

        //게시물 시간 설정
        postTime = findViewById(R.id.tv_Day);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        postTime.setText(transFormat.format(postingInfo.getPostingTime()));


        /*
         * 수정 사항 반영1(Local Broad Cast) : 수정 버튼을 통해 수정된 사항이 있으면 받는다.
         * */
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(postingInfo.getPostingId()));

        /**
         지도로 넘어가기
         **/
        tvAddress = findViewById(R.id.tv_address);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KakaoUtils.OpenKakaoMap(mContext, storeInfo.getKakaoId(), storeInfo.getLat(), storeInfo.getLon());
            }
        });
        /**
         해쉬태그
         **/

        hashTagText = findViewById(R.id.tv_description);
        if (postingInfo.hashTags != null) {
            hashTagText.setText(postingInfo.hashTags);
            textHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.MainColor), new HashTagHelper.OnHashTagClickListener() {
                @Override
                public void onHashTagClicked(String hashTag) {
                    Toast.makeText(getApplicationContext(), hashTag, Toast.LENGTH_SHORT).show();
                }
            });
            textHashTagHelper.handle(hashTagText);
        } else {
            hashTagText.setText("게시물 내용이 없습니다.");
        }

        tvStoreName = findViewById(R.id.tv_storeName);
        tvStoreName.setText(storeInfo.getName());
        tvStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StorePageActivity.class);

                Log.d(TAG, "name : " + storeInfo.getName() + " averStar : " + storeInfo.getAver_star() + " docId : " + storeInfo.getStoreId());
                intent.putExtra("storeName", storeInfo.getName());
                intent.putExtra("averStar", storeInfo.getAver_star());
                intent.putExtra("documentId", storeInfo.getStoreId());

                mContext.startActivity(intent);
            }
        });
        tvAddress.setText(storeInfo.getAddress());
        tvScore = findViewById(R.id.tv_Score);
        tvScore.setText(Double.toString(postingInfo.getAver_star()));
        tvDistance = findViewById(R.id.tv_Distance);
        tvDistance.setText(SubActivity.getDistanceStr(storeInfo.getLat(), storeInfo.getLon()) + "!");


        final StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        //todo : 이미지 클릭 시 확대 창으로 이동
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishView.this, ZoomActivity.class);
                Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        DishView.this,
                        imageView,
                        Objects.requireNonNull(ViewCompat.getTransitionName(imageView))).toBundle();
                intent.putExtra("foodImagePath", postingInfo.imagePathInStorage);
                startActivity(intent, options);
            }
        });

        /**
         * 게시물 삭제 및 수정 버튼 처리
         * */

        if (postingInfo.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            Log.d(TAG, "내 게시물!!!");
            deleteButton = findViewById(R.id.delete_button);
            editButton = findViewById(R.id.edit_button);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "delete button clicked");
                    new AlertDialog.Builder(DishView.this)
                            .setMessage("정말 삭제하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String storeId = postingInfo.getStoreId();
                                    String postingId = postingInfo.getPostingId();
                                    String imagePath = postingInfo.getImagePathInStorage();
                                    double postingAverStar = postingInfo.getAver_star();
                                    commentList.clear();
                                    Log.d(TAG, "size : " + commentList.size());
                                    commentAdapter.notifyDataSetChanged();
                                    DeleteUtils.deletePosting(mContext, db, storage, storeId, postingInfo, imagePath, postingAverStar);
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "edit button clicked");
                    Toast.makeText(getApplicationContext(), "edit button clicked", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DishView.this, EditActivity.class);
                    intent.putExtra("postingInfo", postingInfo);
                    intent.putExtra("storeInfo", storeInfo);
                    startActivity(intent);
                }
            });
        }

        /*
         * 공유기능
         * */
        shareButton = findViewById(R.id.iv_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String disStr = SubActivity.getDistanceStr(storeInfo.getLat(), storeInfo.getLon());
                Log.d(TAG, "num like " + postingInfo.getNumLike());
                LocationTemplate params = LocationTemplate.newBuilder(storeInfo.getAddress(),
                        ContentObject.newBuilder(storeInfo.getName(),
                                postingInfo.imagePathInStorage,
                                LinkObject.newBuilder()
                                        .setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com")
                                        .build())
                                .setDescrption(postingInfo.getHashTags())
                                .build())
                        .setSocial(SocialObject.newBuilder().setLikeCount(postingInfo.getNumLike()).setCommentCount(0)
                                .setSharedCount(0).setViewCount(0).build())
                        .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                .setAndroidExecutionParams("distance=" + disStr + "&postingId=" + postingInfo.getPostingId() + "&storeId=" + storeInfo.getStoreId())
                                .build()))
                        .setAddressTitle(storeInfo.getName())
                        .build();

                Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                serverCallbackArgs.put("user_id", "sth534@naver.com");
                serverCallbackArgs.put("product_id", "292744");

                KakaoLinkService.getInstance().sendDefault(DishView.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    }
                });
            }
        });

        /**
         * 포스팅 작성자의 프로필 정보(이미지, 이름) 불러오기 + 우측 상단에 띄우기
         * **/
        profileImage = findViewById(R.id.iv_profile);
        profileName = findViewById(R.id.tv_profileName);

        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(postingInfo.writerId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, e.getMessage());
                        }
                        if (documentSnapshot.exists()) {

                            //프로필 이미지 업로드
                            userImage = (String) documentSnapshot.get("profileImage");
                            if (userImage != null) {
                                GlideApp.with(getApplicationContext())
                                        .load(userImage)
                                        .into(profileImage);
                                //프로필 이름 업로드
                                String userName = (String) documentSnapshot.get("nickname");
                                profileName.setText(userName);
                            } else {
                                GlideApp.with(getApplicationContext())
                                        .load(R.drawable.basic_user_image)
                                        .into(profileImage);
                                //프로필 이름 업로드
                                String userName = (String) documentSnapshot.get("nickname");
                                profileName.setText(userName);
                            }


                        }
                    }
                });

        /**
         * 작성자의 프로필을 클릭시 작성자 마이페이지 이동 + postingInfo 다음 액티비티로 넘겨주기
         * **/

        profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishView.this, MyPage.class);
                intent.putExtra("userUUID", postingInfo.writerId);
                Log.d(TAG, "들어왔씁니다.");
                startActivity(intent);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DishView.this, MyPage.class);
                intent.putExtra("userUUID", postingInfo.writerId);
                Log.d(TAG, "들어왔씁니다.");
                startActivity(intent);
            }
        });

        /**
         * 좋아요 기능 구현
         * */
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("게시물을 불러오고 있습니다.");
        progressDialog.show();
        likeFunc();

        /**
         * 하단에 질문과 답 리사이클러뷰 설정
         * **/
        commentRecyclerviewInit(postingInfo);

        /**
         * 댓글 부분 질문 작성 부분
         * */
        //좌측 내 프로필 이미지 넣기
        commentProfile = (ImageView) findViewById(R.id.myProfile_commentInput);
        commentEdit = (EditText) findViewById(R.id.comment_edittext_commentInput);
        commentSend = (ImageView) findViewById(R.id.send_imageview_commentInput);

        final CollectionReference commentRef = FirebaseFirestore.getInstance().collection("포스팅")
                .document(postingInfo.postingId).collection("댓글");


        //하단 댓글 작성창 좌측에 프로필 사진 넣기(댓글이랑 대댓에 다 적용)
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        assert documentSnapshot != null;

                        @Nullable String imagePath = (String) documentSnapshot.get("profileImage");
                        String userName = documentSnapshot.get("nickname").toString();

                        GlideApp.with(getApplicationContext())
                                .load(imagePath != null ? imagePath : R.drawable.basic_user_image)
                                .into(commentProfile);
                        //내 프로필 사진 가져온 후 댓글 달기 가능
                        uploadComment(commentRef, imagePath, userName);


                    }
                });

        //실시간 댓글 가져오기
        hasNoComments = findViewById(R.id.hasNoComments_textview_DishView);
        realTimeFetchComment(commentRef, hasNoComments, progressDialog);


        //아래 링크는 어댑터 클래스와 액티비티간 상호작용을 위한 인터페이스 사용법을 다룸.
        //https://stackoverflow.com/questions/32720702/how-to-call-a-mainactivity-method-from-viewholder-in-recyclerview-adapter
        //위 사용법을 활용해서 어댑터에서 특정 행위가 생기면 액티비티로 데이터를 보내주는 거 구현

        OnLikeNumListener(new DishViewLikeNumPass() {
            @Override
            public void likeNumPass(int likeNum) {
                gloabal_like = likeNum;
            }
        });
    }

    public void realTimeFetchComment(CollectionReference commentRef,
                                     final TextView hasNoComments,
                                     final ProgressDialog progressDialog) {
        //실시간 댓글 가져오기
        eventListener_comment = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, e.getMessage());
                }
                assert queryDocumentSnapshots != null;


                if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                    hasNoComments.setVisibility(View.VISIBLE);//"댓글이 없습니다" 텍스트 보여주기
                } else {
                    hasNoComments.setVisibility(View.GONE);
                }

                //todo : 삭제 자동반영하는 법
                //1. 데이터를 불러올 때 비동기인 addSnapshotListener로 불러와야함.
                //2. for문을 돌려서 데이터를 불러오기 직전에 리스트를 싹다 지워줌.(다시 불러올 때 중복 방지)
                //3. for문이 끝나면 데이터 변경이 있는지 감지하고 비동기로 데이터 변경을 받으니까 자동반영됨.
                commentList.clear();

                for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {

                    CommentInfo commentInfo = dc.toObject(CommentInfo.class);
                    commentList.add(commentInfo);
                    commentAdapter.notifyItemChanged(commentList.size());
                }
                commentAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        };
        listenerRegistration_comment = commentRef
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(eventListener_comment);

    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerRegistration_comment.remove();
    }

    public void uploadComment(final CollectionReference commentRef,
                              @Nullable final String profilePath,
                              final String userName) {

        //댓글 업로드하기
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(commentEdit.getText().toString().trim())) {
                    Toast.makeText(DishView.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    final String commentDesc = commentEdit.getText().toString();
                    commentEdit.getText().clear();

                    //파이어스토어에 업로드
                    commentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final String documentId = UUID.randomUUID().toString();//필드로 저장하기 위해 변수에 값으로 넣음.
                            final long commentTime = System.currentTimeMillis();
                            commentRef.document(documentId)
                                    .set(new CommentInfo(documentId, myUid, myName, profilePath, commentDesc, commentTime, false));
                        }
                    });
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //todo : 댓글을 디비로 보낼 때 푸쉬알림도 같이 전송
                    FirebaseFirestore.getInstance()
                            .collection("사용자")
                            .document(postingInfo.writerId)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.d(TAG, e.getMessage());
                                    }

                                    if (documentSnapshot.exists() && documentSnapshot != null) {

                                        @Nullable String token = (String) documentSnapshot.getData().get("fcmToken");

                                        //token null 에러 방지
                                        if (token == null) {
                                            Log.d(TAG, "fcm_token이 없는 유저입니다.");
                                        } else {
                                            if (postingInfo.writerId.equals(FirebaseAuth.getInstance().getUid())) {
                                                return;
                                            } else {
                                                //자신한테는 fcm안보냄.
                                                sendFcmComment(token,
                                                        commentDesc,
                                                        documentSnapshot.get("uid").toString(),
                                                        profilePath,
                                                        userName);
                                            }
                                        }
                                    }
                                }
                            });

                }
            }
        });
    }

    public void sendToNoticeBox(String toUUID, String commentDesc, String senderImagePath, String userName) {
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
                                senderImagePath,
                                userName,
                                "댓글",
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

    public void sendFcmComment(String token,
                               final String desc,
                               final String senderUid,
                               final String myImagePath,
                               final String userName) {
        RootModel rootModel = new RootModel(
                token,
                new NotificationModel(
                        "게시물에 댓글이 달렸습니다.",
                        "댓글 : " + desc,
                        ".Notice.NoticeActivity"));

        Log.d(TAG, "댓글 토큰 => " + rootModel.getToken());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        final retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "댓글 : 성공적으로 Retrofit으로 메시지를 전달했습니다.");
                    sendToNoticeBox(senderUid, desc, myImagePath, userName);

                }
                else{
                    Log.d(TAG, "댓글 => Retrofit 보내기 에러 : "+ response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "댓글 : Retrofit으로 메시지를 전달에 실패했습니다. : " + t.getMessage());
            }
        });

    }

    //좋아요 기능(클릭 함수까지 포함.)
    public void likeFunc() {

        //포스팅 -> 컬렉션 좋아요 -> 좋아요 한 사람의 uid 도큐먼트 -> isLiked를 bool값으로 true일 경우 빨간색 아예 도큐먼트가 없을 경우는 빈 하트
        final ImageView likeImage = findViewById(R.id.iv_Like);

        //좋아요 했으면 빨간색으로 설정
        final DocumentReference likeRef = FirebaseFirestore.getInstance().collection("포스팅")
                .document(postingInfo.postingId)
                .collection("좋아요")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        //좋아요 여부와 색변경
        likeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                assert documentSnapshot != null;

                if (documentSnapshot.exists()) {

                    int img = documentSnapshot.getBoolean("isLiked") ? R.drawable.ic_heart : R.drawable.ic_grey_heart;
                    likeImage.setImageResource(img);

                    isLiked = documentSnapshot.getBoolean("isLiked");
                    likeClick(likeImage, likeRef, isLiked);

                }
                //완전 처음 게시물에 들어갔을 경우(좋아요 부분을 디비에 만들어야함.)
                else {
                    //삭제되어서 필드가 없을 경우를 제외하곤 좋아요 추가
                    FirebaseFirestore.getInstance()
                            .collection("포스팅")
                            .document(postingInfo.postingId)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    assert documentSnapshot != null;

                                    if (documentSnapshot.exists()) {
                                        likeRef.set(ImmutableMap.of("isLiked", false));

                                        likeClick(likeImage, likeRef, false);
                                    }
                                }
                            });
                }
            }
        });
        //좋아요 개수 보여주기
        final TextView likesText = findViewById(R.id.tv_likeNum);

        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("좋아요")
                .whereEqualTo("isLiked", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        assert queryDocumentSnapshots != null;

                        likesText.setText("좋아하는 사람 " + queryDocumentSnapshots.getDocuments().size() + "명");

                        //좋아요 개수 디비에 업데이트트
                        String like_text = likesText.getText().toString();
                        int likes = Integer.valueOf(like_text.substring(8, like_text.length() - 1));//슬라이싱으로 좋아요 개수만 정수로 가져옴.

                        dishViewLikeNumPass.likeNumPass(likes);

                    }
                });
    }


    //좋아요 클릭부분만
    public void likeClick(final ImageView likeImage,
                          final DocumentReference likeRef,
                          final boolean isLiked) {

        //좋아요 애니메이션 적용부분
        final ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        //likeImage가 어떤 drawable을 가질 때마다 애니메이션을 적용해줌.(결국 이미지가 변경될 때마다 bounce애니메이션 적용함.)


        //클릭에 따른 좋아요 색 변경
        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //한번더 클릭하면 기존의 값과 반대로 바꿈.
                boolean _isLiked = !isLiked;

                if (_isLiked) {
                    likeImage.startAnimation(scaleAnimation);
                }
                likeImage.setImageResource(_isLiked ? R.drawable.ic_heart : R.drawable.ic_grey_heart);
                //이미지 바꾸고 디비 업데이트함.
                likeRef.update("isLiked", _isLiked);
            }
        });
    }

    //댓글 부분 리사이클러뷰 설정
    public void commentRecyclerviewInit(PostingInfo postingInfo) {
        commentAdapter = new CommentAdapter(this, commentList, postingInfo);
        commentRecy = findViewById(R.id.comment_recyclerview);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        commentRecy.setLayoutManager(lm);
//        ((SimpleItemAnimator) commentRecy.getItemAnimator()).setSupportsChangeAnimations(false);
        commentRecy.setAdapter(commentAdapter);
    }


    /*
     * 수정 사항 반영2(Local Broad Cast)
     * */

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // intent ..
            Log.d(TAG, "brdCastRecevie : " + intent.getStringExtra("hashTags"));
            postingInfo.setHashTags(intent.getStringExtra("hashTags"));
            postingInfo.setAver_star(intent.getFloatExtra("aver_star", 0.0f));
            postingInfo.setTag((HashMap) intent.getSerializableExtra("tag"));

            if (postingInfo.hashTags != null) {
                hashTagText.setText(postingInfo.hashTags);
            } else {
                hashTagText.setText("게시물 내용이 없습니다.");
            }
            tvScore.setText(Double.toString(postingInfo.getAver_star()));

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //창이 꺼지기 전 다음 액티비티를 불러올 때 포스팅 도큐먼트 필드에 좋아요 개수 저장
        FirebaseFirestore.getInstance()
                .collection("가게")
                .document(postingInfo.storeId)
                .collection("포스팅채널")
                .document(postingInfo.postingId)
                .update("numLike", gloabal_like)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "좋아요 개수를 디비에 업데이트했습니다.");
                    }
                });

    }
}

