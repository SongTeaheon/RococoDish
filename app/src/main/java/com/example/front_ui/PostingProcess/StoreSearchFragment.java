package com.example.front_ui.PostingProcess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoMetaData;
import com.example.front_ui.DataModel.KakaoMetaData;
import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Util_Kotlin.Storage;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.example.front_ui.Utils.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.security.auth.Subject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.front_ui.Utils.KakaoApiStoreSearchService.API_URL;



public class StoreSearchFragment extends Fragment {
    private final String TAG = "TAGStoreResearchFrag";
    private final String kakaoApiId = "KakaoAK 952900bd9ca440b836d9c490525aef64";
    private final String code = "FD6"; //카페는 CE7
    public static int RC_fromStoreSearchFragment = 12345;
    private final String code_store = "FD6"; //음식점
    private final String code_cafe = "CE7"; //카페

    private int count_store;
    private int count_cafe;
    private ArrayList<KakaoStoreInfo> dataList_cafe;
    private ArrayList<KakaoStoreInfo> dataList_store;




    Retrofit retrofit;
    KakaoApiStoreSearchService service;

    EditText searchWordText;
    String searchWord;

    RecyclerView mRecyclerView;
    ArrayList<KakaoStoreInfo> storeInfoArrayList;

    InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "OnCreateView : started");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_search, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);


        storeInfoArrayList = new ArrayList<>();

        //search 버튼(EditText에 있는 단어를 받아서 검색)
        searchWordText = view.findViewById(R.id.searchWord);
        Button searchButton = view.findViewById(R.id.searchButton);
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

        //recyclerview setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


        //recyclerView 아이템 터치 리스터. recycler view 중 가게를 하나 선택하면 다음 프래그먼트로 이동
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //선택한 아이템뷰 확인 및 데이터 전달
                        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
                        Log.d(TAG, "item clicked : " + storeInfoArrayList.get(itemPosition).place_name);
                        Log.d(TAG, "move to Gallery");
                        //activity로 store정보를 보내준다.
                        MainShareActivity activity = (MainShareActivity) getActivity();
                        activity.setKakaoStoreInfo(storeInfoArrayList.get(itemPosition));
                        //선택한 가게 정보 데이터를 bundle에 넣고 다음 프래그먼트로 이동
                        setFragmentAndMove(getContext());

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        return view;
    }

    //선택한 가게 정보 데이터를 bundle에 넣고 다음 프래그먼트로 이동
    //태완태완 : 여기 setFragmentAndMove함수에서 그 갤러리 액티비티로 넘어가야합니다.
    private void setFragmentAndMove(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1001 && resultCode == Activity.RESULT_OK){
            Uri galleryUri = data.getData();

            //크롭 자체 제작(라이브러리 안씀)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(galleryUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 128);
                    cropIntent.putExtra("outputY", 128);
                    cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, RC_fromStoreSearchFragment);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult");
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null){
//            ProgressDialog loading = ProgressDialog.show(getContext(), "로딩중", "잠시만 기다려주세요...");
//            CropImage.ActivityResult result =CropImage.getActivityResult(data);
//            try {
//                Bitmap selectedBmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getUri());
//                OutputStream outputStream = new ByteArrayOutputStream();
//                selectedBmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
//                byte[] byteArray = ((ByteArrayOutputStream) outputStream).toByteArray();
////                Intent passByte = new Intent(getContext(), LastShareFragment.class);
////                passByte.putExtra("byteArray", byteArray);
////                startActivity(passByte);
//                //fragment로 데이터 전송 및 변경
//                LastShareFragment fragment = new LastShareFragment();
//                Bundle args = new Bundle();
//                args.putByteArray("byteArray", byteArray);
//                fragment.setArguments(args);
//
//                FragmentTransaction fragmentTransaction = getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.relLayout1, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
                Log.d(TAG, "request_store enqueue is Successed  ");
                count_store = getCountFromApi(response.body());
                dataList_store = parseJsonToStoreInfo(response.body());

                //cafe완료된면
                if(count_cafe >= 0){
                    setDataAndsetRecyclerview();

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
                Log.d(TAG, "request_cafe enqueue is Successed  ");
                count_cafe = getCountFromApi(response.body());
                dataList_cafe = parseJsonToStoreInfo(response.body());
                //store완료된면
                if(count_store >= 0){
                    setDataAndsetRecyclerview();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request_store enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });

    }

    //store와 cafe의 total count를 가져와서 더 많은거 먼저 집어 넣는다.
    private void setDataAndsetRecyclerview(){
        Log.d(TAG, "setRecyclerView count store : " + count_store + " count cafe : " + count_cafe);
        if(count_cafe > count_store){
            storeInfoArrayList= KakaoInfoArrayListClone(dataList_cafe);
            storeInfoArrayList.addAll(dataList_store);
        }else{
            storeInfoArrayList = KakaoInfoArrayListClone(dataList_cafe);
            storeInfoArrayList.addAll(dataList_cafe);
        }
        Log.d(TAG, "first info : " + storeInfoArrayList.get(0).place_name + " sec : " + storeInfoArrayList.get(1).place_name);
        setRecyclerviewAdapter(storeInfoArrayList);
    }


    //kakao api에서 total count를 받아온다.
    private int getCountFromApi(JsonObject jsonObject) {
        Gson gson = new Gson();

        JsonObject jsonObject_meta = (JsonObject) jsonObject.get("meta");
        KakaoMetaData metaOb = gson.fromJson(jsonObject_meta, KakaoMetaData.class);
        int total_count = Integer.parseInt(metaOb.getTotal_count());

        Log.d(TAG, "meta data of store info.  total count: " +  metaOb.getTotal_count());
        return total_count;
    }
    //kakao api에서 받아온 jsonObject를 파싱해서 ArrayList<>로 변경
    private ArrayList<KakaoStoreInfo> parseJsonToStoreInfo(JsonObject jsonObject) {
        Log.d(TAG, "parseJsonToStoreInfo");
        ArrayList<KakaoStoreInfo> dataList = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray jsonArray = (JsonArray) jsonObject.get("documents");
        for(int i=0 ; i<jsonArray.size(); i++){
            KakaoStoreInfo object = gson.fromJson(jsonArray.get(i), KakaoStoreInfo.class);
            Log.d(TAG, "info  : " + object.place_name);
            dataList.add(object);
        }
        return dataList;
    }

    //recycler view를 네이버 api에서 가져온 리스트와 함께 어댑터 세팅
    private void setRecyclerviewAdapter(ArrayList<KakaoStoreInfo> storeInfoArrayList) {

        StoreSearchRecyclerViewAdapter myAdapter = new StoreSearchRecyclerViewAdapter(getActivity(), storeInfoArrayList);
        myAdapter.notifyDataSetChanged();//검색을 다른 걸로 하면 다시 세팅!
        mRecyclerView.setAdapter(myAdapter);
    }


    public ArrayList<KakaoStoreInfo> KakaoInfoArrayListClone(ArrayList<KakaoStoreInfo> list){
        ArrayList<KakaoStoreInfo> temp = new ArrayList<>();
        for(KakaoStoreInfo list_item: list){
            try {
                temp.add((KakaoStoreInfo)list_item.clone());
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return temp;
    }

}