package com.rococodish.front_ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.GlidePlaceHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

    public Recyclerview_myPage_Adapter(Context context) {
        Log.d(TAG, "어댑터의 constructor가 불러졌습니다.");

        list = new ArrayList<>();
        this.context = context;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getPostingDataFromCloud();
    }

    @NonNull
    @Override
    public myPageItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.polar_style, viewGroup, false);
        myPageItemHolder holder = new myPageItemHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final myPageItemHolder itemRowHolder, int i) {
        Log.d(TAG, "onBindviewHolder position : " + i);

        GlideApp.with(context)
                .load(list.get(i).imagePathInStorage)
                .placeholder(GlidePlaceHolder.circularPlaceHolder(context))
                .into(itemRowHolder.imageview);

        //아이템 클릭시 이벤트
        itemRowHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "추후 추가 예정", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    //내가 쓴 데이터를 모두 가져온다.
    private void getPostingDataFromCloud() {
        Log.d(TAG, "getDataFromFirestore");

        db.collection("포스팅")
                .whereEqualTo("writerId", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //store 정보를 가져오고, id를 따로 저장한다.
                                PostingInfo postingInfo = document.toObject(PostingInfo.class);
                                //해당 가게 정보의 post데이터를 가져온다.
                                list.add(postingInfo);
                                notifyDataSetChanged();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
