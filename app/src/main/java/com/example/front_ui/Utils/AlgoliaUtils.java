package com.example.front_ui.Utils;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.front_ui.DataModel.StoreInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlgoliaUtils {
    private static final String AlgoliaApplicationID = "TYMUUCLMND";
    private static final String AlgoliaAdminAPIKey = "c4c2b2d45e16459c1eb2322e74e31b86";
    public static Client client = new Client(AlgoliaApplicationID, AlgoliaAdminAPIKey);
    public static Index storeIndex = client.getIndex("store");

    public static void addObject(StoreInfo storeInfo){
        Map<String, Object> storeMap = new HashMap<>();
        storeMap.put("name", storeInfo.getName());
        storeMap.put("storeId", storeInfo.getStoreId());

        storeIndex.addObjectAsync(new JSONObject(storeMap), null);

    }



}
