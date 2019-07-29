package com.example.front_ui.Search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentSotreData;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.R;

import java.util.ArrayList;

public class FragmantStoreRecyclerViewAdapter extends RecyclerView.Adapter<FragmantStoreRecyclerViewAdapter.ItemViewHolder> {

    final private String TAG = "TAGFragmentStoreAdapter";
    private ArrayList<StoreInfo> listData;

    public FragmantStoreRecyclerViewAdapter(Context mConxtext, ArrayList<StoreInfo> list) {
        Log.d(TAG, "FragmantStoreRecyclerViewAdapter constructor");
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_store_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(StoreInfo data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView itemStoreName;
        private TextView itemStoreDistance;
        private TextView itemStoreAddress;
        private TextView itemStorePosts;
        private TextView itemStoreScore;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemStoreName = itemView.findViewById(R.id.itemStoreName);
//            itemStoreDistance = itemView.findViewById(R.id.itemStoreDistance);
            itemStoreAddress = itemView.findViewById(R.id.itemStoreAddress);
//            itemStorePosts = itemView.findViewById(R.id.itemStorePosts);
//            itemStoreScore = itemView.findViewById(R.id.itemStoreScore);
        }

        void onBind(StoreInfo storeInfo) {
            itemStoreName.setText(storeInfo.getName());
//            itemStoreDistance.setText(String.valueOf(storeInfo.getStoreDistance()));
            itemStoreAddress.setText(storeInfo.getAddress());
//            itemStorePosts.setText(storeInfo.getStorePosts());
//            itemStoreScore.setText(String.valueOf(storeInfo.getAver_star()));
        }

        //여기에 각 가게 터치 시 각 가게 페이지로 넘어가도록
    }
}
