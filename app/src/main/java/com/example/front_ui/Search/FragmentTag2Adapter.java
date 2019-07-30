package com.example.front_ui.Search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.DataModel.StorePostInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.GlideApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FragmentTag2Adapter extends BaseAdapter {

    private String TAG = "TAGFragmentTag2Adapter";
    Context context;
    List<StorePostInfo> list;
    ImageView foodImageview;
    TextView starScore;
    TextView likeNum;
    String tag;

    //생성자
    public FragmentTag2Adapter(Context context,
                               String tag){

        list = new ArrayList<>();

        this.context = context;
        this.tag = tag;

        getPostDataWithTag(tag);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.store_page_item, parent, false);

        foodImageview = convertView.findViewById(R.id.imagefood0);
        starScore = convertView.findViewById(R.id.storePageScore);
        likeNum = convertView.findViewById(R.id.storePageLike);


        //이미지 불러오기
        StorageReference filePath = FirebaseStorage.getInstance().getReferenceFromUrl(list.get(position).getPostImagePath());
        GlideApp.with(context)
                .load(filePath)
                .into(foodImageview);

        //별점 불러오기
        starScore.setText(list.get(position).getStarNum());

        //좋아요 개수 불러오기
        likeNum.setText(list.get(position).getLikeNum());


        //뷰 누를경우 DishView로 이동
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : postingInfo와 StoreInfo를 전달하면서 DishView로 이동
            }
        });

        return convertView;
    }
    //데이터를 불러오는데 검색한 태그 기준으로만 불러와야함.
    private void getPostDataWithTag(String tag){
        FirebaseFirestore.getInstance()
                .collection("포스팅")
//                .whereEqualTo(tag, tag)//todo : 태그를 map으로 저장하니 못빼옵니다.
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if(!queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc:  queryDocumentSnapshots.getDocuments()){

                                StorePostInfo storePostInfo = dc.toObject(StorePostInfo.class);

                                list.add(storePostInfo);
                            }
                            notifyDataSetChanged();

                        }
                    }
                });
    }
}
