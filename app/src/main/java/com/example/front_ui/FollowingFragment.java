package com.example.front_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.front_ui.DataModel.FollowInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
