package com.example.front_ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.front_ui.DataModel.PostingInfo;
//import com.songtaeheon.posting.Utils.GlideApp;

import java.util.ArrayList;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> { // 가로 리사이클러뷰를 위한 어뎁터

    private ArrayList<PostingInfo> list;
    private Context mContext;
    private static final String TAG = "TAGSectionViewAdapter";
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;



    public SectionListDataAdapter(Context context,String postId, ArrayList<PostingInfo> mList) {
        this.list = mList;
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //getPostDataFromCloud(postId);
    }



    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder");

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.polar_style, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder");

        final PostingInfo singleItem = list.get(i);

        //holder.mainView.setText(singleItem.getTitle());

        Log.d(TAG, "downloadImageFromFirebaseStorage : " + singleItem.imagePathInStorage);


        StorageReference fileReference = storage.getReferenceFromUrl(singleItem.imagePathInStorage);
        //GlideApp.with(mContext).load(fileReference).into(holder.imageView);

        //태완태완 이미지 선택시 반응입니다. 여기가 그 각 포스팅1 글 누르면 발생하는 이벤트 부분입니다.
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //여기서 포스팅2로 넘어가면 됩니다.
                Toast.makeText(v.getContext(), singleItem.title, Toast.LENGTH_SHORT).show();
            }
        });


       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        //private TextView mainView;
        View view;


        public SingleItemRowHolder(View view) {
            super(view);
            this.view = view;
            Log.d(TAG, "singleItemRowHolder");
            //this.mainView = (TextView) view.findViewById(R.id.textmain);
            this.imageView = (ImageView) view.findViewById(R.id.imagefood);



        }

    }






}
