package com.example.front_ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FollowViewPagerAdapter extends FragmentPagerAdapter {

    String userUUID;

    public FollowViewPagerAdapter(FragmentManager fm, String userUUID) {
        super(fm);
        this.userUUID = userUUID;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        switch (i){
            case 0:
                FollowerFragment followerFragment = new FollowerFragment();
                bundle.putString("userUUID", userUUID);
                followerFragment.setArguments(bundle);
                return followerFragment;
            default:
                FollowingFragment followingFragment = new FollowingFragment();
                bundle.putString("userUUID", userUUID);
                followingFragment.setArguments(bundle);
                return followingFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "팔로워";
        }
        return "팔로잉";
    }
}
