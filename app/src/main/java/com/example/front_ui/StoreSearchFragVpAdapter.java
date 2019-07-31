package com.example.front_ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.front_ui.PostingProcess.StoreSearchFragCafe;
import com.example.front_ui.PostingProcess.StoreSearchFragStore;

public class StoreSearchFragVpAdapter extends FragmentPagerAdapter {


    public StoreSearchFragVpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {//가게 프레그먼트
            return new StoreSearchFragStore();
        }//카페 프레그먼트
        return new StoreSearchFragCafe();
    }

    @Override
    public int getCount() {
        return 2;
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
