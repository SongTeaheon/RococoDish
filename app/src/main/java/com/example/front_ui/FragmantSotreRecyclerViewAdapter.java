package com.example.front_ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentSotreData;

import java.util.ArrayList;

public class FragmantSotreRecyclerViewAdapter extends RecyclerView.Adapter<FragmantSotreRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<FragmentSotreData> listData = new ArrayList<>();

    public FragmantSotreRecyclerViewAdapter(ArrayList<FragmentSotreData> list) {
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragmentstore_item, parent, false);
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

    void addItem(FragmentSotreData data) {
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
            itemStoreDistance = itemView.findViewById(R.id.itemStoreDistance);
            itemStoreAddress = itemView.findViewById(R.id.itemStoreAddress);
            itemStorePosts = itemView.findViewById(R.id.itemStorePosts);
            itemStoreScore = itemView.findViewById(R.id.itemStoreScore);
        }

        void onBind(FragmentSotreData fragmentSotreData) {
            itemStoreName.setText(fragmentSotreData.getStoreName());
            itemStoreDistance.setText(String.valueOf(fragmentSotreData.getStoreDistance()));
            itemStoreAddress.setText(fragmentSotreData.getStoreAddress());
            itemStorePosts.setText(fragmentSotreData.getStorePosts());
            itemStoreScore.setText(String.valueOf(fragmentSotreData.getStoreScore()));
        }

        //여기에 각 가게 터치 시 각 가게 페이지로 넘어가도록
    }
}
