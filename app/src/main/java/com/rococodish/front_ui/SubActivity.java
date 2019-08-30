package com.rococodish.front_ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rococodish.front_ui.AlgoliaTest.StoreNameSearchAcitivity;
import com.rococodish.front_ui.Coupon.CouponActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.Edit.BroadcastUtils;
import com.rococodish.front_ui.Interface.FirebasePredicate;
import com.rococodish.front_ui.Notice.NoticeActivity;
import com.rococodish.front_ui.PostingProcess.MainShareActivity;
import com.rococodish.front_ui.Search.SubSearchPage;
import com.rococodish.front_ui.Utils.DataPassUtils;
import com.rococodish.front_ui.Utils.LocationUtil;
import com.rococodish.front_ui.Utils.MathUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class SubActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final int SEARCH_REQUEST_CODE = 10003;
    private static final int REQUEST_LOCATION = 10002;
    private static final String TAG = "TAGSubActivity";

    private FusedLocationProviderClient mFusedLocationClient;
    RecyclerView main_recyclerview;
//    RecyclerView myPage_recyclerview;
    NestedScrollView nestedScrollView;
    public static Location mCurrentLocation;
//    private Location searchLocation;

    private boolean mLocationPermissionGranted = false;
    TextView myPageTextview;
    private TextView starText;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView search_btn;
    Toolbar pageViewToolbar;
    SlidingRootNav slidingRootNav;
    TextView logOutText;
    TextView userNameText;
    TextView tv_setting;
    TextView tv_qna;
    TextView tv_couponBox;
    TextView tv_noticeBox;
    ImageView iv_feed;
    RecyclerViewDataAdapter recyclerViewDataAdapter;
    FloatingActionButton addPosting;
    FrameLayout loadingFrame;
    ViewPager myPostViewPager;
    SubViewPagerAdapter viewPagerAdapter;

    String SaveMarketVersion;
    String SaveAppVersion;

    TextView whatDoYou;
    public String Apppackage = "com.rococodish.front_ui";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


//        CompareVersion();

//        try {
//            SaveMarketVersion = new getMarketVersion().execute().get();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }

//        /*
//         * 임시 기능입니다. 임시가게이름 검색!
//         * */
//        ImageView tv_storeSearch = findViewById(R.id.iv_mainText);
//        tv_storeSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SubActivity.this, StoreNameSearchAcitivity.class);
//                startActivity(intent);
//            }
//        });
        //피드로 넘어가는 버튼
        iv_feed = findViewById(R.id.iv_mainText);
        iv_feed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "아저씨 왜 안눌리세요");
                Intent intent = new Intent(SubActivity.this, FeedActivity.class);
                startActivity(intent);
            }
        });
       //우측 하단 포스팅 추가 버튼
        addPosting = findViewById(R.id.addPosting_fab_subActivity);
        addPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MainShareActivity.class);
                startActivity(intent);
            }
        });

        //툴바 설정
        pageViewToolbar = findViewById(R.id.pageViewToolbar);
        setSupportActionBar(pageViewToolbar);

        /**
         * 네비게이션 드로워 설정들
         * **/
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.activity_sub_drawer)
                .withToolbarMenuToggle(pageViewToolbar)
                .inject();
        tv_couponBox = findViewById(R.id.couponBox_textview_drawer);
        tv_couponBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, CouponActivity.class);
                startActivity(intent);
            }
        });
        tv_noticeBox = findViewById(R.id.notice_textview_activitySub);
        tv_noticeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubActivity.this, NoticeActivity.class));
            }
        });




        /*
        * 임시 기능입니다. 임시가게이름 검색!
        * */
//        TextView tv_storeSearch = findViewById(R.id.textview_storesearch);
//        tv_storeSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SubActivity.this, StoreNameSearchAcitivity.class);
//                startActivity(intent);
//            }
//        });


        //로그아웃 기능
        logOutText = findViewById(R.id.logOut_textview_drawer);
        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //마이페이지 글자 누를시 이벤트
        myPageTextview = findViewById(R.id.myPage_textview_activitySub);
        myPageTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MyPage.class);
                intent.putExtra("userUUID", FirebaseAuth.getInstance().getUid());
                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                intent.putExtra("longitude", mCurrentLocation.getLongitude());
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        slidingRootNav.closeMenu();
                    }
                }, 1000);//1초 텀두고 메뉴를 닫아줌.(텀을 안두면 intent이동이랑 멤뉴 닫는거랑 겹쳐서 앱 속도 느려짐.)
            }
        });

        tv_setting = findViewById(R.id.setting_textview_activitySub);
        tv_qna = findViewById(R.id.qna_textview_activitySub);

        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SubActivity.this, "추후 예정", Toast.LENGTH_SHORT).show();
            }
        });
        tv_qna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubActivity.this, QnaActivity.class));
            }
        });



        /**
         * 가게 리사이클러뷰 설정
         * **/


        main_recyclerview = (RecyclerView) findViewById(R.id.mrecyclerView);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        //mCurrentLocation초기화 -> permission안될 경우 때문!!
        mCurrentLocation = new Location("dummyprovider");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission(); //Permission완료해야 recyclerview를 불러온다.(그 전에 불러오면 안되기 때문)


//        마이페이지용 리사이클러 뷰 변수
        myPostViewPager = findViewById(R.id.myPage_viewpager_activitySub);
        myPostViewPager.setPageTransformer(true, new Carousel(this));
        viewPagerAdapter = new SubViewPagerAdapter(this);
        myPostViewPager.setAdapter(viewPagerAdapter);
//         myPostViewPager.setOffscreenPageLimit(3);
        myPostViewPager.setClipChildren(false);
        myPostViewPager.setClipToPadding(false);
        myPostViewPager.setCurrentItem(3);
        myPostViewPager.setPadding(40, 0, 40, 0);
//         myPostViewPager.setPageMargin(-300);
        myPostViewPager.setOverScrollMode(2);
        myPostViewPager.setOffscreenPageLimit(4);
        myPostViewPager.setPageMargin(-900);

//        myPage_recyclerview = findViewById(R.id.myPage_recyclerview_activitySub);
//        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
//        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        myPage_recyclerview.setAdapter(myPageAdapter);

//
////        myPage_recyclerview = findViewById(R.id.myPage_recyclerview_activitySub);
////        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
////        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
////        myPage_recyclerview.setAdapter(myPageAdapter);
////
////        //마이페이지 글자 누를시 이벤트
////        main_recyclerview.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
//                return false;
//            }
//        });


        //별점평균 누를시 생기는 이벤트
//        starText = findViewById(R.id.textView_whatdoyoueat);
//        starText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }); todo

        //검색 동작.
        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SubActivity.this, MainSearchActivity.class);
                Intent intent = new Intent(SubActivity.this, SubSearchPage.class);
                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                intent.putExtra("longitude", mCurrentLocation.getLongitude());
                startActivity(intent);
            }
        });

        /*
        * kakao로 데이터가 날아온 경우. dishview로 날아가야함.
        * */
        Intent intent = getIntent();
        if(intent != null){
            //카카오톡 메시지를 통해 온 경우.
            Uri uri = intent.getData();
            if(uri != null) {
                Log.d(TAG, "uriuri : " + uri.toString());

                final String postingId = uri.getQueryParameter("postingId");
                final String storeId = uri.getQueryParameter("storeId");
                String disStr = uri.getQueryParameter("distance");
                disStr = disStr.replace("m", "");
                disStr = disStr.replace("km", "");

                final String disNumStr = disStr;


                getDataWithId("포스팅", postingId, new FirebasePredicate() {
                    @Override
                    public void afterGetData(DocumentSnapshot document) {
                        if (document.exists()) {
                            final PostingInfo postingInfo = document.toObject(PostingInfo.class);
                            getDataWithId("가게", storeId, new FirebasePredicate() {
                                @Override
                                public void afterGetData(DocumentSnapshot document) {
                                    StoreInfo storeInfo = document.toObject(StoreInfo.class);
                                    Double distance = Double.parseDouble(disNumStr);
                                    Intent intent = new Intent(SubActivity.this, DishView.class);
                                    DataPassUtils.makeIntentForData(intent, postingInfo, storeInfo);
                                    SubActivity.this.startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(SubActivity.this, "해당 게시글이 삭제되었습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else {
                Log.e(TAG, "no uri");
            }

        }
    }


    //location permission을 가져온다.
    //permission이 있으면 location을 가져온다.
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission");

        //permission없는 경우 요청, 있는 경우 location을 가져온다.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //permission없는 경우
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            //gps켜져있는지 확인
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            //gps가 꺼져있다면
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                Log.d(TAG, "GPS is disabled");
                mCurrentLocation.setLatitude(37.583816);
                mCurrentLocation.setLongitude(127.058877);
                initRecyclerView(mCurrentLocation);
            }
            //gps가 켜져있다면
            else {
                getCurrentLocation();//위치좌표를 불러온다.
            }
            mLocationPermissionGranted = true;
        }
    }

    //location을 가져오고 recylcer뷰를 실행한다.
    private void getCurrentLocation() {
        Log.d(TAG, "getCurrentLocation");
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mCurrentLocation = task.getResult();
                                Log.d(TAG, "getCurrentLocation - success mCurrentLocation lat : " + mCurrentLocation.getLatitude() + " long : " + mCurrentLocation.getLongitude());
                               Log.d(TAG, "lat : " + mCurrentLocation.getLatitude() +
                                        "\n lng: " + mCurrentLocation.getLongitude());
                                initRecyclerView(mCurrentLocation);//내 위치를 가져오면 그 때 recyclerview 실행
                            } else {
                                Log.e(TAG, "getCurrentLocation Exception" + task.getException());
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    //permission요청 결과
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult : grantResult size" + grantResults.length);
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission허용한 경우
                    //gps켜져있는지 확인
                    final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    //gps가 꺼져있다면
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        Log.d(TAG, "GPS is disabled");
                        mCurrentLocation.setLatitude(37.583816);
                        mCurrentLocation.setLongitude(127.058877);
                        initRecyclerView(mCurrentLocation);
                    }
                    //gps가 켜져있다면
                    else {
                        getCurrentLocation();//위치좌표를 불러온다.
                    }
                } else {
                    //permission거절한 경우
                    Log.d(TAG, "permission is denied");
                    Toast.makeText(getApplicationContext(), "permission is denied", Toast.LENGTH_LONG).show();
                    mCurrentLocation.setLatitude(37.583816);
                    mCurrentLocation.setLongitude(127.058877);
                    initRecyclerView(mCurrentLocation);//확인필요!!!
                }
                return;
            }
        }
    }

    //주변 가게 recyclerviewt세팅!
    public void initRecyclerView(Location locationCenter) {
        //todo :로딩창
        loadingFrame = findViewById(R.id.loadingFrame);
        loadingFrame.setVisibility(View.VISIBLE);

        Log.d(TAG, "initRecyclerView");
        main_recyclerview.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, locationCenter, loadingFrame);
        recyclerViewDataAdapter.setHasStableIds(true); //dataSetChange할 때, blink하는 문제를 해결하기 위해!! getItemId 오버라이드 필요!!
        main_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        main_recyclerview.setAdapter(recyclerViewDataAdapter);
    }


    @Override
    public void onRefresh() {
        // 새로고침 코드
        getCurrentLocation();

        loadingFrame = findViewById(R.id.loadingFrame);

        main_recyclerview.setHasFixedSize(true);
        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, mCurrentLocation, loadingFrame);
        main_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        main_recyclerview.setAdapter(recyclerViewDataAdapter);

//        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
//        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        myPage_recyclerview.setAdapter(myPageAdapter);
        //TODO : 뷰페이저로 수정
        myPostViewPager.removeAllViews();//다 지워주고(중복 방지)
        viewPagerAdapter = new SubViewPagerAdapter(this);//데이터 불러옴
        myPostViewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();//다시 불러온거 업데이트


        // 새로고침 완료
        recyclerViewDataAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SubActivity.this, SignUpActivity.class));
            finish();
        }
        else{
            userNameText = findViewById(R.id.userName_textview_drawer);
            FirebaseFirestore.getInstance()
                    .collection("사용자")
                    .document(FirebaseAuth.getInstance().getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(e != null){
                                Log.d(TAG, e.getMessage());
                            }
                            assert documentSnapshot != null;
                            if(documentSnapshot.exists()){

                                String name = documentSnapshot.get("nickname").toString();
                                userNameText.setText(name);
                            }
                        }
                    });

        }

        //todo : 키자마자 푸쉬알림 토큰을 디비에 업데이트(기기마다 다르므로 매번 해줘야함.)
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "get InstanceId을 가져오는데 실패 : "+ task.getException());
                        }

                        String token = task.getResult().getToken();
                        Log.d(TAG, "토큰 획득 성공 : "+ token);

                        Map tokenMap = new HashMap();
                        tokenMap.put("fcmToken", token);
                        if(FirebaseAuth.getInstance().getUid() == null){
                            return;
                        }
                        FirebaseFirestore.getInstance()
                                .collection("사용자")
                                .document(FirebaseAuth.getInstance().getUid())
                                .update(tokenMap)
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Log.d(TAG, "FCM 토큰을 가져오는데 성공했습니다.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "FCM 토큰을 가져오는데 실패했습니다.");
                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        //드로워가 열려있으면 취소버튼 누르면 닫히고
        if(slidingRootNav.isMenuOpened()){
            slidingRootNav.closeMenu();
        }
        //이미 닫혀있는 상태에서 취소 누르면 종료할 건지 다이얼로그 뜨게함.
        else{
            new AlertDialog.Builder(this)
                    .setMessage("정말 종료하시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.UnregBrdcastReceiver_posting(this);
    }


    private void getDataWithId(String collectionName, String id, final FirebasePredicate predicate){
        Log.d(TAG, "getDataFromFirestore");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        predicate.afterGetData(documentSnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.e(TAG, "해당 데이터가 삭제되었습니다.");
                }
        });
    }

    public static String getDistanceStr(double lat, double lon){
        double distance = LocationUtil.getDistanceFromMe(mCurrentLocation, lat, lon);//내 위치로부터의 거리 측정.
        String distanceStr = MathUtil.adjustedDistance(distance);
        return distanceStr;
    }

    private Boolean isNetWork() {//버전 확인을 위한 네트워크 체크 함수
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        return (isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect);
    }

    private class getMarketVersion extends AsyncTask<String, String, String> {

        String MarketVersion;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String AppFromPlayStore = "https://play.google.com/store/apps/details?id=com.rococodish.front_ui";
                Document doc = Jsoup.connect(AppFromPlayStore).get();

                Elements Version = doc.select(".htlgb ");

                for (int i = 0; i < 10; i++) {
                    MarketVersion = Version.get(i).text();
                    if (Pattern.matches("^[0-9]{1}.[0-9]{1}$", MarketVersion)) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MarketVersion;
        }
    }

    private String getAppVersion() {
        PackageManager pm = getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }

        String currentVersion = pInfo.versionName;

        return currentVersion;
    }


    public void CompareVersion() {
        if (isNetWork()) {
            try {
                SaveMarketVersion = new getMarketVersion().execute().get();
                SaveAppVersion = getAppVersion();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (!SaveMarketVersion.equals(SaveAppVersion)) {
                new AlertDialog.Builder(SubActivity.this)
                        .setMessage("업데이트가 필요합니다.\n업데이트를 해 주세요.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                Uri uri = Uri.parse("market://details?id=" + Apppackage);
                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        } else Toast.makeText(SubActivity.this, "인터넷 연결이 되어있지않아 버전정보를 확인할 수 없음", Toast.LENGTH_SHORT).show();
    }
}
