package com.rococodish.front_ui.Search;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rococodish.front_ui.DataModel.UserInfo;
import com.rococodish.front_ui.MyPage;
import com.rococodish.front_ui.R;
import com.rococodish.front_ui.Utils.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FragmantPeopleRecyclerViewAdapter extends RecyclerView.Adapter<FragmantPeopleRecyclerViewAdapter.ItemViewHolder> {

    private final String TAG = "TAGFragmentPeopleRecyc";
    private ArrayList<UserInfo> listData;
    private Context mContext;
    private double currentLat;
    private double currentLon;
    private int mode;
    FirebaseStorage storage;


    //mode가 1이면 데이터 수 제한 x, 2이면 데이터 수 4개 제한.
    public FragmantPeopleRecyclerViewAdapter(Context mContext, ArrayList<UserInfo> list, double lat, double lon/*, int mode*/) {
        storage = FirebaseStorage.getInstance();
        this.listData = list;
        this.mContext = mContext;
        this.currentLat = lat;
        this.currentLon = lon;
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
        final UserInfo userInfo = listData.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "item is cliceked. go to MyPage userid : " + userInfo.getUid());
                Intent intent = new Intent(mContext, MyPage.class);
                intent.putExtra("userUUID", userInfo.getUid());
                mContext.startActivity(intent);
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
        private TextView itemPeopleEmail;

        ItemViewHolder(View itemView) {
            super(itemView);
            imageViewPeople = itemView.findViewById(R.id.imageViewPeople);
            itemPeopleName = itemView.findViewById(R.id.itemPeopleName);
            itemPeopleEmail = itemView.findViewById(R.id.itemPeopleEmail);
        }

        void onBind(UserInfo userInfo) {
            //TODO:이거 데이터 어떻게 가져오는지 태완님한테 물어봐야할 듯
            Log.d(TAG, "nickname : " + userInfo.getNickname());
            Log.d(TAG, "email : " + userInfo.eMail);

            itemPeopleName.setText(userInfo.getNickname());
//            imageViewPeople;
            itemPeopleEmail.setText(userInfo.eMail);
            //profileImage는 Null!!!
            if(userInfo.profileImage != null) {
                StorageReference fileReference = storage.getReferenceFromUrl(userInfo.profileImage);
                GlideApp.with(mContext).load(fileReference).into(imageViewPeople);
            }else{
                GlideApp.with(mContext)
                        .load(R.drawable.basic_user_image)
                        .into(imageViewPeople);
            }
        }
    }
}
