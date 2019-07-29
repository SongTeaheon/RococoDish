package com.example.front_ui.Search;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.AlgoliaTagData;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Interface.AlgoliaSearchPredicate;
import com.example.front_ui.R;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.JsonParsing;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubSearchPage extends AppCompatActivity {

    private final String TAG = "TAGSubSearchPage";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentStore fragmentStore = new FragmentStore();
    private FragmentRegion fragmentRegion = new FragmentRegion();
    private FragmentTag fragmentTag = new FragmentTag();
    private FragmentPeople fragmentPeople = new FragmentPeople();

    Retrofit retrofit;
    KakaoApiStoreSearchService service;
    //TODO: 리팩토링 : kakao api utils따로 만들어서 StoreSearchFragment코드랑 합치기
    private final String kakaoApiId = "KakaoAK 952900bd9ca440b836d9c490525aef64";

    SimpleCursorAdapter mAdapter = null;
    MyDBHandler dbHandler;
    EditText editText;
    ImageView searchBtn;
    ImageView backBtn;

    //검색결과 리스트
    ArrayList<StoreInfo> storeList;
    ArrayList<SearchedData> regionList;
    ArrayList<UserInfo> peopleList;
    ArrayList<AlgoliaTagData> tagList;

    //리사이클러 뷰
    RecyclerView recyclerViewStore;
    RecyclerView recyclerViewPeople;
    RecyclerView recyclerViewRegion;
    RecyclerView recyclerViewTag;

    NestedScrollView scrollView;

    CardView cardViewStore;
    CardView cardViewPeople;
    CardView cardViewRegion;
    CardView cardViewTag;

    TextView tvStore;
    TextView tvRegion;
    TextView tvPeople;
    TextView tvTag;

    double currentLatitude;
    double currentLongtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_search_page);

        Intent intent = this.getIntent();
        currentLatitude = intent.getDoubleExtra("latitude", 0.0);
        currentLongtitude = intent.getDoubleExtra("longitude", 0.0);

        storeList = new ArrayList<>();;
        regionList = new ArrayList<>();
        peopleList = new ArrayList<>();

        recyclerViewStore = findViewById(R.id.recyclerviewStore);
        recyclerViewPeople = findViewById(R.id.recyclerviewPeople);
        recyclerViewRegion = findViewById(R.id.recyclerviewRegion);
        recyclerViewTag = findViewById(R.id.recyclerviewTag);

        cardViewStore = findViewById(R.id.cardviewStore);
        cardViewPeople = findViewById(R.id.cardviewPeople);
        cardViewRegion = findViewById(R.id.cardviewRegion);
        cardViewTag = findViewById(R.id.cardviewTag);

        tvStore = findViewById(R.id.tv_store);
        tvPeople = findViewById(R.id.tv_people);
        tvRegion = findViewById(R.id.tv_region);
        tvTag = findViewById(R.id.tv_tag);

        editText = findViewById(R.id.mainsearch_text);

        //검색 버튼
        searchBtn = findViewById(R.id.main_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "search button is clicked");
                String keyword = editText.getText().toString();
                dbHandler.insert(keyword);

                getRegionSearchResult(keyword);
                getPeopleSearchResult(keyword);
                getTagSearchResult(keyword);
                getStoreSearchResult(keyword);

            }
        });

        tvStore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "tvStore is clicked");
                initFragment(fragmentStore);
            }
        });
        tvRegion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "tvRegion is clicked");
                initFragment(fragmentRegion);
            }
        });
        tvPeople.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "tvPeople is clicked");
                initFragment(fragmentPeople);
            }
        });
        tvTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "tvTag is clicked");
                initFragment(fragmentTag);

            }
        });


        //뒤로가기(백) 버튼
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //검색 기록을 위한 DB
        if( dbHandler == null ) {
            dbHandler = MyDBHandler.open(this);
        }
        Cursor c = dbHandler.select();
        Log.d(TAG, "record index : " + c.getColumnIndex("RECORD"));
        Log.d(TAG, "record size : " + c.getCount());

        if(c.getCount() != 0)
            dbHandler.getAllRecordData();
//        mAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_activated_2,
//                c, new String[]{"id", "record"}, new int[]{android.R.id.text1, android.R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //id와 record를 찍어보자

    }
    /********************************* 리사이클러 뷰 4개 세팅  ********************************/
    void initRecyclerViewStore(){
        Log.d(TAG, "initRecyclerViewStore");
        recyclerViewStore.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        FragmantStoreRecyclerViewAdapter storeRecyclerViewAdapter = new FragmantStoreRecyclerViewAdapter(this, storeList);
        recyclerViewStore.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewStore.setAdapter(storeRecyclerViewAdapter);
    }
    void initRecyclerViewPeople(){
        Log.d(TAG, "initRecyclerViewPeople");
        recyclerViewPeople.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        FragmantPeopleRecyclerViewAdapter peopleRecyclerViewAdapter = new FragmantPeopleRecyclerViewAdapter(this, peopleList, currentLatitude, currentLongtitude);
        recyclerViewPeople.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPeople.setAdapter(peopleRecyclerViewAdapter);
    }
    void initRecyclerViewRegion(){
        Log.d(TAG, "initRecyclerViewStore");
        recyclerViewRegion.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        FragmantRegionRecyclerViewAdapter regionRecyclerViewAdapter = new FragmantRegionRecyclerViewAdapter(this, regionList);
        recyclerViewRegion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewRegion.setAdapter(regionRecyclerViewAdapter);
    }
    void initRecyclerViewTag(){
        Log.d(TAG, "initRecyclerViewStore");
        recyclerViewTag.setHasFixedSize(true);
        //가게 안에 목록 가져오는 리사이클러뷰
        FragmantTagRecyclerViewAdapter tagRecyclerViewAdapter = new FragmantTagRecyclerViewAdapter(this, tagList);
        recyclerViewTag.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewTag.setAdapter(tagRecyclerViewAdapter);
    }

    void initFragment(Fragment fragment){
        fragmentManager.beginTransaction().replace(R.id.resultFrameLayout, fragment).commit();
    }

    /********************************* 가게, 지역, 사람 검색 만들기 필요 변수 및 함수  *********************************/
    void getStoreSearchResult(String keyword){
        AlgoliaUtils.searchData("store", "name", keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                storeList =  JsonParsing.getStoreListFromJsonList(jsonArray);
                for(int i = 0; i < storeList.size(); i++){
                    Log.d(TAG, "store data " + i + " : "+ storeList.get(i).getName());
                }
                if(storeList.size() != 0) {
                    initRecyclerViewStore();
                    cardViewStore.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "no data for Store");
                    recyclerViewStore.setVisibility(View.GONE);
                    cardViewStore.setVisibility(View.GONE);
                }
            }
        });
    }

    void getPeopleSearchResult(String keyword){
        AlgoliaUtils.searchData("user", "nickname", keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                peopleList =  JsonParsing.getUserListFromJsonList(jsonArray);
                for(int i = 0; i < peopleList.size(); i++){
                    Log.d(TAG, "user data " + i + " : "+ peopleList.get(i).getNickname());
                }
                if(peopleList.size() != 0) {
                    initRecyclerViewPeople();
                    cardViewPeople.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "no data for People");
                    recyclerViewPeople.setVisibility(View.GONE);
                    cardViewPeople.setVisibility(View.GONE);
                }
            }
        });
    }

    void getTagSearchResult(String keyword){
        //TODO: exact 아님 그냥 data로 바꿔야함. 그냥 테스트용
        AlgoliaUtils.searchData("tag", "text", keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                Log.d(TAG, "tag data json array " + jsonArray.toString());
                tagList =  JsonParsing.getTagListFromJsonList(jsonArray);
                for(int i = 0; i < tagList.size(); i++){
                    Log.d(TAG, "tag data " + i + " : "+ tagList.get(i).getText());
                }
                if(tagList.size() != 0) {
                    initRecyclerViewTag();
                    cardViewTag.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "no data for Tag");
                    recyclerViewTag.setVisibility(View.GONE);
                    cardViewTag.setVisibility(View.GONE);
                }
            }
        });
    }

    /********************************* 지역검색 만들기 필요 변수 및 함수 *********************************/
    private ArrayList<SearchedData> local_list;
    private ArrayList<SearchedData> subway_list;
//    private ArrayList<SearchedData> attraction_list;
    private ArrayList<SearchedData> school_list;

    private int localCnt;
    private int schoolCnt;
    private int subwayCnt;
    private int attractionCnt;
    private final String school_code = "SC4";
    private final String subway_code = "SW8";
//    private final String attraction_code = "AT4";
    void getRegionSearchResult(final String searchWord){

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
//        Call<JsonObject> request_attraction = service.getKakaoStoreInfo(kakaoApiId, searchWord, attraction_code, "4");//, code

        //로컬 listener
        request_local.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_local enqueue is Successed. data : " + response.body().toString());
                    local_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 1);
                    localCnt = local_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1){
                        makeResultForStoreSearch();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_local enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //대학교 listener
        request_school.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_school enqueue is Successed. data : " + response.body().toString());
                    school_list = JsonParsing.parseJsonToSearchedInfo(response.body(),  searchWord,2);
                    schoolCnt = school_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1){
                        makeResultForStoreSearch();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_school enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //지하철 listener
        request_subway.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_subway enqueue is Successed. data : " + response.body().toString());
                    subway_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 2);
                    subwayCnt = subway_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1){
                        makeResultForStoreSearch();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_subway enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //관광명소 listener
//        request_attraction.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if(response.body() != null) {
//                    Log.d(TAG, "request_attraction enqueue is Successed. data : " + response.body().toString());
//                    attraction_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 2);
//                    attractionCnt = attraction_list.size();
//                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
//                        makeResultForStoreSearch();
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.d(TAG, "request_attraction enqueue is failed : w : " + t.toString());
//                t.printStackTrace();
//            }
//        });
    }

    void makeResultForStoreSearch(){
        regionList = local_list;
        regionList.addAll(subway_list);
        regionList.addAll(school_list);
//        regionList.addAll(attraction_list);
        Log.d(TAG, "size of local list : " + local_list.size());
        Log.d(TAG, "size of region list : " + regionList.size());

        for(int i = 0; i < regionList.size(); i++){
            Log.d(TAG, "region data " + i + " : "+ regionList.get(i).getPlace_name());
        }
        if(regionList.size() != 0) {
            initRecyclerViewRegion();
            cardViewRegion.setVisibility(View.VISIBLE);
        }else {
            Log.d(TAG, "no data for region");
            recyclerViewRegion.setVisibility(View.GONE);
            cardViewRegion.setVisibility(View.GONE);
        }
    }

    /********************************* 데이터 전달을 위한 함수 - 리팩토링 필요한가?? *********************************/
    public ArrayList<StoreInfo> getStoreList(){
        return storeList;
    }
    public ArrayList<SearchedData> getRegionList(){
        return regionList;
    }
    public ArrayList<UserInfo> getPeopleList(){
        return peopleList;
    }
    public ArrayList<AlgoliaTagData> getTagList(){
        return tagList;
    }
    public double getLat(){
        return currentLatitude;
    }
    public double getLon(){
        return currentLongtitude;
    }



}
