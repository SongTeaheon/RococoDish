package com.example.front_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.front_ui.DataModel.FragmentTagData;

import java.util.ArrayList;

public class FragmentTag extends Fragment {

    private RecyclerView recyclerView;
    private FragmantTagRecyclerViewAdapter adapter;
    private ArrayList<FragmentTagData> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.search_fragment_tag, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.tagRecyclerView);

        adapter = new FragmantTagRecyclerViewAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    //각 태그 누르면 그 태그가 있는 가게들 게시글 쭉 뜨는 FragmentTag2로 넘어가야 합니다다
}
