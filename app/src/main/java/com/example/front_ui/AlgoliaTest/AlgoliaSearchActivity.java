//package com.example.front_ui.AlgoliaTest;
//
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//
//import com.algolia.search.saas.AlgoliaException;
//import com.algolia.search.saas.Client;
//import com.algolia.search.saas.CompletionHandler;
//import com.algolia.search.saas.Index;
//import com.algolia.search.saas.Query;
//import com.example.front_ui.DataModel.StoreInfo;
//import com.example.front_ui.R;
//import com.example.front_ui.Utils.MathUtil;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AlgoliaSearchActivity extends AppCompatActivity {
//    private final String TAG = "AlgoliaSearchActivity";
//    private final String algoliaApplicationID = "TYMUUCLMND";
//    private final String algoliaApiKey = "72b0a80045f2596e93b60ab3f038a8f9";
//    private final String algoliaIndexName = "store_index";
//    Client client;
//    Index index;
//
//    FirebaseFirestore db;
//    FirebaseStorage storage;
//    StorageReference storageReference;
//
//    List<JSONObject> array = new ArrayList<>();
//
//    EditText editText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_algolia_search);
//
//        db = FirebaseFirestore.getInstance();
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//
//        editText = findViewById(R.id.editText);
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                Query query = new Query(editable.toString())
//                        .setAttributesToRetrieve("name")
//                        .setHitsPerPage(50);
//                index.searchAsync(query, new CompletionHandler() {
//                    @Override
//                    public void requestCompleted(JSONObject content, AlgoliaException error) {
//                        Log.d(TAG, content.toString());
////                        try {
////                            JSONArray hits = content.getJSONArray("hits");
////                            List<String> list = new ArrayList<>();
////                            for(int i =0; i< hits.length(); i++){
////                                JSONObject jsonObject = hits.getJSONObject(i);
////                                String store_name = jsonObject.getString("name");
////                                list.add(store_name);
////                            }
////
////                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
////                        } catch (JSONException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                });
//            }
//        });
//
//        SetSearchStoreName();
//    }
//
//    /*
//     * 검색 기능! - 가게 검색
//     *
//     * */
//
//    private void SetSearchStoreName(){
//
//        client = new Client(algoliaApplicationID, algoliaApiKey);
//        index = client.getIndex(algoliaIndexName);
//        //db다 가져오기.
//        db.collection("가게")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                StoreInfo storeInfo = document.toObject(StoreInfo.class);
//                                Map<String, Object> mapOne = new HashMap<>();
//                                mapOne.put("name", storeInfo.name);
//                                array.add(new JSONObject(mapOne));
//
//                            }
//                             index.addObjectsAsync(new JSONArray(array), null);
//                            Log.d(TAG, "setSearchStorename is done");
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//
//
//
//    }
//
//}
