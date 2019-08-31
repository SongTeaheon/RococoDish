package com.rococodish.front_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.PostingProcess.MainShareActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class FeedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = "TAGFeedActivity";
    RecyclerView recy_feed;
    FeedAdapter recyFeedAdapter;
    FrameLayout loadingFrame;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionButton addPosting;


    private ArrayList<PostingInfo> mListPosting;
    ArrayList<String> mListFollower;
    FirebaseFirestore db;
    Context mContext;
    ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mContext = this;
        db = FirebaseFirestore.getInstance();
        mListPosting = new ArrayList<>();
        recy_feed = findViewById(R.id.mrecyclerView);
        //todo :로딩창
        loadingFrame = findViewById(R.id.loadingFrame);
        loadingFrame.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getFollowerData();

        //우측 하단 포스팅 추가 버튼
        addPosting = findViewById(R.id.addPosting_fab_subActivity_feed);
        addPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, MainShareActivity.class);
                startActivity(intent);
            }
        });

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getFollowerData(){
        //1.팔로우 데이터를 가져온다. userid리스트
        //2.해당 userid와 같은 데이터 전부 가져온다.
        //3.게시글을 시간 순으로 보여주자.
        mListFollower = new ArrayList<>();
        Log.d(TAG, "getFollowData");
        db.collection("사용자")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("팔로잉")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //조건에 해당하는게 없는지 확인
                            if (task.getResult().isEmpty()) {
                                Log.d(TAG, "task.getResult : " + task.getResult().isEmpty());
                                Toast.makeText(mContext, "팔로워가 없습니다. 관심있는 친구를 팔로우 하세요!", Toast.LENGTH_LONG).show();
                            }else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "팔로워 id : " + document.getId());
                                    mListFollower.add(document.getId());
                                }
                                //다끝났으면 팔로워들의 데이터를 시간 순으로 가져오자.
                                getPostingData();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getPostingData() {
        Log.d(TAG, "getPostingData  데이터 수 : " + mListFollower.size());

        for (int i = 0; i < mListFollower.size(); i++) {
            final int count = i;
            db.collection("포스팅")
                    .whereEqualTo("writerId", mListFollower.get(i))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //조건에 해당하는게 없는지 확인
                                if (task.getResult().isEmpty()) {
                                    Log.d(TAG, "task.getResult : " + task.getResult().isEmpty());
                                    Toast.makeText(mContext, "팔로우한 사람들의 게시글이 존재하지 않습니다", Toast.LENGTH_LONG).show();
                                }
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "포스팅 id : " + document.getId());
                                    PostingInfo postingInfo = document.toObject(PostingInfo.class);
                                    mListPosting.add(postingInfo);
                                }
                                //다끝났으면 팔로워들의 데이터를 시간 순으로 가져오자.
                                if(count >= mListFollower.size() -1){
                                    display();
                                }
                                display();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    private void display(){
        //1. 시간 순으로 정렬하고, 보내준다.
        Log.d(TAG, "display()");
        if(mListPosting != null){
            Collections.sort(mListPosting, new Comparator<PostingInfo>() {
                @Override
                public int compare(PostingInfo o1, PostingInfo o2) {
                    return ((Date)o2.getPostingTime()).compareTo((Date)o1.getPostingTime());//FIXME:아무리 생각해도 이거 Timestamp인데 자꾸 Date라고 해서 Date처럼 함.
                }
            });



            //FIXME: 지금은 한 번에 다 올리지만, 나중에는 10개만 올리고, Adapter.OnLoadMoreItemsListener를 이용해서 따로 올리는걸 해야할 듯
            //https://github.com/stephyswe/Android-Instagram.git
            initRecyclerView();
        }else {
            loadingFrame.setVisibility(View.GONE);
            Log.d(TAG, "mListPosting은 null");
        }

    }



    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView");
        recy_feed.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        recyFeedAdapter = new FeedAdapter(this, mListPosting, loadingFrame);
        recyFeedAdapter.setHasStableIds(true); //dataSetChange할 때, blink하는 문제를 해결하기 위해!! getItemId 오버라이드 필요!!
        recy_feed.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recy_feed.setAdapter(recyFeedAdapter);
        //swipe끝내기
    }

    @Override
    public void onRefresh() {
        loadingFrame.setVisibility(View.VISIBLE);
        mListPosting.clear();
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }//이건 그냥 꺼줘도 됨.
        getFollowerData();
    }
}
