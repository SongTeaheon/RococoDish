package com.example.front_ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.front_ui.DataModel.SerializableStoreInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Edit.BroadcastUtils;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.GlidePlaceHolder;
import com.example.front_ui.Utils.LocationUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.ByteArrayOutputStream;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Interface.MyPageDataPass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.rishabhkhanna.customtogglebutton.CustomToggleButton;

//interface for datapass to MyPage
public class MyPage extends AppCompatActivity implements MyPageDataPass {

    private final String TAG = "TAGMyPage";
    int numOfMyPost = 0;
    TextView tvOfNum;
    double currentLatitude;
    double currentLongtitude;
    TextView followerText;
    TextView followingText;
    TextView followerNum;
    TextView followingNum;
    UserInfo userInfo;
    TextView userName;
    ImageView backBtn;
    static final String basicProfile = "https://firebasestorage.googleapis.com/v0/b/rococodish.appspot.com/o/user6.png?alt=media&token=f6f73ce5-bfe1-4dac-bbf2-29fb94706e09";

    @Override
    public void setNumberOfData(int value) {
        Log.d(TAG, "setNumberOfData - callback function");
        numOfMyPost = value;
        if(tvOfNum == null)
            tvOfNum = findViewById(R.id.textNumOfData);
        tvOfNum.setText(Integer.toString(numOfMyPost));
    }

    private int RC_GALLERY = 1;
//    public ImageButton imageButton;
    private ImageView circleImageView;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page);

        //이스터에그로 넘어감.
        TextView textView = findViewById(R.id.textView);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return false;
            }
        });

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /**
         * 이전 창에서 데이터 가져옴.
         * **/
        //현재 위치 정보를 가져온다.
        Intent intent = this.getIntent();
        currentLatitude = SubActivity.mCurrentLocation.getLatitude();
        currentLongtitude = SubActivity.mCurrentLocation.getLongitude();

        //이전 페이지에서 사용자의 uuid를 가져옴.
        final String userUUID = intent.getStringExtra("userUUID");

        //팔로우 글자 누르면 팔로우 리스트 창 이동
        followerText = findViewById(R.id.follow_textview);
        followerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPage.this, FollowActivity.class);
                intent.putExtra("userUUID", userUUID);
                intent.putExtra("pageNum", 0);//바로 팔로워 페이지 보이게
                startActivity(intent);
            }
        });
        //팔로잉 창 누르면 팔로잉 창으로 이동
        followingText = findViewById(R.id.following_textview);
        followingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPage.this, FollowActivity.class);
                intent.putExtra("userUUID", userUUID);
                intent.putExtra("pageNum", 1);// 바로 팔로잉 페이지 보이게
                startActivity(intent);
            }
        });
        //팔로워 숫자 설정
        followerNum = findViewById(R.id.textNumOfFollower);
        followingNum = findViewById(R.id.textNumOfFollowing);

        setNumOfFollow(userUUID, followerNum, "팔로워");
        setNumOfFollow(userUUID, followingNum, "팔로잉");


        //유저 이름 띄우기
        userName = findViewById(R.id.userName);
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists() && documentSnapshot != null){

                            userName.setText(documentSnapshot.get("nickname").toString());
                        }
                    }
                });

        /**
         * 팔로우 버튼 처리
         * **/
        final CustomToggleButton follow = findViewById(R.id.followToggle);
        if(userUUID.equals(FirebaseAuth.getInstance().getUid())){
            follow.setVisibility(View.GONE);
        }
        else{

            //팔로우 화면표시용
            follow.setVisibility(View.INVISIBLE);
            FirebaseFirestore.getInstance().collection("사용자")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .collection("팔로잉")
                    .document(userUUID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                Log.d(TAG, "파이어스토어 '사용자 -> 팔로잉'에 접근합니다.");
                                if (documentSnapshot.getBoolean("팔로우 여부") == true) {
                                    follow.setChecked(true);
                                    Log.d(TAG, "팔로우 버튼 -> 팔로잉 버튼");
                                } else {
                                    follow.setChecked(false);
                                    Log.d(TAG, "팔로우 버튼 변경 X");
                                }
                                follow.setVisibility(View.VISIBLE);
                            }
                            else{
                                follow.setVisibility(View.VISIBLE);
                            }
                        }
                    });
            //팔로우 버튼 누르기 이벤트 처리
            follow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentReference followingRef = FirebaseFirestore.getInstance()
                            .collection("사용자")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("팔로잉")
                            .document(userUUID);
                    DocumentReference followerRef = FirebaseFirestore.getInstance()
                            .collection("사용자")
                            .document(userUUID)
                            .collection("팔로워")
                            .document(FirebaseAuth.getInstance().getUid());

                    if(isChecked){
                        Log.d(TAG, "팔로우 버튼 클릭됨");

                        //본인 사용자 -> 서브컬렉션 팔로잉 -> 도큐먼트 필드 추가
                        followingRef.set(ImmutableMap.of("팔로우 여부", true)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "팔로우 버튼을 눌러서 '사용자->팔로잉' 디비에 업로드 완료");
                            }
                        });

                        //상대방 사용자 -> 서브컬렉션 팔로워 -> 도큐먼트 필드 추가
                        followerRef.set(ImmutableMap.of("팔로워 여부", true)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "팔로우 버튼을 눌러서 '사용자->팔로워' 디비에 업로드 완료");
                            }
                        });
                    }
                    else{
                        Log.d(TAG, "팔로우 버튼 클릭 해제됨");
                        //삭제 부분
                        followingRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "팔로우 버튼 해제해서 '사용자->팔로잉' 디비 삭제 완료");
                            }
                        });
                        followerRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "팔로우 버튼 해제해서 '사용자->팔로워' 디비 삭제 완료");
                            }
                        });
                    }
                }
            });
        }


        MyAdapter adapter = new MyAdapter(
                this,
                R.layout.polar_style,
                this,
                currentLatitude,
                currentLongtitude, userUUID);       // GridView 항목의 레이아웃 row.xml

        GridView gv = findViewById(R.id.gridview);
        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
        TextView tv = findViewById(R.id.textNumOfData);

//        imageButton = (ImageButton) findViewById(R.id.imageView);
        circleImageView = findViewById(R.id.circleImage);
        //킬 때마다 프로필 사진 불러옴
        final ProgressDialog progressDialog = ProgressDialog.show(this, "로딩중", "잠시만 기다려주세요...");

        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        userInfo = documentSnapshot.toObject(UserInfo.class);
                        userInfo.setUid(documentSnapshot.getId());
                        if(documentSnapshot.get("profileImage") != null){//프로필 사진이 있을 경우
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                            circularProgressDrawable.setStrokeCap(Paint.Cap.ROUND);
                            circularProgressDrawable.setCenterRadius(10f);
                            circularProgressDrawable.setBackgroundColor(R.color.colorMainSearch);
                            circularProgressDrawable.start();

                            String path = documentSnapshot.get("profileImage").toString();
                            GlideApp.with(getApplicationContext())
                                    .load(path)
                                    .placeholder(circularProgressDrawable)
                                    .into(circleImageView);
                            progressDialog.dismiss();
                        }
                        else{//프로필 사진이 없을 경우 걍 로딩 없앰.
                            progressDialog.dismiss();
                        }
                    }
                });
        //todo : 타인의 프로필 변경은 막기
        //자신일 경우에만 프로필 변경 가능
        if(userUUID.equals(FirebaseAuth.getInstance().getUid())){
            circleImageView.setOnClickListener(new View.OnClickListener() {
                final String[] profiles = new String[] {"앨범에서 사진 선택", "기본 이미지로 변경"};
                @Override
                public void onClick(final View v) {
                    switch (v.getId()) {
                        case R.id.circleImage :
                            new AlertDialog.Builder(MyPage.this)
                                    .setTitle("프로필")
                                    .setItems(profiles, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (profiles[which]) {
                                                case "앨범에서 사진 선택":
                                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    startActivityForResult(intent, RC_GALLERY);
                                                    break;
                                                case "기본 이미지로 변경":
                                                    FirebaseFirestore.getInstance().collection("사용자")
                                                            .document(userUUID)
                                                            .update("profileImage", basicProfile);
                                                    //todo : 나갔다 들어오면서 이전 액티비티에서 전해오는 userUUID가 NULL이ㄷ 되버리는 에러
                                                    GlideApp.with(getApplicationContext())
                                                            .load(basicProfile)
                                                            .into(circleImageView);
                                                    break;
                                            }
                                        }
                                    })
                                    .show();
                            break;
                    }
                }
            });
        }

    }

    private void setNumOfFollow(String userUUID, final TextView textView, String collectionName){
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .collection(collectionName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(!queryDocumentSnapshots.isEmpty()){

                            textView.setText(String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                        }
                        else{
                            textView.setText("0");
                        }
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //프로필 사진 고를 시 앨범에서 사진 가져올 경우
        if(requestCode == RC_GALLERY && resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImageFromGallery = data.getData();

            UCrop.Options options = new UCrop.Options();
            options.setToolbarWidgetColor(getResources().getColor(R.color.colorWhite));//툴바 글자색
            options.setToolbarColor(getResources().getColor(R.color.MainColor));//툴바 색
            options.setCropFrameColor(getResources().getColor(R.color.MainColor));//테두리
            options.setHideBottomControls(true);

            Uri destinationUri = Uri.fromFile(new File(getApplicationContext().getCacheDir(), "IMG_" + System.currentTimeMillis()));
            UCrop.of(selectedImageFromGallery, destinationUri)
                    .withAspectRatio(1, 1)
                    .withOptions(options)
                    .withMaxResultSize(450, 450)
                    .start(this);
        }

        if(requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            final ProgressDialog progressDialog  = ProgressDialog.show(this, null, "사진을 등록중입니다.");
            progressDialog.setCancelable(false);
            if (resultUri != null) {
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                    byte[] byteArray = outputStream.toByteArray();
                    Storage.INSTANCE.uploadProfileImage(byteArray, progressDialog, new Function1<Uri, Unit>() {
                        @Override
                        public Unit invoke(Uri uri) {
                            //todo : userInfo가 initialize되지 않아서 에러생깁니다.
                            Log.d(TAG, "check Image uri : " + uri.toString() );
                            AlgoliaUtils.changeProfileImagePath(userInfo, uri.toString());
                            return null;
                        }
                    });
                    GlideApp.with(this)
                            .load(bmp)
                            .placeholder(GlidePlaceHolder.circularPlaceHolder(this))
                            .into(circleImageView);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.UnregBrdcastReceiver_posting(this);
    }
}


//TODO: 데이터 불러오는 걸 adapter에서
class MyAdapter extends BaseAdapter {
    private final String TAG = "TAGMyAdapter";
    FirebaseFirestore db;

    Context mContext;
    int layout;
    ArrayList<PostingInfo> list;
    LayoutInflater inf;
    FirebaseStorage storage;
    StorageReference storageReference;
    double currentLatitude;
    double currentLongtitude;
    String userUUID;

    //interface for datapass to MyPage
    private MyPageDataPass mCallback;


    public MyAdapter(Context context, int layout, MyPageDataPass listener, double lat, double lon, String userUUID) {
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        currentLatitude = lat;
        currentLongtitude = lon;
        this.userUUID = userUUID;
        this.mContext = context;
        this.layout = layout;
        mCallback = listener;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        getPostingDataFromCloud(userUUID);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //이미지 세팅
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imagefood);

        final PostingInfo singleItem = list.get(position);
        Log.d(TAG, "downloadImageFromFirebaseStorage : " + singleItem.imagePathInStorage);
        StorageReference fileReference = storage.getReferenceFromUrl(singleItem.imagePathInStorage);
        GlideApp.with(mContext)
                .load(fileReference)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(mContext))
                .into(iv);

//태완태완 이미지 선택시 반응입니다. 여기가 그 각 포스팅1 글 누르면 발생하는 이벤트 부분입니다.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //데이터 수정이 일어난 데이터가 클릭되면 수정된 데이터를 여기에서 반영해야함1!! receive 필터 아이디는 postingId
                LocalBroadcastManager
                        .getInstance(mContext)
                        .registerReceiver(BroadcastUtils.getBrdCastReceiver_posting(singleItem),
                                new IntentFilter(singleItem.getPostingId()));

                final Intent intent = new Intent(mContext, DishView.class);
                //비동기로 객체 가져오기
                db.collection("가게")
                        .document(singleItem.getStoreId())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot document, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    StoreInfo storeInfo = document.toObject(StoreInfo.class);
                                    Log.d(TAG, "storeInfo : " + storeInfo.getName());
                                    SerializableStoreInfo serializableStoreInfo = new SerializableStoreInfo(storeInfo);
                                    double distance = LocationUtil.getDistanceFromMe(currentLatitude, currentLongtitude, storeInfo.getGeoPoint());
                                    intent.putExtra("postingInfo", singleItem);
                                    intent.putExtra("storeInfo", serializableStoreInfo);
                                    intent.putExtra("distance", distance);

                                    mContext.startActivity(intent);

//                                    Toast.makeText(mContext, storeInfo.name, Toast.LENGTH_SHORT).show();

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            }
                        });
            }
        });
        return convertView;
    }

    //내가 쓴 데이터를 모두 가져온다.
    private void getPostingDataFromCloud(String userUUID) {
        Log.d(TAG, "getDataFromFirestore");

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("게시물을 불러오는 중입니다.");
        progressDialog.show();

        db.collection("포스팅")
                .whereEqualTo("writerId", userUUID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, e.getMessage());
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {

                            list.clear();

                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //store 정보를 가져오고, id를 따로 저장한다.
                                PostingInfo postingInfo = document.toObject(PostingInfo.class);
                                //해당 가게 정보의 post데이터를 가져온다.
                                list.add(postingInfo);
                                notifyDataSetChanged();
                            }
                            notifyDataSetChanged();
                            Log.d(TAG, "getPostingData size : " + queryDocumentSnapshots.getDocuments().size());
                            mCallback.setNumberOfData(queryDocumentSnapshots.getDocuments().size());
                        } else {
                            Log.d(TAG, "getPostingData size : " + queryDocumentSnapshots.getDocuments().size());
                            mCallback.setNumberOfData(queryDocumentSnapshots.getDocuments().size());
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}

