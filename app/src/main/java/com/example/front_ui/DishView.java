package com.example.front_ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.SerializableStoreInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.KotlinCode.PostToMyPage;
import com.example.front_ui.Utils.DishViewUtils;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.MathUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class DishView extends AppCompatActivity {

    private final String TAG = "TAGDishView";
    TextView deleteButton;
    TextView editButton;
    TextView tvAddress;
    ImageView backButton;
    TextView tvStoreName;



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
    String myProfilePath;
    EditText commentEdit;
    ImageView commentSend;
    ArrayList<CommentInfo> commentList = new ArrayList<>();
    final String myUid = FirebaseAuth.getInstance().getUid();
    TextView hashTagText;
    TextView descText;
    TextView tvScore;
    TextView tvDistance;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        mContext = this;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        imageView = (ImageView) findViewById(R.id.imageView1);


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
         LastShareFragment 에서 singleitem 을 받음
         * **/
        //LastFragmentShare에서 받은 아이템 정보를 갖고옴.
        Intent intent = this.getIntent();

        postingInfo = (PostingInfo)intent.getSerializableExtra("postingInfo");
        Log.d(TAG, "posting Info description " + postingInfo.description +"storage path " + postingInfo.imagePathInStorage
        + " storeId : " + postingInfo.getStoreId() +" postingid : " + postingInfo.postingId);
        storeInfo = (SerializableStoreInfo)intent.getSerializableExtra("storeInfo");
        Log.d(TAG, "store Info id : " + storeInfo.getStoreId() + " store name : " + storeInfo.getName() + " store map :" + storeInfo.getLat()+", "+storeInfo.getLon() +
                " star :  " + storeInfo.getAver_star());
        double distance = (double)intent.getDoubleExtra("distance", 0.0);
        Log.d(TAG, "거리(미터단위) : " + distance);

        /**
         지도로 넘어가기
         **/
        tvAddress = findViewById(R.id.textViewAddress);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "show the kakao map");
                Toast.makeText(getApplicationContext(), "address is clicked", Toast.LENGTH_LONG).show();
                //daummaps://place?id=7813422 이거 할라면 게시글 쓸 때,
                //daummaps://look?p=37.537229,127.005515
                //daummaps://search?q=맛집&p=37.537229,127.005515이걸로 안될라나
                String url = "daummaps://search?q=맛집&p=37.537229,127.005515";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        /**
         해쉬태그
         **/
        //TextView hashTag = (TextView) findViewById(R.id.hashTag_textView_dishView);
        //해쉬태그가 있을 경우에만 실행
       // if(postingInfo.hashTags != null){
        //    setTags(hashTag, postingInfo.hashTags);
       // }
        hashTagText = findViewById(R.id.hashTag_textview_dishView);
        if(postingInfo.hashTags != null){
            setTags(hashTagText, postingInfo.hashTags);
        }
        else{
            setTags(hashTagText, "태그가 없습니다.");
        }



        /**
         * 게시물 내용 설정
         * **/
        descText = findViewById(R.id.desc_textview_dishView);
        if(postingInfo.description != null){
            descText.setText(postingInfo.description);
        }
        else{
            descText.setText("게시물 내용이 없습니다.");
        }
        tvStoreName = findViewById(R.id.tvStore);
        tvStoreName.setText(storeInfo.getName());
        tvAddress.setText(storeInfo.getAddress());
        tvScore = findViewById(R.id.textViewScore);
        tvScore.setText(Double.toString(postingInfo.getAver_star()));
        tvDistance = findViewById(R.id.textDistance);

        tvDistance.setText(MathUtil.adjustedDistance(distance)+"!");




        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        //우측 상단의 삭제 버튼을 누를 경우 디비를 삭제시킴(포스팅과 포스팅채널 둘다)
        /*
        * 삭제 및 수정 버튼 처리
        * */
        if(postingInfo.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Log.d(TAG, "내 게시물!!!");
            deleteButton = findViewById(R.id.delete_button);
            editButton = findViewById(R.id.edit_button);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "delete button clicked");
                    Toast.makeText(getApplicationContext(), "삭제하시겠습니까? 버튼 추가하", Toast.LENGTH_LONG).show();

                    String storeId = postingInfo.getStoreId();
                    String postingId = postingInfo.getPostingId();
                    String imagePath = postingInfo.getImagePathInStorage();
                    double postingAverStar = postingInfo.getAver_star();
                    DishViewUtils.deletePosting(mContext,  db, storage, storeId, postingId, imagePath, postingAverStar);

                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "edit button clicked");
                    Toast.makeText(getApplicationContext(), "edit button clicked", Toast.LENGTH_LONG).show();
                }
            });
        }
        /**
         * 포스팅 작성자의 프로필 정보(이미지, 이름) 불러오기 + 우측 상단에 띄우기
         * **/
        profileImage = findViewById(R.id.profile_imageview_dishView);
        profileName = findViewById(R.id.profileNam_dishView);

        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(postingInfo.writerId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //프로필 이미지 업로드
                        userImage = (String) document.get("profileImage");

                        if(userImage != null){
                            GlideApp.with(getApplicationContext())
                                    .load(userImage)
                                    .into(profileImage);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "프로필 이미지가 없네요", Toast.LENGTH_SHORT).show();
                            GlideApp.with(getApplicationContext())
                                    .load(R.drawable.basic_user_image)
                                    .into(profileImage);
                        }
                        //프로필 이름 업로드
                        String userName = (String) document.get("nickname");
                        profileName.setText(userName);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        /**
         * 작성자의 프로필을 클릭시 작성자 마이페이지 이동 + postingInfo 다음 액티비티로 넘겨주기
         * **/

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DishView.this, PostToMyPage.class);
                final Bundle bundle = new Bundle();
                bundle.putSerializable("allPostingInfo", postingInfo);
                intent.putExtra("writerImage", userImage);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        /**
         * 좋아요 기능 구현
         * */
        likeFunc();


        /**
         * 하단에 질문과 답 리사이클러뷰 설정
         * **/
        commentRecyclerviewInit();

        /**
         * 댓글 부분 질문 작성 부분
         * */
        //좌측 내 프로필 이미지 넣기
        commentProfile = (ImageView) findViewById(R.id.myProfile_commentInput);
        commentEdit = (EditText) findViewById(R.id.comment_edittext_commentInput);
        commentSend = (ImageView) findViewById(R.id.send_imageview_commentInput);

        final CollectionReference commentRef = FirebaseFirestore.getInstance().collection("포스팅")
                .document(postingInfo.postingId).collection("댓글");


        //하단 댓글 입력칸 좌측에 프로필 사진 넣기
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        assert documentSnapshot != null;

                        myProfilePath = documentSnapshot.get("profileImage").toString();

                        GlideApp.with(getApplicationContext())
                                .load(myProfilePath)
                                .into(commentProfile);

                        //내 프로필 사진 가져온 후 댓글 달기 가능
                        uploadComment(commentRef, myProfilePath);

                    }
                });

        //실시간 댓글 가져오기
        realTimeFetchComment(commentRef);


    }
    public void realTimeFetchComment(CollectionReference commentRef){
        //실시간 댓글 가져오기
        commentRef
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        assert e != null;
                        assert queryDocumentSnapshots != null;

                        for(DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()){
                            switch (snapshot.getType()){
                                case ADDED:
                                    String imagePath = snapshot.getDocument().getData().get("imgPath").toString();
                                    String question = snapshot.getDocument().getData().get("question").toString();
                                    String answer = snapshot.getDocument().getData().get("answer").toString();

                                    commentList.add(new CommentInfo(myUid, imagePath, question, answer, 0));
                                    commentAdapter.notifyItemChanged(commentList.size()+1);
                            }
                        }
                    }
                });

    }

    public void uploadComment(final CollectionReference commentRef,
                              final String profilePath){
        //댓글 전송 시 리사이클러뷰 업로드


        //댓글 업로드하기
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentDesc = commentEdit.getText().toString();
                commentEdit.getText().clear();

                commentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final String documentId = UUID.randomUUID().toString();
                        final long commentTime = System.currentTimeMillis();
                        commentRef.document(documentId).set(new CommentInfo(myUid, profilePath, commentDesc, "답변이 아직 없습니다.", commentTime));
                    }
                });

            }
        });
    }

    public void likeFunc(){
        //포스팅 -> 컬렉션 좋아요 -> 좋아요 한 사람의 uid 도큐먼트 -> isLiked를 bool값으로 true일 경우 빨간색 아예 도큐먼트가 없을 경우는 빈 하트
        final ImageView likeImage = findViewById(R.id.like_imageview_dishView);
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

                if(documentSnapshot.exists()){

                    int img = documentSnapshot.getBoolean("isLiked")? R.drawable.ic_like : R.mipmap.ic_grey_heart;
                    likeImage.setImageResource(img);

                    isLiked = documentSnapshot.getBoolean("isLiked");
                    likeClick(likeImage, likeRef, isLiked);

                }
                //완전 처음 게시물에 들어갔을 경우(좋아요 부분을 디비에 만들어야함.)
                else{

                    HashMap map = new HashMap();
                    map.put("isLiked", false);
                    likeRef.set(map);

                    likeClick(likeImage, likeRef, false);//오류 수정함
                }
            }
        });
    }

    //좋아요 클릭부분만
    public void likeClick(final ImageView likeImage,
                          final DocumentReference likeRef,
                          final boolean isLiked){

        //클릭에 따른 좋아요 색 변경
        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //한번더 클릭하면 기존의 값과 반대로 바꿈.
                boolean _isLiked = !isLiked;
                likeImage.setImageResource(_isLiked? R.drawable.ic_like : R.mipmap.ic_grey_heart);
                //이미지 바꾸고 디비 업데이트함.
                likeRef.update("isLiked", _isLiked);
            }
        });
    }
    //댓글 부분 리사이클러뷰 설정
    public void commentRecyclerviewInit(){
//        Long longTime = System.currentTimeMillis();
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
//        commentList.add(new CommentInfo(postingInfo.imagePathInStorage, "질문입니다.", "답변입니다.",longTime));
        commentAdapter = new CommentAdapter(this, commentList);
        commentRecy = findViewById(R.id.comment_recyclerview);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        commentRecy.setLayoutManager(lm);
        commentRecy.setAdapter(commentAdapter);
    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
    //디비에 있는 "해쉬태그" 정보를 받아서 #부분만 색깔을 칠해주는 메서드(무조건 #이 있는 스트링만 받아야함.)
    //추가로 해쉬태그 누를시 이벤트도 생성 가능
    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d(TAG, "현재 누른 태그 = " + tag);
                            Toast.makeText(DishView.this, "태그 = "+tag, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color = #80FF909A 옅은 색
                            ds.setColor(Color.parseColor("#FF6E6E"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }
}

