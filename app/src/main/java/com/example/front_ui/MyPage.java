package com.example.front_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.Interface.MyPageDataPass;
import com.example.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

//interface for datapass to MyPage
public class MyPage extends AppCompatActivity implements MyPageDataPass {

    private final String TAG = "TAGMyPage";
    int numOfMyPost = 0;
    TextView tvOfNum;
    @Override
    public void setNumberOfData(int value) {
        Log.d(TAG, "setNumberOfData - callback function");
        numOfMyPost = value;
        if(tvOfNum == null)
            tvOfNum = findViewById(R.id.textNumOfData);
        tvOfNum.setText(Integer.toString(numOfMyPost));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page);
        ImageButton imageButton = findViewById(R.id.imageView);

        MyAdapter adapter = new MyAdapter(
                getApplicationContext(),
                R.layout.polar_style,
                this);       // GridView 항목의 레이아웃 row.xml


        GridView gv = findViewById(R.id.gridview);
        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용
        TextView tv = findViewById(R.id.textNumOfData);

        imageButton.setOnClickListener(new View.OnClickListener() {
            final String[] profiles = new String[] {"사진촬영", "앨범에서 사진 선택", "기본 이미지로 변경"};
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView :
                        new AlertDialog.Builder(MyPage.this)
                            .setTitle("프로필")
                            .setItems(profiles, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (profiles[which]) {
                                        case "사진촬영":
                                            Toast.makeText(MyPage.this, "사진촬영", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "앨범에서 사진 선택":
                                            Toast.makeText(MyPage.this, "앨범에서 사진 선택", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "기본 이미지로 변경":
                                            Toast.makeText(MyPage.this, "기본 이미지로 변경", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            })
                            .show();
                        break;
                }
            }
        });

        imageButton.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            imageButton.setClipToOutline(true);
        }



    }
}

class MyAdapter extends BaseAdapter {
    private final String TAG = "TAGMyAdapter";
    FirebaseFirestore db;

    Context context;
    int layout;
    ArrayList<PostingInfo> list;
    LayoutInflater inf;
    FirebaseStorage storage;
    StorageReference storageReference;

    //interface for datapass to MyPage
    private MyPageDataPass mCallback;


    public MyAdapter(Context context, int layout, MyPageDataPass listener) {
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        this.context = context;
        this.layout = layout;
        mCallback = listener;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        getPostingDataFromCloud();
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


    //이미지 세팅
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imagefood);

        final PostingInfo singleItem = list.get(position);
        Log.d(TAG, "downloadImageFromFirebaseStorage : " + singleItem.imagePathInStorage);
        StorageReference fileReference = storage.getReferenceFromUrl(singleItem.imagePathInStorage);
        GlideApp.with(context).load(fileReference).into(iv);


        return convertView;
    }

    //내가 쓴 데이터를 모두 가져온다.
    private void getPostingDataFromCloud() {
        Log.d(TAG, "getDataFromFirestore");

        db.collection("포스팅")
                .whereEqualTo("writerId", FirebaseAuth.getInstance().getCurrentUser().getEmail())
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
                            Log.d(TAG, "getPostingData size : " + task.getResult().size());
                            mCallback.setNumberOfData(task.getResult().size());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}

