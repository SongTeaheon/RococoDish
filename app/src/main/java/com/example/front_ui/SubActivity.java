package com.example.front_ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.AlgoliaTest.StoreNameSearchAcitivity;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.Interface.MyPageDataPass;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.SlidingRootNavLayout;

import java.util.Objects;


public class SubActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final int SEARCH_REQUEST_CODE = 10003;
    private static final int REQUEST_LOCATION = 10002;
    private static final String TAG = "TAGSubActivity";



    private FusedLocationProviderClient mFusedLocationClient;
    RecyclerView my_recycler_view;
    RecyclerView myPage_recyclerview;
    NestedScrollView nestedScrollView;
    private Location mCurrentLocation;
    private Location searchLocation;

    private boolean mLocationPermissionGranted = false;
    TextView myPageTextview;
    private TextView starText;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView search_btn;
    Toolbar pageViewToolbar;
    SlidingRootNav slidingRootNav;
    TextView logOutText;
    TextView userNameText;
    RecyclerViewDataAdapter recyclerViewDataAdapter;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        pageViewToolbar = findViewById(R.id.pageViewToolbar);
        setSupportActionBar(pageViewToolbar);

        /**
         * 네비게이션 드로워 설정들
         * **/
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.activity_sub_drawer)
                .withToolbarMenuToggle(pageViewToolbar)
                .inject();


        /*
        * 임시 기능입니다. 임시가게이름 검색!
        * */
        TextView tv_storeSearch = findViewById(R.id.textview_storesearch);
        tv_storeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivity.this, StoreNameSearchAcitivity.class);
                startActivity(intent);
            }
        });


        //로그아웃 기능
        logOutText = findViewById(R.id.logOut_textview_drawer);
        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //사용자 이름 띄우기
//        userNameText = findViewById(R.id.userName_textview_drawer);
//        userNameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        //마이페이지 글자 누를시 이벤트
        myPageTextview = findViewById(R.id.myPage_textview_activitySub);
        myPageTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MyPage.class);
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


        /**
         * 가게 리사이클러뷰 설정
         * **/


        my_recycler_view = (RecyclerView) findViewById(R.id.mrecyclerView);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        //mCurrentLocation초기화 -> permission안될 경우 때문!!
        mCurrentLocation = new Location("dummyprovider");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission(); //Permission완료해야 recyclerview를 불러온다.(그 전에 불러오면 안되기 때문)


        //마이페이지용 리사이클러 뷰 변수
        myPage_recyclerview = findViewById(R.id.myPage_recyclerview_activitySub);
        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myPage_recyclerview.setAdapter(myPageAdapter);

        //마이페이지 글자 누를시 이벤트
        my_recycler_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                return false;
            }
        });

        //별점평균 누를시 생기는 이벤트
        starText = findViewById(R.id.storeStar);
        starText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //검색 동작.
        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SubActivity.this, MainSearchActivity.class);
                Intent intent = new Intent(SubActivity.this, MainSearchActivity.class);

                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });
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
                                Toast.makeText(getApplicationContext(), "lat : " + mCurrentLocation.getLatitude() +
                                        "\n lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
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
                    getCurrentLocation();
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
    private void initRecyclerView(Location locationCenter) {
        Log.d(TAG, "initRecyclerView");
        my_recycler_view.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, locationCenter);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(recyclerViewDataAdapter);
    }


    @Override
    public void onRefresh() {
        // 새로고침 코드
        getCurrentLocation();

        my_recycler_view.setHasFixedSize(true);
        recyclerViewDataAdapter = new RecyclerViewDataAdapter(this, mCurrentLocation);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(recyclerViewDataAdapter);

        Recyclerview_myPage_Adapter myPageAdapter = new Recyclerview_myPage_Adapter(this);
        myPage_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myPage_recyclerview.setAdapter(myPageAdapter);

        // 새로고침 완료
        recyclerViewDataAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        switch ( resultCode ){
            case RESULT_OK:
                Log.d(TAG, "back from search page ");
                SearchedData item = (SearchedData) data.getSerializableExtra("SearchedData");
                double x = Double.parseDouble(item.x);
                double y = Double.parseDouble(item.y);
                searchLocation = new Location("dummyprovider");
                searchLocation.setLongitude(x);
                searchLocation.setLatitude(y);

                Log.d(TAG, "x, y, name : " + x +" " + y + " " + item.getPlace_name());

                initRecyclerView(searchLocation);
                break;
            case RESULT_CANCELED:
                initRecyclerView(mCurrentLocation);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SubActivity.this, LoginActivity.class));
            finish();
        }
        else{
            userNameText = findViewById(R.id.userName_textview_drawer);
            userNameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
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
}
