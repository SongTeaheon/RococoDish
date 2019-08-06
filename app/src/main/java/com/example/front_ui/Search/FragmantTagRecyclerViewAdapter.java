package com.example.front_ui.Search;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.AlgoliaTagData;
import com.example.front_ui.R;

import java.util.ArrayList;

public class FragmantTagRecyclerViewAdapter extends RecyclerView.Adapter<FragmantTagRecyclerViewAdapter.ItemViewHolder> {

    private ArrayList<AlgoliaTagData> listData = new ArrayList<>();

    public FragmantTagRecyclerViewAdapter(Context mContext, ArrayList<AlgoliaTagData> list) {
        this.listData = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tag_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));

        AlgoliaTagData tagData =  listData.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: TAG페이지로 넘어가는 부분.
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(AlgoliaTagData data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewTag;
        private TextView itemTagName;
        private TextView itemTagPosts;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemTagName = itemView.findViewById(R.id.itemTagName);
            itemTagPosts = itemView.findViewById(R.id.itemTagPostNum);
        }

        void onBind(AlgoliaTagData tagData) {
            itemTagName.setText(tagData.getText());
            String postNumStr = "게시글 "+ tagData.getPostingNum()+"개";
            itemTagPosts.setText(postNumStr);
        }

        // 각 지역 누르면 그 태그가 있는 게시물 들을 마이페이지 처럼 2 X 무한대로 띄움

    }
}
