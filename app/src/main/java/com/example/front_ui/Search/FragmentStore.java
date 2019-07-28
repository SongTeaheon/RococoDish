package com.example.front_ui.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.R;

import java.util.ArrayList;

public class FragmentStore extends Fragment {

    private RecyclerView recyclerView;
    private FragmantStoreRecyclerViewAdapter adapter;
    private ArrayList<StoreInfo> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.search_fragment_store, container, false);
        list = ((SubSearchPage)getActivity()).getStoreList();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.storeRecyclerView);
        if(!list.isEmpty()) { //TODO: 지역 검색에서 아무 데이터가 없는 경우 임시 처리. 전체검색 UI나오면 변경!
            adapter = new FragmantStoreRecyclerViewAdapter(getContext(), list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        return rootView;
    }
}
