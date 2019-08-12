package com.rococodish.front_ui.Search;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rococodish.front_ui.DataModel.UserInfo;
import com.rococodish.front_ui.R;

import java.util.ArrayList;

public class FragmentPeople extends Fragment {

    private RecyclerView recyclerView;
    private FragmantPeopleRecyclerViewAdapter adapter;
    private ArrayList<UserInfo> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.search_fragment_people, container, false);
        list = ((SubSearchPage)getActivity()).getPeopleList();
        double lat = ((SubSearchPage)getActivity()).getLat();
        double lon = ((SubSearchPage)getActivity()).getLon();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.peopleRecyclerView);
        adapter = new FragmantPeopleRecyclerViewAdapter(getActivity(), list, lat, lon);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
