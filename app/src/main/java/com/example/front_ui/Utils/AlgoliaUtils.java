package com.example.front_ui.Utils;

import android.util.Log;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Interface.AlgoliaSearchPredicate;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlgoliaUtils {
    private static final String TAG = "TAGAlgoliaUtils";
    private static final String AlgoliaApplicationID = "TYMUUCLMND";
    private static final String AlgoliaAdminAPIKey = "c4c2b2d45e16459c1eb2322e74e31b86";
    public static Client client = new Client(AlgoliaApplicationID, AlgoliaAdminAPIKey);

    public static void addObject(String indexName, Object data){
        Index index = client.getIndex(indexName);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(data);
        Log.d(TAG, jsonStr);
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            index.addObjectAsync(json, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //검색한다. 어떤 인덱스에서 어떤 어트리뷰트에, 어떤 단어가 있으면 검색한다. 검색 결과를 받으면 listArray로 받는다. 이 때 각 list에는 name, 주소, id, object id가 있어야한다.
    public static void searchStore(String indexName, String attr, String searchText, final AlgoliaSearchPredicate asp){
        Index index = client.getIndex(indexName);
        Query query = new Query(searchText)
                .setAttributesToRetrieve(attr)
                .setHitsPerPage(50);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                Log.d(TAG, "query result : " + content.toString());
                try {
                    JSONArray hits = content.getJSONArray("hits");
                    asp.gettingJSONArrayCompleted(hits);//hits로 하고 싶은거 다해!! 직접 만들것.
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
