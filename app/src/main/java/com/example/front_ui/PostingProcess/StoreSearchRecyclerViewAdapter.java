package com.example.front_ui.PostingProcess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.R;


import java.util.ArrayList;

public class StoreSearchRecyclerViewAdapter extends RecyclerView.Adapter<StoreSearchRecyclerViewAdapter.ViewHolder> {

    private ArrayList<KakaoStoreInfo> kakaoStoreInfoArray;
    private LayoutInflater mInflater;
    private final String TAG = "TAGRecyclerviewAdapter";
    private Context mContext;


    public StoreSearchRecyclerViewAdapter(Context context, ArrayList<KakaoStoreInfo> kakaoStoreInfoArray) {
        this.kakaoStoreInfoArray = kakaoStoreInfoArray;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
        }

    }



    @Override
    public StoreSearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                        int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_store_search_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StoreSearchRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        TextView tv_title;
        TextView tv_category;
        TextView tv_address;

        tv_title = viewHolder.view.findViewById(R.id.tv_title);
        tv_address = viewHolder.view.findViewById(R.id.tv_address);

        tv_title.setText(kakaoStoreInfoArray.get(i).place_name);
        tv_address.setText(kakaoStoreInfoArray.get(i).address_name);
    }



    @Override
    public int getItemCount() {
        return kakaoStoreInfoArray.size();
    }
}