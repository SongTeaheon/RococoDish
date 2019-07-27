package com.example.front_ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.Utils.MathUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

public class DoubleRecyActivity extends AppCompatActivity{

    String TAG = "TAG_DoubleRecyActivity";

    RecyclerView parentRecy;
    RecyclerView myPostsRecy;

    DoubleRecyAdapter1 doubleRecyAdapter1;
    DoubleRecyMyPostAdapter doubleRecyMyPostAdapter;

    List<StoreInfo> parentList = new ArrayList<>();
    List<PostingInfo> myPostList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    Toolbar toolbar;
    SlidingRootNav drawerView;
    TextView title;
    TextView userName_drawer;
    TextView myPage;

    //거리관련 전역변수
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_recy);

        /**
         * 거리 가져오기
         * **/
        //todo : GPS 설정 여부 확인
        //todo : GPS 허용 권한 여부 확인
        //todo : 거리 불러오기
        //todo : 리사이클러뷰 세팅
        //todo : 가게 목록 불러오기

        //GPS 사용여부 & 허용권한 여부
        if (!checkLocationServicesStatus()) {
            //GPS가 꺼져있으면 키라고 다이얼로그 띄워줌.
            showDialogForLocationServiceSetting();
        }
        else {
            //GPS가 켜져있으면 허용 권한 설정
            checkRunTimePermission();
        }

        /**
         * 툴바 설정(드로워 연결용)
         * */
        toolbar = findViewById(R.id.pageViewToolbar);
        setSupportActionBar(toolbar);


        /**
         * 드로워 설정
         * **/
        drawerView = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.activity_sub_drawer)
                .withToolbarMenuToggle(toolbar)
                .inject();
        //마이페이지로 이동
        myPage = findViewById(R.id.myPage_textview_activitySub);
        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoubleRecyActivity.this, MyPage.class);
                intent.putExtra("userUUID", FirebaseAuth.getInstance().getUid());
                //todo : 마이페이지로 가는데 왜 위도, 경도를 보내는지?
                intent.putExtra("latitude", getMyLocation().getLatitude());
                intent.putExtra("longitude", getMyLocation().getLongitude());
                startActivity(intent);
            }
        });


        /**
         * 리사이클러뷰 설정
         * **/
        //내 포스팅 리사이클러뷰
        myPostsRecy = findViewById(R.id.myPosts_recyclerview_DoubleRecyActivity);
        myPostsRecy.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        doubleRecyMyPostAdapter = new DoubleRecyMyPostAdapter(this, myPostList);
        myPostsRecy.setAdapter(doubleRecyMyPostAdapter);

        //전체 포스팅 리사이클러뷰
        parentRecy = findViewById(R.id.parentRecy);
        initStoreRecyclerView(parentRecy, getMyLocation());


        /**
         * 화면에 띄울 데이터들 불러오기
         * **/
        // 내 포스팅들 불러오기
        getMyPostData();
        // 가게 목록을 가까운 거리순으로 불러옴
        getCloseStore();


        /**
         * 스와이프 리프레시 레이아웃 설정
         * **/
        swipeRefreshLayout = findViewById(R.id.swipe_layout_DoubleRecyActivity);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침하면 바로 리사이클러뷰 없애버림.
                myPostList.clear();
                parentList.clear();
                doubleRecyMyPostAdapter.notifyDataSetChanged();
                doubleRecyAdapter1.notifyDataSetChanged();

                if(swipeRefreshLayout.isRefreshing()){
                    myPostsRecy.setVisibility(View.GONE);
                    parentRecy.setVisibility(View.GONE);
                    getMyPostData();
                    getCloseStore();
                }
            }
        });

    }
    public GeoPoint getMyLocation(){
        gpsTracker = new GpsTracker(DoubleRecyActivity.this);
        title = findViewById(R.id.title_DoubleRecyActivity);
        final double latitude = gpsTracker.getLatitude();
        final double longitude = gpsTracker.getLongitude();
        title.setText(getCurrentAddress(latitude, longitude));
        Toast.makeText(DoubleRecyActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
        return new GeoPoint(latitude, longitude);
    }

    //가게 거리순 정렬해서 불러오기(거리 제한도 가능)
    public void getCloseStore(){
        final Map<Double,String> docMap = new HashMap<>();

        //내가 있는 위치의 위도, 경도값 가져오기(여기부턴 변하지 않게 final로 처리)
        final double myLat = getMyLocation().getLatitude();
        final double myLong = getMyLocation().getLongitude();

        FirebaseFirestore.getInstance()
                .collection("가게")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(!queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                String storeDocId = dc.getId();//가게 도큐먼트 아이디
                                GeoPoint geoPoint = (GeoPoint) dc.getData().get("geoPoint");//가게의 위도, 경도

                                double storeLat = geoPoint.getLatitude();
                                double storeLong = geoPoint.getLongitude();

                                //현재 내 위치랑 가게 위치간에 직선거리를 계산
                                double distance = MathUtil.distanceBtwMeAndStore(myLat, myLong, storeLat, storeLong);

                                docMap.put(distance, storeDocId);
                            }
                            //todo : 이제 가까운 순으로 정렬해서 리사이클러뷰에 순서대로 넣어줌.
                            TreeMap treeMap = new TreeMap<Double, String>(docMap);
                            Iterator<Double> iterator = treeMap.keySet().iterator(); // hashMap을 오름차순 정렬

                            while(iterator.hasNext()){
                                Double distance = iterator.next();

                                if(distance < 10.0){//거리 10km미만 제한걸 수 있음.
                                    String docId = docMap.get(distance);

                                    getStoreData(docId);//이제 거리순 오름차순 정렬한대로 이제 가게목록을 가져옴.
                                }

                            }
                        }
                    }
                });
    }

    //가게 목록 불러오는 리사이클러뷰 설정
    public void initStoreRecyclerView(RecyclerView parentRecy, GeoPoint myGeoPoint){
        parentRecy.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        doubleRecyAdapter1 = new DoubleRecyAdapter1(DoubleRecyActivity.this, parentList, myGeoPoint);
        parentRecy.setAdapter(doubleRecyAdapter1);
    }

    //내가 올린 포스팅들만 가져오기
    private void getMyPostData(){
        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .whereEqualTo("writerId", FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            for(DocumentSnapshot dc: queryDocumentSnapshots.getDocuments()){

                                PostingInfo postingInfo = dc.toObject(PostingInfo.class);

                                myPostList.add(postingInfo);

                                doubleRecyMyPostAdapter.notifyItemChanged(myPostList.size());

                                myPostsRecy.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
    }

    //가게, 부모리사이클러뷰 정보를 불러옴
    private void getStoreData(String documentId){
        FirebaseFirestore.getInstance()
                .collection("가게")
                .document(documentId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot != null && documentSnapshot.exists()){
                            StoreInfo storeInfo = documentSnapshot.toObject(StoreInfo.class);

                            parentList.add(storeInfo);

                            doubleRecyAdapter1.notifyItemChanged(parentList.size());

                            //새로고침시 로딩 후 프로그레스 바 없애줌
                            parentRecy.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    /**
     *거리 함수들
     * **/
    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(DoubleRecyActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(DoubleRecyActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            //todo : 위치값을 가져올 수 있으니까 여기부터 이제 내 위치와 리사이클러뷰 가져와야함.



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(DoubleRecyActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(DoubleRecyActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DoubleRecyActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DoubleRecyActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //경도와 위도값으로 한국어 주소로 표현해줌.
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DoubleRecyActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 변경하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ( requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(DoubleRecyActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(DoubleRecyActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DoubleRecyActivity.this, SignUpActivity.class));
            finish();
        }
        else{
            userName_drawer = findViewById(R.id.userName_textview_drawer);
            userName_drawer.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
    }
}
