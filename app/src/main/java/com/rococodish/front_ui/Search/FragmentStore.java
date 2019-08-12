package com.rococodish.front_ui.Search;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.R;

import java.util.ArrayList;

public class FragmentStore extends Fragment {

    final private String TAG = "TAGFragmentStore";
    private RecyclerView recyclerView;
    private FragmantStoreRecyclerViewAdapter adapter;
    private ArrayList<StoreInfo> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OncreateView");
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.search_fragment_store, container, false);
        rootView.bringToFront();
        list = ((SubSearchPage)getActivity()).getStoreList();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.storeRecyclerView);
        if(!list.isEmpty()) { //TODO: 지역 검색에서 아무 데이터가 없는 경우 임시 처리. 전체검색 UI나오면 변경!
            adapter = new FragmantStoreRecyclerViewAdapter(getActivity(), list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        return rootView;
    }
}
