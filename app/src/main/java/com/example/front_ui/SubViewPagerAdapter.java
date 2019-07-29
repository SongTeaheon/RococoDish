package com.example.front_ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.GlidePlaceHolder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class SubViewPagerAdapter extends PagerAdapter {

    private String TAG = "TAGSubViewPagerAdapter";
    private ArrayList<PostingInfo> list;
    private Context context;

    ImageView image;

    //생성자
    public SubViewPagerAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
        getRandomData();//랜덤으로 데이터 가져오는 메서드
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_sub_viewpager_page, container, false);

        image = view.findViewById(R.id.image_viewpager_activity_sub);

        StorageReference filePath = FirebaseStorage.getInstance()
                .getReferenceFromUrl(list.get(position).imagePathInStorage);
        GlideApp.with(context)
                .load(filePath)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                .into(image);

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }


    //랜덤으로 포스팅 가져오는 메서드
    private void getRandomData(){

        FirebaseFirestore.getInstance().collection("포스팅")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if(!queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                PostingInfo postingInfo = dc.toObject(PostingInfo.class);

                                list.add(postingInfo);

                                notifyDataSetChanged();
                            }

                        }
                    }
                });
    }
}
