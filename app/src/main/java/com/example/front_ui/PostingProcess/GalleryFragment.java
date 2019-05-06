package com.example.front_ui.PostingProcess;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.FilePaths;
import com.example.front_ui.Utils.FileSearch;


import java.util.ArrayList;


public class GalleryFragment extends Fragment {
    private static final String TAG = "TAGGalleryFragment";
    private static final String mAppend = "file:/";

    public static String selectedImgURL;

    private GridView gridView;
    private Spinner directorySpinner;
    private boolean isItemSelected = false;
    private View selectedView;

    KakaoStoreInfo kakaoStoreInfo;

    private ArrayList<String> directories;
    ArrayList<String> imgURLs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "OnCreateView : started");


        //넘어온 카카오 정보를 받는다.
        if (getArguments() != null) {
            kakaoStoreInfo = getArguments().getParcelable("StoreData");
            Log.d(TAG, "kakaoStoreInfo : " + kakaoStoreInfo.place_name);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gridView = view.findViewById(R.id.gridView);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        directories = new ArrayList<>();

        //x버튼
        ImageView shareClose = view.findViewById(R.id.backButton);
        shareClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : x버튼 눌림. storeSearch화면으로 이동");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStackImmediate();
            }
        });

        // 태완태완 next 버튼 태완님 여기 건드리시면 됩니다. 여기서 이 nextScreen버튼 누르면 사진크롭화면으로 넘어가야합니당
        //지금은 LastShareFragment로 넘어가고 있습니다.
        //태완님 그 사진크롭 끝나면 LastShareFragment로 넘어가야합니다.
        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                /*
                * GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putParcelable("StoreData", storeInfoArrayList.get(itemPosition));
        fragment.setArguments(args);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                * */
                Log.d(TAG, "onclick : navigating to the LastShareFragment(나중에는 사진크롭화면으로 바뀌어야함)");
                LastShareFragment fragment = new LastShareFragment();
                Bundle args = new Bundle();
                args.putParcelable("StoreData", kakaoStoreInfo);
                fragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.relLayout1, fragment);
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        //spinner setting
        init();

        return view;
    }


    //spinner Setting
    private void init(){
        //check for other folders inside "/storage/emulated/0/pictures
        if(FileSearch.getDirectoryPaths(FilePaths.PICTURES) != null || FileSearch.getDirectoryPaths(FilePaths.DCIM) != null){
            directories = FileSearch.getDirectoryPaths(FilePaths.DCIM);
            directories.addAll(FileSearch.getDirectoryPaths(FilePaths.PICTURES));
        }else{
            return;
        }

        Log.d(TAG, "init() : set adapter on directorySpinner");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, directories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        directorySpinner.setAdapter(adapter);



        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: selected : " + directories.get(i));
                //images 세팅
                setupGridView(directories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setupGridView(FilePaths.CAMERA);
            }
        });


    }

    //grid View setting
    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen : " + selectedDirectory);
        imgURLs = FileSearch.getFilePaths(selectedDirectory);


        //use the grid adapter to adapter the images to gridView
        GalleryGridImageAdapter adapter = new GalleryGridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);




        //set click the image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick : selected an image : " + imgURLs.get(i));

                //다른 이미지가 선택된 상태라면 그 선택된 이미지의 바탕색을 흰색으로 바꾼다.
                if(isItemSelected){
                    selectedView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                //다른 이미지가 선택될 경우 이전 이미지의 바탕색을 바꿔야하므로 저장해놓는다.
                selectedView = view;
                isItemSelected = true;

                //선택된 이미지의 바탕색을 바꾸어놓는다.
                view.setBackgroundColor(Color.parseColor("#000000"));
                selectedImgURL = imgURLs.get(i);


            }
        });
    }





}
