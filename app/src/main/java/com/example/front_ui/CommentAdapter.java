package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.CommentInfo;
import com.example.front_ui.Utils.GlideApp;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private ArrayList<CommentInfo> list;
    private Context context;

    public CommentAdapter(Context context,
                          ArrayList<CommentInfo> list){
        this.list = list;
        this.context = context;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView question;
        public TextView answer;
        public Long time;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewProfile);
            question = itemView.findViewById(R.id.text_Q);
            answer = itemView.findViewById(R.id.text_A);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_qna_row, null);
        CommentViewHolder cv = new CommentViewHolder(view);
        return cv;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        GlideApp.with(context)
                .load(list.get(i).getImgPath())
                .into(commentViewHolder.image);

        commentViewHolder.question.setText("Q. " + list.get(i).getQuestion());
        commentViewHolder.answer.setText("A. "+ list.get(i).getAnswer());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
