package com.example.front_ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.front_ui.DataModel.SearchedData;

import java.util.ArrayList;

public class MainSearchRecyclerViewAdapter extends RecyclerView.Adapter<MainSearchRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<SearchedData> list;
    private static final String TAG = "TAGRecyclerViewAdapter";
    private LayoutInflater mInflater;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.tv_title);
        }
    }

    public MainSearchRecyclerViewAdapter(ArrayList<SearchedData> list) {
        Log.d(TAG, "adpater constructor called");
        this.list= list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_main_search_row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.textView.setText(list.get(i).place_name);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
}
