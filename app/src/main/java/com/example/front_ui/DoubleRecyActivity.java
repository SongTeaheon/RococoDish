package com.example.front_ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.front_ui.DataModel.StoreInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class DoubleRecyActivity extends AppCompatActivity{

    String TAG = "TAG_DoubleRecyActivity";
    RecyclerView parentRecy;
    DoubleRecyAdapter1 doubleRecyAdapter1;
    List<StoreInfo> parentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_recy);

        parentRecy = findViewById(R.id.parentRecy);
        parentRecy.setLayoutManager(new LinearLayoutManager(DoubleRecyActivity.this, RecyclerView.VERTICAL, false));
        doubleRecyAdapter1 = new DoubleRecyAdapter1(DoubleRecyActivity.this, parentList);
        parentRecy.setAdapter(doubleRecyAdapter1);



        FirebaseFirestore.getInstance()
                .collection("가게")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.d(TAG, e.getMessage());
                        }

                        if (!queryDocumentSnapshots.isEmpty()){

                            for (DocumentSnapshot dc: queryDocumentSnapshots.getDocuments()){

                                StoreInfo storeInfo = dc.toObject(StoreInfo.class);

                                parentList.add(storeInfo);
                                doubleRecyAdapter1.notifyItemChanged(parentList.size());
                            }

                        }
                    }
                });

    }
}
