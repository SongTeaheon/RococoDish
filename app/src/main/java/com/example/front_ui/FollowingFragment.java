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
    List<FollowInfo> followingList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        followingRecycler = view.findViewById(R.id.followingList_recyclerview_followingFrag);
        followRecyAdapter = new FollowRecyAdapter(getActivity(), followingList);
        followingRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        followingRecycler.setAdapter(followRecyAdapter);

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userUUID = getArguments().getString("userUUID");

        //todo : 팔로우를 취소했을 때 화면반영해줌.
        //마이페이지에서 왔을 때
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .collection("팔로잉")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if(e != null)
                            Log.d(TAG, e.getMessage());

                        if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){

                                switch (dc.getType()){
                                    case ADDED:
                                        String followerUid = dc.getDocument().getId();

                                        //유저의 프로필 정보 가져오기
                                        FirebaseFirestore.getInstance()
                                                .collection("사용자")
                                                .document(followerUid)
                                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                        String imagePath = documentSnapshot.get("profileImage").toString();
                                                        String name = documentSnapshot.get("nickname").toString();
                                                        String email = documentSnapshot.get("eMail").toString();
                                                        String uid = documentSnapshot.getId();

                                                        followingList.add(new FollowInfo(imagePath, name, email, uid));
                                                        followRecyAdapter.notifyItemChanged(followingList.size());
                                                    }
                                                });
                                }

                            }
                        }
                    }
                });
    }
}
