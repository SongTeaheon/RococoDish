package com.example.front_ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.FragmentTagData;

import java.util.ArrayList;

public class FragmantTagRecyclerViewAdapter extends RecyclerView.Adapter<FragmantTagRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<FragmentTagData> listData = new ArrayList<>();

    public FragmantTagRecyclerViewAdapter(ArrayList<FragmentTagData> list) {
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragmentTag_item, parent, false);
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

    void addItem(FragmentTagData data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewTag;
        private TextView itemTagName;
        private TextView itemTagPosts;

        ItemViewHolder(View itemView) {
            super(itemView);

            imageViewTag = itemView.findViewById(R.id.imageViewTag);
            itemTagName = itemView.findViewById(R.id.itemTagName);
            itemTagPosts = itemView.findViewById(R.id.itemTagPosts);
        }

        void onBind(FragmentTagData fragmentSotreData) {
            imageViewTag.setImageResource(fragmentSotreData.getImageViewTag()); // to do 임시로 이미지 int로 해놓았습니다 ㅜㅜ
            itemTagName.setText(fragmentSotreData.getItemTagName());
            itemTagPosts.setText(fragmentSotreData.getItemTagPosts());
        }

        // 각 지역 누르면 그 태그가 있는 게시물 들을 마이페이지 처럼 2 X 무한대로 띄움

    }
}
