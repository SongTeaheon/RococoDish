package com.example.front_ui.Search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.MyPage;
import com.example.front_ui.R;
import com.example.front_ui.StorePageActivity;

import java.util.ArrayList;

public class FragmantStoreRecyclerViewAdapter extends RecyclerView.Adapter<FragmantStoreRecyclerViewAdapter.ItemViewHolder> {

    final private String TAG = "TAGFragmentStoreAdapter";
    private ArrayList<StoreInfo> listData;
    private Context mContext;

    public FragmantStoreRecyclerViewAdapter(Context context, ArrayList<StoreInfo> list) {
        Log.d(TAG, "FragmantStoreRecyclerViewAdapter constructor");
        this.listData = list;
        mContext = context;
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
        final StoreInfo storeInfo = listData.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "item is cliceked. go to storePage userid : " + storeInfo.getStoreId());
                Intent intent = new Intent(mContext, StorePageActivity.class);
                intent.putExtra("storeName", storeInfo.getName());
                intent.putExtra("documentId", storeInfo.getStoreId());
                mContext.startActivity(intent);
                ((SubSearchPage)mContext).finish();
            }
        });

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
        private TextView itemStoreAddress;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemStoreName = itemView.findViewById(R.id.itemStoreName);
            itemStoreAddress = itemView.findViewById(R.id.itemStoreAddress);
        }

        void onBind(StoreInfo storeInfo) {
            itemStoreName.setText(storeInfo.getName());
            itemStoreAddress.setText(storeInfo.getAddress());
        }

        //여기에 각 가게 터치 시 각 가게 페이지로 넘어가도록
    }
}
