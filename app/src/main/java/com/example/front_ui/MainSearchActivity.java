package com.example.front_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.PostingProcess.MainShareActivity;
import com.example.front_ui.PostingProcess.StoreSearchRecyclerViewAdapter;
import com.example.front_ui.Utils.JsonParsing;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.example.front_ui.Utils.RecyclerItemClickListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainSearchActivity extends AppCompatActivity {

    private final String TAG = "TAGMainSearchActivity";
    RecyclerView mRecyclerView;
    TextView search_tv;
    ImageView search_btn;

    Retrofit retrofit;
    KakaoApiStoreSearchService service;
    private final String kakaoApiId = "KakaoAK 952900bd9ca440b836d9c490525aef64";




    private final String school_code = "SC4";
    private final String subway_code = "SW8";
    private final String attraction_code = "AT4";


    private ArrayList<SearchedData> total_list;

    private ArrayList<SearchedData> local_list;
    private ArrayList<SearchedData> subway_list;
    private ArrayList<SearchedData> attraction_list;
    private ArrayList<SearchedData> school_list;

    private int localCnt;
    private int schoolCnt;
    private int subwayCnt;
    private int attractionCnt;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search_page);

        mRecyclerView = findViewById(R.id.mrecyclerView);
        search_tv = findViewById(R.id.mainsearchStore);
        search_btn = findViewById(R.id.main_search_btn);


        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String search_word = search_tv.getText().toString();
                Log.d(TAG, "search!! keyword : " + search_word);
                requestSearchApi(search_word);
            }
        });


        //recyclerView 아이템 터치 리스터. recycler view 중 가게를 하나 선택하면 다음 프래그먼트로 이동
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //선택한 아이템뷰 확인 및 데이터 전달
                        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
                        Log.d(TAG, "item clicked : " + total_list.get(itemPosition).place_name);
                        Log.d(TAG, "Search view");

                        Intent intent = new Intent();
                        intent.putExtra("SearchedData", total_list.get(itemPosition));
                        Log.d(TAG, "searchedData : placename, x, y" + total_list.get(itemPosition).place_name+ total_list.get(itemPosition).x+total_list.get(itemPosition).y);
                        //선택한 가게 정보 데이터를 bundle에 넣고 다음 프래그먼트로 이동
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }



    /*
    * 검색 기능! - 지역 검색
    * */
    private void requestSearchApi(final String searchWord){

        localCnt = -1;
        schoolCnt  = -1;
        subwayCnt  = -1;
        attractionCnt = -1;

        retrofit = new Retrofit.Builder()
                .baseUrl(KakaoApiStoreSearchService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(KakaoApiStoreSearchService.class);
        Call<JsonObject> request_local = service.getKakaoLocalInfo(kakaoApiId, searchWord);//, code
        Call<JsonObject> request_school = service.getKakaoStoreInfo(kakaoApiId, searchWord, school_code, "2");//, code
        Call<JsonObject> request_subway = service.getKakaoStoreInfo(kakaoApiId, searchWord, subway_code, "1");//, code
        Call<JsonObject> request_attraction = service.getKakaoStoreInfo(kakaoApiId, searchWord, attraction_code, "4");//, code

        request_local.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_local enqueue is Successed. data : " + response.body().toString());
                    local_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 1);
                    localCnt = local_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
                        initRecyclerView();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_local enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //store listener
        request_school.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_school enqueue is Successed. data : " + response.body().toString());
                    school_list = JsonParsing.parseJsonToSearchedInfo(response.body(),  searchWord,2);
                    schoolCnt = school_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
                        initRecyclerView();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_school enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //store listener
        request_subway.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_subway enqueue is Successed. data : " + response.body().toString());
                    subway_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 2);
                    subwayCnt = subway_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
                        initRecyclerView();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_subway enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //store listener
        request_attraction.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_attraction enqueue is Successed. data : " + response.body().toString());
                    attraction_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 2);
                    attractionCnt = attraction_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
                        initRecyclerView();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_attraction enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });



    }


    /*
    * recyclerview 세팅
    * */
    //주변 가게 recyclerviewt세팅!
    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView");
        //데이터 순서 정렬. subway_list에 다 때려넣는다.
        total_list = local_list;
        total_list.addAll(subway_list);
        total_list.addAll(school_list);
        total_list.addAll(attraction_list);

        for(int i = 0; i < local_list.size(); i++){
            Log.d(TAG, "data " + i + " : "+ local_list.get(i).getPlace_name());
        }

        mRecyclerView.setHasFixedSize(true);
        MainSearchRecyclerViewAdapter adapter = new MainSearchRecyclerViewAdapter(total_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
        //recyclerview 세팅 필요!!!

    }



}
