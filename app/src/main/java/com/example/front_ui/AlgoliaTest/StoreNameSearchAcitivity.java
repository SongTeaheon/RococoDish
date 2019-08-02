package com.example.front_ui.AlgoliaTest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Interface.AlgoliaSearchPredicate;
import com.example.front_ui.R;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.JsonParsing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class StoreNameSearchAcitivity extends AppCompatActivity {
    private final String TAG = "TAGStoreNameSearchActi";
    ListView listView;
    EditText editText;
    Button button_copy;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_name_search_acitivity);

        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.listview_record);
        editText = findViewById(R.id.editText_storeNameSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                AlgoliaUtils.searchData("store", "name", editable.toString(), new AlgoliaSearchPredicate() {
                            @Override
                            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                                List<StoreInfo> storeList =  JsonParsing.getStoreListFromJsonList(jsonArray);
                                ArrayList<String> nameList = new ArrayList<>();
                                for(int i = 0; i < storeList.size(); i++){
                                    nameList.add(storeList.get(i).getName());
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nameList);
                                listView.setAdapter(adapter);
                            }
                        });
//                Query query = new Query(editable.toString())
//                        .setAttributesToRetrieve("name", "storeId")
//                        .setHitsPerPage(50);
//                AlgoliaUtils.storeIndex.searchAsync(query, new CompletionHandler() {
//                    @Override
//                    public void requestCompleted(JSONObject content, AlgoliaException error) {
//                        Log.d(TAG, "query result : " + content.toString());
//                        try {
//                            JSONArray hits = content.getJSONArray("hits");
//                            List<String> list = new ArrayList<>();
//                            for(int k = 0; k < hits.length(); k++){
//                                JSONObject jsonObject = hits.getJSONObject(k);
//                                String storeName = jsonObject.getString("name");
//                                list.add(storeName);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
//                            listView.setAdapter(adapter);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
            }
        });


        button_copy = findViewById(R.id.button_copy);
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendStoreDataToAlgolia();
            }
        });



    }
    private void sendStoreDataToAlgolia() {
        Log.d(TAG, "get all store data");

//        db.collection("가게")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getString("name"));
//                                StoreInfo storeInfo = document.toObject(StoreInfo.class);
//
//                                AlgoliaUtils.addObject("store", storeInfo);
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

        db.collection("사용자")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getString("nickname"));
                                UserInfo userInfo = document.toObject(UserInfo.class);
                                userInfo.setUid(document.getId());

                                AlgoliaUtils.addObject("user", userInfo);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
