package com.rococodish.front_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FollowerFragment extends Fragment {

    String TAG = "TAGFollowerFrag";
    RecyclerView followerRecycler;
    FollowRecyAdapter followRecyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        String userUUID = getArguments().getString("userUUID");

        followerRecycler = view.findViewById(R.id.followerList_recyclerview_followerFrag);
        followRecyAdapter = new FollowRecyAdapter(getActivity(), userUUID, "팔로워");
        followerRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        followerRecycler.setAdapter(followRecyAdapter);

        return view;

    }
}
