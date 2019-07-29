package com.example.front_ui.Search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentRegionData;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.MyPage;
import com.example.front_ui.R;

import java.util.ArrayList;

public class FragmantRegionRecyclerViewAdapter extends RecyclerView.Adapter<FragmantRegionRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<SearchedData> listData = new ArrayList<>();

    public FragmantRegionRecyclerViewAdapter(Context mContext, ArrayList<SearchedData> list) {
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
        SearchedData searchedData = listData.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 가게 페이지로 넘어가는 부분. storeInfo는 정확하지 않아서 아이디만 사용해야할 거 같아요
                //여기에서 lat과 lon을 보내줘야함.
//                Intent intent = new Intent(mContext, MyPage.class);
//                intent.putExtra("userUUID", userInfo.getUserId());
//                intent.putExtra("latitude", currentLat);
//                intent.putExtra("longitude", currentLon);
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(SearchedData data) {
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
//            itemRegionDistance = itemView.findViewById(R.id.itemRegionDistance);
            itemRegionAddress = itemView.findViewById(R.id.itemRegionAddress);
//            itemRegionPosts = itemView.findViewById(R.id.itemRegionPosts);
        }

        void onBind(SearchedData searchedData) {
//            imageViewRegion.setImageResource(searchedData.getImageViewRegion()); // to do 임시로 이미지 int로 해놓았습니다 ㅜㅜ
            itemRegionName.setText(searchedData.getPlace_name());
//            itemRegionDistance.setText(String.valueOf(searchedData.getItemRegionDistance()));
            itemRegionAddress.setText(searchedData.getAddress());
//            itemRegionPosts.setText(searchedData.getItemRegionPosts());
        }

        // 각 지역 누르면 각 지역 가게 뜨는 화면으로

    }
}
