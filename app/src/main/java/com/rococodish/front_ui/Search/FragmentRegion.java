package com.rococodish.front_ui.Search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rococodish.front_ui.DataModel.SearchedData;
import com.rococodish.front_ui.R;

import java.util.ArrayList;

public class FragmentRegion extends Fragment {

    private RecyclerView recyclerView;
    private FragmantRegionRecyclerViewAdapter adapter;
    private ArrayList<SearchedData> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_fragment_region, container, false);

        list = ((SubSearchPage)getActivity()).getRegionList();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.regionRecyclerView);

        adapter = new FragmantRegionRecyclerViewAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
