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

public class FollowingFragment extends Fragment {

    String TAG = "TAGFollowingFrag";
    RecyclerView followingRecycler;
    FollowRecyAdapter followRecyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        String userUUID = getArguments().getString("userUUID");

        followingRecycler = view.findViewById(R.id.followingList_recyclerview_followingFrag);
        followRecyAdapter = new FollowRecyAdapter(getActivity(), userUUID, "팔로잉");
        followingRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        followingRecycler.setAdapter(followRecyAdapter);

        return view;

    }
}
