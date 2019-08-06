package com.example.front_ui.PostingProcess;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.R;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(fragDataAdapter);

        return view;
    }
}


