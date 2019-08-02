package com.rococodish.front_ui.PostingProcess;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rococodish.front_ui.DataModel.KakaoStoreInfo;
import com.rococodish.front_ui.R;
import java.util.ArrayList;

public class StoreSearchInViewPager extends Fragment {

    ArrayList<KakaoStoreInfo> arraylist;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_search_frag_store, container, false);
        if (getArguments() != null) {
            arraylist = getArguments().getParcelableArrayList("arraylist");
        }
        recyclerView = view.findViewById(R.id.recyclerview_fragStoreSearchStore);
        StoreSearchAdapterInViewPager fragDataAdapter = new StoreSearchAdapterInViewPager(getActivity(), arraylist);
        fragDataAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(fragDataAdapter);

        return view;
    }
}


