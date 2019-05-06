package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.front_ui.DataModel.PostingInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Recyclerview_myPage_Adapter extends RecyclerView.Adapter<Recyclerview_myPage_Adapter.myPageItemHolder> {

    private ArrayList<PostingInfo> list;
    private Context context;
    private static final String TAG = "TAG_myPage_Adapter";
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    public Recyclerview_myPage_Adapter(Context context){
        Log.d(TAG, "어댑터의 constructor가 불러졌습니다.");

        list = new ArrayList<>();
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }


    @NonNull
    @Override
    public myPageItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.polar_style, null);
        myPageItemHolder holder = new myPageItemHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final myPageItemHolder itemRowHolder, int i) {
        itemRowHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "여기다가 디비에 있는 이미지 끌어와서 글라이드로 붙이면 될듯", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != list? list.size() : 0);
    }

    public class myPageItemHolder extends RecyclerView.ViewHolder{

        private ImageView imageview;

        public myPageItemHolder(@NonNull View itemView) {
            super(itemView);

            Log.d(TAG, "홀더클래스");
            this.imageview = itemView.findViewById(R.id.imagefood);
        }
    }
}
