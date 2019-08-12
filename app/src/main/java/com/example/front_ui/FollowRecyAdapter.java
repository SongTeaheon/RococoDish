package com.example.front_ui;

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

import com.example.front_ui.DataModel.FollowInfo;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.GlidePlaceHolder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FollowRecyAdapter extends RecyclerView.Adapter<FollowRecyAdapter.FollowViewHolder> {

    private  String TAG = "TAGFollowRecyAdapter";
    Context context;
    List<FollowInfo> list;
    String userUUID;
    String collectionName;

    public FollowRecyAdapter(Context context,
                             String userUUID,
                             String collectionName){
        this.context = context;
        this.userUUID = userUUID;
        this.collectionName = collectionName;

        list = new ArrayList<>();
        getFollowData();
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView profileUpper;
        TextView profileLower;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profileImage = itemView.findViewById(R.id.profileImage_imageview_followRecy);
            this.profileUpper = itemView.findViewById(R.id.profileUpper_textview_followRecy);
            this.profileLower = itemView.findViewById(R.id.profileLower_textview_followRecy);
        }
    }


    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_follow_item, viewGroup, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder followViewHolder, final int i) {

        @Nullable String imagePath = list.get(i).getProfileImagePath();
        GlideApp.with(context)
                .load(imagePath != null? imagePath : R.drawable.basic_user_image)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                .into(followViewHolder.profileImage);


        followViewHolder.profileUpper.setText(list.get(i).getProfileTextUpper());

        followViewHolder.profileLower.setText(list.get(i).getProfileTextLower());

        followViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyPage.class);
                intent.putExtra("userUUID", list.get(i).getUserUID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getFollowData(){
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(userUUID)
                .collection(collectionName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots != null){

                            list.clear();//삭제 반영을 위해서 미리 깨끗하게 지움(아이템 중복 방지)

                            for(DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){

                                String docId = dc.getId();


                                FirebaseFirestore.getInstance()
                                        .collection("사용자")
                                        .document(docId)
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                if(e != null){
                                                    Log.d(TAG, e.getMessage());
                                                }
                                                if(documentSnapshot.exists() && documentSnapshot != null){

                                                    @Nullable String imagePath = (String) documentSnapshot.get("profileImage");
                                                    String name = documentSnapshot.get("nickname").toString();
                                                    String email = documentSnapshot.get("eMail").toString();
                                                    String uid = documentSnapshot.getId();

                                                    list.add(new FollowInfo(imagePath, name, email, uid));

//                                                    notifyItemChanged(list.size()-1);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                            notifyDataSetChanged();

                        }
                    }
                });
    }
}
