package com.example.front_ui.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Interface.AlgoliaSearchPredicate;
import com.example.front_ui.R;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.JsonParsing;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.google.gson.JsonObject;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

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



    EditText editText;
    ImageView searchBtn;
    ImageView backBtn;

    //검색결과 리스트
    public ArrayList<StoreInfo> storeList;
    public ArrayList<SearchedData> regionList;
    public ArrayList<UserInfo> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_search_page);

        editText = findViewById(R.id.mainsearch_text);
        //검색 버튼
        searchBtn = findViewById(R.id.main_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "search button is clicked");
                String keyword = editText.getText().toString();
                //1. 가게이름 검색
                getRegionSearchResult(keyword);
                getPeopleSearchResult(keyword);
                //TODO: 태그 검색 만들기
//                getTagSearchResult(keyword);
                getStoreSearchResult(keyword);

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


        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_bar);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragmentStore).commitAllowingStateLoss();

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.nav_store: {
                        transaction.replace(R.id.frame_layout, fragmentStore).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_region: {
                        transaction.replace(R.id.frame_layout, fragmentRegion).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_tag: {
                        transaction.replace(R.id.frame_layout, fragmentTag).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_people: {
                        transaction.replace(R.id.frame_layout, fragmentPeople).commitAllowingStateLoss();
                        break;
                    }
                }

                return true;
            }
        });
    }


    void getStoreSearchResult(String keyword){
        AlgoliaUtils.searchStore("store", "name", keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                storeList =  JsonParsing.getStoreListFromJsonList(jsonArray);
                for(int i = 0; i < storeList.size(); i++){
                    Log.d(TAG, "store data " + i + " : "+ storeList.get(i).getName());
                }
            }
        });
    }

    void getPeopleSearchResult(String keyword){
        AlgoliaUtils.searchStore("user", "nickname", keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                userList =  JsonParsing.getUserListFromJsonList(jsonArray);
                for(int i = 0; i < userList.size(); i++){
                    Log.d(TAG, "user data " + i + " : "+ userList.get(i).getNickname());
                }
            }
        });
    }

    /********************************* 지역검색 만들기 필요 변수 및 함수 *********************************/
    private ArrayList<SearchedData> local_list;
    private ArrayList<SearchedData> subway_list;
    private ArrayList<SearchedData> attraction_list;
    private ArrayList<SearchedData> school_list;

    private int localCnt;
    private int schoolCnt;
    private int subwayCnt;
    private int attractionCnt;
    private final String school_code = "SC4";
    private final String subway_code = "SW8";
    private final String attraction_code = "AT4";
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
        Call<JsonObject> request_attraction = service.getKakaoStoreInfo(kakaoApiId, searchWord, attraction_code, "4");//, code

        //로컬 listener
        request_local.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_local enqueue is Successed. data : " + response.body().toString());
                    local_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 1);
                    localCnt = local_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
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
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
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
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
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
        request_attraction.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_attraction enqueue is Successed. data : " + response.body().toString());
                    attraction_list = JsonParsing.parseJsonToSearchedInfo(response.body(), searchWord, 2);
                    attractionCnt = attraction_list.size();
                    if(localCnt > -1 && schoolCnt >-1 && subwayCnt > -1 && attractionCnt > -1){
                        makeResultForStoreSearch();
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

    void makeResultForStoreSearch(){
        regionList = local_list;
        regionList.addAll(subway_list);
        regionList.addAll(school_list);
        regionList.addAll(attraction_list);

        for(int i = 0; i < regionList.size(); i++){
            Log.d(TAG, "region data " + i + " : "+ regionList.get(i).getPlace_name());
        }
    }


}
