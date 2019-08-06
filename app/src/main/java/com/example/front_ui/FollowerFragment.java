package com.example.front_ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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