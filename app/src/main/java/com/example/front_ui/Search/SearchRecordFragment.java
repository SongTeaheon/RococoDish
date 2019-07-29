package com.example.front_ui.Search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.front_ui.R;

import java.util.ArrayList;

public class SearchRecordFragment extends Fragment {
    private final String TAG = "TAGSearchRecordFrag";
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_record, container, false);
        listView = rootView.findViewById(R.id.listview_record);
        final ArrayList<String> recordList = ((SubSearchPage)getActivity()).getRecordList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, recordList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                ((SubSearchPage)getActivity()).searchButtonClicked(recordList.get(position));
            }
        });
        return rootView;
    }

}
