package com.example.front_ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentRegionData;
import com.example.front_ui.DataModel.FragmentSotreData;

import java.util.ArrayList;

public class FragmantRegionRecyclerViewAdapter extends RecyclerView.Adapter<FragmantRegionRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<FragmentRegionData> listData = new ArrayList<>();

    public FragmantRegionRecyclerViewAdapter(ArrayList<FragmentRegionData> list) {
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_region_item, parent, false);
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

    void addItem(FragmentRegionData data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewRegion;
        private TextView itemRegionName;
        private TextView itemRegionDistance;
        private TextView itemRegionAddress;
        private TextView itemRegionPosts;

        ItemViewHolder(View itemView) {
            super(itemView);

            imageViewRegion = itemView.findViewById(R.id.imageViewRegion);
            itemRegionName = itemView.findViewById(R.id.itemRegionName);
            itemRegionDistance = itemView.findViewById(R.id.itemRegionDistance);
            itemRegionAddress = itemView.findViewById(R.id.itemRegionAddress);
            itemRegionPosts = itemView.findViewById(R.id.itemRegionPosts);
        }

        void onBind(FragmentRegionData fragmentSotreData) {
            imageViewRegion.setImageResource(fragmentSotreData.getImageViewRegion()); // to do 임시로 이미지 int로 해놓았습니다 ㅜㅜ
            itemRegionName.setText(fragmentSotreData.getItemRegionName());
            itemRegionDistance.setText(String.valueOf(fragmentSotreData.getItemRegionDistance()));
            itemRegionAddress.setText(fragmentSotreData.getItemRegionAddress());
            itemRegionPosts.setText(fragmentSotreData.getItemRegionPosts());
        }

        // 각 지역 누르면 각 지역 가게 뜨는 화면으로

    }
}
