package com.example.front_ui.Search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentPeopleData;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.R;

import java.util.ArrayList;

public class FragmantPeopleRecyclerViewAdapter extends RecyclerView.Adapter<FragmantPeopleRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<UserInfo> listData;

    public FragmantPeopleRecyclerViewAdapter(Context mContext, ArrayList<UserInfo> list) {
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_people_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
        UserInfo userInfo = listData.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(UserInfo data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewPeople;
        private TextView itemPeopleName;
        private TextView itemPeoplePosts;

        ItemViewHolder(View itemView) {
            super(itemView);

            imageViewPeople = itemView.findViewById(R.id.imageViewPeople);
            itemPeopleName = itemView.findViewById(R.id.itemPeopleName);
//            itemPeoplePosts = itemView.findViewById(R.id.itemPeoplePosts);
        }

        void onBind(UserInfo userInfo) {
            //TODO:이거 데이터 어떻게 가져오는지 태완님한테 물어봐야할 듯
            itemPeopleName.setText(userInfo.getNickname());

        }
    }
}
