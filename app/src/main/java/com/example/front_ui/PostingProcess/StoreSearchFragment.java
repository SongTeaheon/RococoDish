package com.example.front_ui.PostingProcess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.JsonParsing;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.front_ui.Utils.KakaoApiStoreSearchService.API_URL;



public class StoreSearchFragment extends Fragment {
    private final String TAG = "TAGStoreResearchFrag";
    private final String kakaoApiId = "KakaoAK 952900bd9ca440b836d9c490525aef64";
    private final String code_store = "FD6"; //음식점
    private final String code_cafe = "CE7"; //카페

    private int count_store;
    private int count_cafe;
    private ArrayList<KakaoStoreInfo> dataList_cafe =null;
    private ArrayList<KakaoStoreInfo> dataList_store = null;

    ConstraintLayout constraint;


    Retrofit retrofit;
    KakaoApiStoreSearchService service;

    EditText searchWordText;
    String searchWord;

    RecyclerView mRecyclerView;
    ArrayList<KakaoStoreInfo> storeInfoArrayList;

    InputMethodManager imm;

    TabLayout tabLayout;
    ViewPager viewPager;
    StoreSearchFragVpAdapter storeSearchFragVpAdapter;

    ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "OnCreateView : started");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_search, container, false);
//        mRecyclerView = view.findViewById(R.id.recyclerview);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        constraint = view.findViewById(R.id.constraint_tv);

        storeInfoArrayList = new ArrayList<>();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        //search 버튼(EditText에 있는 단어를 받아서 검색)
        searchWordText = view.findViewById(R.id.searchWord);
        ImageView searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchWord = searchWordText.getText().toString();
                Log.d(TAG, "search button clicked. searchWord : "+ searchWord);
                count_cafe = -1;
                count_store = -1;
                requestSearchApi(searchWord);
            }
        });

        searchWordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchWord = searchWordText.getText().toString();
                    Log.d(TAG, "search button clicked. searchWord : "+ searchWord);
                    count_cafe = -1;
                    count_store = -1;
                    requestSearchApi(searchWord);
                    imm.hideSoftInputFromWindow(searchWordText.getWindowToken(), 0);
                }
                return false;
            }
        });

        //todo : 기존에 태헌님이 만드신 리사이클러뷰는 일단 주석처리해놓을께요.
//        //recyclerview setup
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//
//
//        //recyclerView 아이템 터치 리스터. recycler view 중 가게를 하나 선택하면 다음 프래그먼트로 이동
//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//
//                        //선택한 아이템뷰 확인 및 데이터 전달
//                        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//                        Log.d(TAG, "item clicked : " + storeInfoArrayList.get(itemPosition).place_name);
//                        Log.d(TAG, "move to Gallery");
//                        //activity로 store정보를 보내준다.
//                        MainShareActivity activity = (MainShareActivity) getActivity();
//                        activity.setKakaoStoreInfo(storeInfoArrayList.get(itemPosition));
//                        //선택한 가게 정보 데이터를 bundle에 넣고 다음 프래그먼트로 이동
//                        setFragmentAndMove(getContext());
//
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
        //todo : 탭이랑 뷰페이저 설정 - 태완
        tabLayout = view.findViewById(R.id.tablayout_fragStoreSearch);
        viewPager = view.findViewById(R.id.viewpager_fragStoreSearch);


        return view;
    }


    //네이버 api 검색 실행. 성공하면 정보 받아서 리사이클러뷰 어댑터로 넘긴다.
    private void requestSearchApi(String searchWord){

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(KakaoApiStoreSearchService.class);
        Call<JsonObject> request_store = service.getKakaoStoreInfo(kakaoApiId, searchWord, code_store);//, code
        Call<JsonObject> request_cafe = service.getKakaoStoreInfo(kakaoApiId, searchWord, code_cafe);//, code



        //store listener
        request_store.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_store enqueue is Successed  ");
                    count_store = JsonParsing.getCountFromApi(response.body());
                    dataList_store = JsonParsing.parseJsonToStoreInfo(response.body());
                }else{
                    count_store = 0;
                    dataList_store = null;
                }
                //cafe완료된면
                if (count_cafe >= 0) {
                    setFragVpAdapter();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_store enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

        //cafe listener
        request_cafe.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body() != null) {
                    Log.d(TAG, "request_cafe enqueue is Successed  ");
                    count_cafe = JsonParsing.getCountFromApi(response.body());
                    dataList_cafe = JsonParsing.parseJsonToStoreInfo(response.body());
                }else{
                    count_cafe = 0;
                    dataList_cafe = null;
                }
                //store완료된면
                if (count_store >= 0) {
                    setFragVpAdapter();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_store enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

    }

    //recycler view를 네이버 api에서 가져온 리스트와 함께 어댑터 세팅
    private void setFragVpAdapter() {
        //TODO:여기서 다 세팅할 필요없을 거 같은데 확인필요
        storeSearchFragVpAdapter = new StoreSearchFragVpAdapter(getFragmentManager(), dataList_store, dataList_cafe);
        storeSearchFragVpAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(storeSearchFragVpAdapter);
//        StoreSearchRecyclerViewAdapter myAdapter = new StoreSearchRecyclerViewAdapter(getActivity(), storeInfoArrayList);
//        myAdapter.notifyDataSetChanged();//검색을 다른 걸로 하면 다시 세팅!
//        mRecyclerView.setAdapter(myAdapter);
    }

}