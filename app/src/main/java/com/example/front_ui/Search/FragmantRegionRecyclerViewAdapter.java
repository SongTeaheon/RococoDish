package com.example.front_ui.Search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private final String TAG = "TAGFragmentRegionAdap";
    private ArrayList<SearchedData> listData = new ArrayList<>();
    private Context mContext;

    public FragmantRegionRecyclerViewAdapter(Context context, ArrayList<SearchedData> list) {
        this.listData = list;
        this.mContext = context;
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
        final SearchedData searchedData = listData.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 가게 페이지로 넘어가는 부분. storeInfo는 정확하지 않아서 아이디만 사용해야할 거 같아요
                //여기에서 lat과 lon을 보내줘야함.
                Log.d(TAG, "lon : " + searchedData.getX() + " lat : " + searchedData.getY());
                Intent intent = new Intent(mContext, RegionSearchActivity.class);
                intent.putExtra("placeName", searchedData.getPlace_name());
                intent.putExtra("address", searchedData.getAddress());
                intent.putExtra("longtitude", searchedData.getX());
                intent.putExtra("latitude", searchedData.getY());
                mContext.startActivity(intent);
                ((SubSearchPage)mContext).finish();

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
            itemRegionName = itemView.findViewById(R.id.itemRegionName);
            itemRegionAddress = itemView.findViewById(R.id.itemRegionAddress);
        }

        void onBind(SearchedData searchedData) {
            itemRegionName.setText(searchedData.getPlace_name());
            itemRegionAddress.setText(searchedData.getAddress());
        }

        // 각 지역 누르면 각 지역 가게 뜨는 화면으로

    }
}
