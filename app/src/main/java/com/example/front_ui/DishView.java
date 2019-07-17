package com.example.front_ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.SerializableStoreInfo;
import com.example.front_ui.Utils.DeleteUtils;
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
import java.util.List;
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
    List<CommentInfo> commentList = new ArrayList<>();
    final String myUid = FirebaseAuth.getInstance().getUid();
    final String myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    String myProfileImgPath;
    TextView hashTagText;
    TextView tvScore;
    TextView tvDistance;
    EditText commentEdit;//댓글 작성창
    ImageView commentSend;//댓글 업로드 버튼
    EditText cocomentEditText;//대댓글 작성창
    ImageView cocomentSend;//대댓글 업로드 버튼

    DishViewProfileImgPass dishViewProfileImgPass;

    //프로필 경로 받는 리스너
    public void OnProfileImgGetListener(DishViewProfileImgPass _dishViewProfileImgPass){
        dishViewProfileImgPass = _dishViewProfileImgPass;
    }

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
        Log.d(TAG, "postingID : " + postingInfo.getPostingId());
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
                String storeName = storeInfo.getName();
//                String url = "daummaps://search?q="+storeName+"&p=37.537229,127.005515";
                String url;
                if(storeInfo.getKakaoId() != null){
                    url = "daummaps://place?id=" + storeInfo.getKakaoId();
                }else{
                    url = "daummaps://look?p="+storeInfo.getLat() +","+storeInfo.getLon();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        /**
         해쉬태그
         **/
        hashTagText = findViewById(R.id.hashTag_textview_dishView);
        if(postingInfo.hashTags != null){
            setTags(hashTagText, postingInfo.hashTags);
        }
        else{
            setTags(hashTagText, "게시물 내용이 없습니다.");
        }



        /**
         * 게시물 내용 설정
         * **/
//        descText = findViewById(R.id.desc_textview_dishView);
//        if(postingInfo.description != null){
//            descText.setText(postingInfo.description);
//        }
//        else{
//            descText.setText("게시물 내용이 없습니다.");
//        }

        tvStoreName = findViewById(R.id.tvStore);
        tvStoreName.setText(storeInfo.getName());
        tvAddress.setText(storeInfo.getAddress());
        tvScore = findViewById(R.id.textViewScore);
        tvScore.setText(Double.toString(postingInfo.getAver_star()));
        tvDistance = findViewById(R.id.textDistance);

        tvDistance.setText(MathUtil.adjustedDistance(distance)+"!");




        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        /**
        * 게시물 삭제 및 수정 버튼 처리
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
                                    DeleteUtils.deletePosting(mContext,  db, storage, storeId, postingId, imagePath, postingAverStar);
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
//                final Intent intent = new Intent(DishView.this, PostToMyPage.class);
//                final Bundle bundle = new Bundle();
//                bundle.putSerializable("allPostingInfo", postingInfo);
//                intent.putExtra("writerImage", userImage);
//                intent.putExtras(bundle);
                //TODO: 기존에 있던 PostToMyPage를 MyPage하나로 통합
                Intent intent = new Intent(DishView.this, MyPage.class);
                intent.putExtra("userUUID", postingInfo.writerId);
                Log.d(TAG, "들어왔씁니다.");
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

                        String profileImagePath = documentSnapshot.get("profileImage").toString();

                        //여기서  내 프로필 이미지를 모든 구간에 배포함.(팔요할 때 리스너로 받으면 됨.)
                        dishViewProfileImgPass.passProfileImgPath(profileImagePath);

                        if(profileImagePath != null){
                            GlideApp.with(getApplicationContext())
                                    .load(documentSnapshot.get("profileImage").toString())
                                    .into(commentProfile);

                            //내 프로필 사진 가져온 후 댓글 달기 가능
                            uploadComment(commentRef, profileImagePath);
                        }


                    }
                });
//        //댓글 업로드 시 내 프로필 사진 적용
        OnProfileImgGetListener(new DishViewProfileImgPass() {
            @Override
            public void passProfileImgPath(String imgPath) {

                myProfileImgPath = imgPath;//대댓에 들어가는 용도(전역변수로 해서 들어감. 리스너 안에 리스너안에서 파이어스토어 업로드가 작동을 안해서 전역변수로 접근함.)

                if(imgPath != null){
                    GlideApp.with(getApplicationContext())
                            .load(imgPath)
                            .into(commentProfile);

                    uploadComment(commentRef, imgPath);
                }
                else{
                    Log.d(TAG, "프로필경로가 없습니다.");
                }
            }
        });

        //실시간 댓글 가져오기
        realTimeFetchComment(commentRef);


        /**
         * 대댓 처리부분
         * **/
        cocomentEditText = findViewById(R.id.cocoment_edittext_cocommentInput);//대댓 작성창
        cocomentSend = findViewById(R.id.send_imageview_cocomentInput);//대댓 작성 버튼

        //아래 링크는 어댑터 클래스와 액티비티간 상호작용을 위한 인터페이스 사용법을 다룸.
        //https://stackoverflow.com/questions/32720702/how-to-call-a-mainactivity-method-from-viewholder-in-recyclerview-adapter
        //위 사용법을 활용해서 어댑터에서 특정 행위가 생기면 액티비티로 데이터를 보내주는 거 구현

        //5. 데이터를 받고자 하는 액티비티에서 리스너를 실행
        commentAdapter.getDocIdListener(new commentAdapterToDishView() {
            @Override
            public void sendGetCommentDocId(final String docId) {

                //만약 도큐먼트 아이디를 제대로 받았다면 대댓작성하게 UI를 변경한다.
                if(docId != null){
                    //대댓 작성창과 이미지를 보이게 하고, 기존 댓글 작성창과 이미지를 없앤다.
                    cocomentEditText.setVisibility(View.VISIBLE);
                    cocomentSend.setVisibility(View.VISIBLE);
                    commentEdit.setVisibility(View.GONE);
                    commentSend.setVisibility(View.GONE);

                    //AlertDialog에서 작성버튼 누르면 키보드 올라가게 함.
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    //TODO: 대댓글 취소할시 키보드가 내려가면 댓글작성으로 바뀌어야함.
                    //대댓글 쓰려다가 취소버튼 누를 경우


                    //변경한 UI에서 작성을 누르면 업로드를 시작
                    cocomentSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(cocomentEditText.getText().toString().isEmpty()){
                                Toast.makeText(DishView.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                            }
                            //글자를 넣었을 경우엔 이제 글을 파이어스토어에 업로드한다.
                            else{
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);//키보드 내려줌.

                                String cocoment = cocomentEditText.getText().toString();//대댓 내용
                                cocomentEditText.getText().clear();//대댓쓴걸 지워준다.

                                //댓글 컬렉션 안에 서브컬렉션 대댓글 안에 필드로 넣음.
                                String uuid = UUID.randomUUID().toString();

                                //UI 원래상태로 돌림.
                                commentEdit.setVisibility(View.VISIBLE);
                                cocomentEditText.setVisibility(View.GONE);
                                commentSend.setVisibility(View.VISIBLE);
                                cocomentSend.setVisibility(View.GONE);

                                //TODO : 대댓글에서 프로필 이미지 처리
                                commentRef
                                        .document(docId)
                                        .collection("대댓글")
                                        .document(uuid)
                                        .set(new CommentInfo(
                                                uuid,
                                                myUid,
                                                myName,
                                                myProfileImgPath,
                                                cocoment,
                                                System.currentTimeMillis(),
                                                false));
                            }
                        }
                    });
                }
                //데이터 패치는 뷰홀더에서 처리함. 댓글에 대해 각각 해야해서 뷰홀더안에서 해야함.
            }
        });

    }

    public void realTimeFetchComment(CollectionReference commentRef){
        //실시간 댓글 가져오기
        commentRef
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        assert queryDocumentSnapshots != null;

                        for(DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()){
                            switch (snapshot.getType()){
                                case ADDED:
                                    CommentInfo commentInfo = snapshot.getDocument().toObject(CommentInfo.class);
                                    commentList.add(commentInfo);
                                    commentAdapter.notifyItemChanged(commentList.size()+1);
                            }
                        }
                    }
                });

    }

    public void uploadComment(final CollectionReference commentRef,
                              final String profilePath){

        //댓글 업로드하기
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(commentEdit.getText().toString().isEmpty()){
                    Toast.makeText(DishView.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
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

                }
            }
        });
    }

    //좋아요 기능(클릭 함수까지 포함.)
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

                    int img = documentSnapshot.getBoolean("isLiked")? R.mipmap.ic_heart : R.mipmap.ic_grey_heart;
                    likeImage.setImageResource(img);

                    isLiked = documentSnapshot.getBoolean("isLiked");
                    likeClick(likeImage, likeRef, isLiked);

                }
                //완전 처음 게시물에 들어갔을 경우(좋아요 부분을 디비에 만들어야함.)
                else{
                    FirebaseFirestore.getInstance().collection("포스팅")
                            .document(postingInfo.postingId)
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    assert documentSnapshot != null;

                                    if(documentSnapshot.exists()){
                                        HashMap map = new HashMap();
                                        map.put("isLiked", false);
                                        likeRef.set(map);


                                        likeClick(likeImage, likeRef, false);
                                    }
                                }
                            });
                }
            }
        });
        //좋아요 개수 보여주기
        final TextView likesText = findViewById(R.id.likeNum_textview_dishView);

        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("좋아요")
                .whereEqualTo("isLiked", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        assert queryDocumentSnapshots != null;
                        int likes = queryDocumentSnapshots.getDocuments().size();
                        likesText.setText("좋아하는 사람 "+likes+ "명");

                        //TODO : 들어올 때 좋아요 개수 업데이트보다 나갈 때 좋아요 개수 업데이트가 좋지 않을까....근데 어렵다...
                        //좋아요 개수 디비에 업데이트트
                       FirebaseFirestore.getInstance()
                                .collection("포스팅")
                                .document(postingInfo.postingId)
                                .update("numLike", likes)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "좋아요 개수를 디비에 업데이트했습니다.");
                                    }
                                });
                    }
                });
    }

    //좋아요 클릭부분만
    public void likeClick(final ImageView likeImage,
                          final DocumentReference likeRef,
                          final boolean isLiked){

        //좋아요 애니메이션 적용부분
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        //likeImage가 어떤 drawable을 가질 때마다 애니메이션을 적용해줌.(결국 이미지가 변경될 때마다 bounce애니메이션 적용함.)
        if(likeImage.getDrawable().equals(R.drawable.ic_like)){
            likeImage.startAnimation(scaleAnimation);
        }
        else {
            likeImage.startAnimation(scaleAnimation);
        }


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
    public void commentRecyclerviewInit(PostingInfo postingInfo){
        commentAdapter = new CommentAdapter(this, commentList, postingInfo);
        commentRecy = findViewById(R.id.comment_recyclerview);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        commentRecy.setLayoutManager(lm);
//        ((SimpleItemAnimator) commentRecy.getItemAnimator()).setSupportsChangeAnimations(false);
        commentRecy.setAdapter(commentAdapter);
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
                            Toast.makeText(DishView.this, "누르면 이 태그로 검색 ->"+tag, Toast.LENGTH_SHORT).show();
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

