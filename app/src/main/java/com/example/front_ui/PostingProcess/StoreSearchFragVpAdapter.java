package com.example.front_ui.PostingProcess;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.front_ui.DataModel.KakaoStoreInfo;

import java.util.ArrayList;

public class StoreSearchFragVpAdapter extends FragmentStatePagerAdapter {

    final private String TAG = "TAGStoreSearchVPAdapter";
    ArrayList<KakaoStoreInfo> dataList_store;
    ArrayList<KakaoStoreInfo> dataList_cafe;

    public StoreSearchFragVpAdapter(FragmentManager fm, ArrayList<KakaoStoreInfo> dataList_store, ArrayList<KakaoStoreInfo> dataList_cafe) {
        super(fm);
        Log.d(TAG, "StoreSearchFragVpAdapter constructor");
        this.dataList_store = dataList_store;
        this.dataList_cafe = dataList_cafe;
//        this.notifyDataSetChanged();

    }



    @Override
    public Fragment getItem(int i) {
        Log.d(TAG, "StoreSearchFragVpAdapter getItem");
        if (i == 0) {//가게 프레그먼트
            StoreSearchInViewPager fragment =  new StoreSearchInViewPager();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("arraylist", dataList_store);
            fragment.setArguments(bundle);
            return fragment;
        }else {//카페 프레그먼트
            StoreSearchInViewPager fragment =  new StoreSearchInViewPager();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("arraylist", dataList_cafe);
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Log.d(TAG, "StoreSearchFragVpAdapter getItem");
        return POSITION_NONE;//TODO:https://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "가게";
        }
        else{
            return "카페";
        }
    }
}
