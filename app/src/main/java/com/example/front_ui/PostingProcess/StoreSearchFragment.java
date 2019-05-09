package com.example.front_ui.PostingProcess;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.KakaoApiStoreSearchService;
import com.example.front_ui.Utils.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

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


    Retrofit retrofit;
    KakaoApiStoreSearchService service;

    EditText searchWordText;
    String searchWord;

    RecyclerView mRecyclerView;
    ArrayList<KakaoStoreInfo> storeInfoArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "OnCreateView : started");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_search, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);




        //back 버튼
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : closing the restaurantn research fragment, finish Activity");
                //back button 기능
                getActivity().finish();



            }
        });

        //next 버튼
        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : navigating to gallery fragment : 필요없어질 수도!");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.relLayout1, new GalleryFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //search 버튼(EditText에 있는 단어를 받아서 검색)
        searchWordText = view.findViewById(R.id.searchWord);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchWord = searchWordText.getText().toString();
                Log.d(TAG, "search button clicked. searchWord : "+ searchWord);
                requestSearchApi(searchWord);


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
//        Log.d(TAG, "put data in Fragment");
//        GalleryFragment fragment = new GalleryFragment();
//        Bundle args = new Bundle();
//        args.putParcelable("StoreData", storeInfoArrayList.get(itemPosition));
//        fragment.setArguments(args);
//
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.relLayout1, fragment);
//        ft.addToBackStack(null);
//        ft.commit();
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
               // .setCropShape(CropImageView.CropShape.OVAL)
                .setBorderCornerColor(Color.GRAY)
                .setAutoZoomEnabled(true)
                .setFixAspectRatio(true)
                .start((Activity) context);
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
        Call<JsonObject> request = service.getKakaoStoreInfo(kakaoApiId, searchWord, code);//, code
        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "request enqueue is Successed  ");
                storeInfoArrayList  = parseJsonToStoreInfo(response.body());
                setRecyclerviewAdapter(storeInfoArrayList);
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "request enqueue is failed : w : " + t.toString());
                t.printStackTrace();
            }
        });


    }

    //kakao api에서 받아온 jsonObject를 파싱해서 ArrayList<>로 변경
    private ArrayList<KakaoStoreInfo> parseJsonToStoreInfo(JsonObject jsonObject) {
        ArrayList<KakaoStoreInfo> dataList = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray jsonArray = (JsonArray) jsonObject.get("documents");
        for(int i=0 ; i<jsonArray.size(); i++){
            KakaoStoreInfo object = gson.fromJson(jsonArray.get(i), KakaoStoreInfo.class);
            dataList.add(object);
        }
        return dataList;

    }

    //recycler view를 네이버 api에서 가져온 리스트와 함께 어댑터 세팅
    private void setRecyclerviewAdapter(ArrayList<KakaoStoreInfo> storeInfoArrayList) {
        StoreSearchRecyclerViewAdapter myAdapter = new StoreSearchRecyclerViewAdapter(getActivity(), storeInfoArrayList);
        mRecyclerView.setAdapter(myAdapter);
    }
}