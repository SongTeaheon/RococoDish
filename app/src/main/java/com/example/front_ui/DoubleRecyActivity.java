package com.example.front_ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_recy);

        /**
         * 툴바 설정
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
        parentRecy.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        doubleRecyAdapter1 = new DoubleRecyAdapter1(DoubleRecyActivity.this, parentList);
        parentRecy.setAdapter(doubleRecyAdapter1);


        /**
         * 화면에 띄울 데이터들 불러오기
         * **/
        // 내 포스팅들 불러오기
        getMyPostData();
        // 가게, 부모리사이클러뷰 정보를 불러옴
        getStoreData();


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
                    getStoreData();
                }

            }
        });

    }
    //내 포스팅들 가져오기
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
    private void getStoreData(){
        FirebaseFirestore.getInstance()
                .collection("가게")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc: queryDocumentSnapshots.getDocuments()){

                                StoreInfo storeInfo = dc.toObject(StoreInfo.class);

                                parentList.add(storeInfo);
                                doubleRecyAdapter1.notifyItemChanged(parentList.size());

                                //새로고침시 로딩 후 프로그레스 바 없애줌
                                parentRecy.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    }
                });
    }
}
