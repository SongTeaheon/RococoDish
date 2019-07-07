package com.example.front_ui.Utils;

import android.util.Log;

import com.example.front_ui.DataModel.KakaoLocalInfo;
import com.example.front_ui.DataModel.KakaoMetaData;
import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.DataModel.StoreInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParsing {

    private static final String TAG ="TAGJsonParsing";
    //kakao api에서 total count를 받아온다.

    public static int getCountFromApi(JsonObject jsonObject) {
        Gson gson = new Gson();

        JsonObject jsonObject_meta = (JsonObject) jsonObject.get("meta");
        KakaoMetaData metaOb = gson.fromJson(jsonObject_meta, KakaoMetaData.class);
        int total_count = Integer.parseInt(metaOb.getTotal_count());

        Log.d(TAG, "meta data info.  total count: " +  metaOb.getTotal_count());
        return total_count;
    }

    //kakao api에서 받아온 jsonObject를 파싱해서 ArrayList<>로 변경
    public static ArrayList<KakaoStoreInfo> parseJsonToStoreInfo(JsonObject jsonObject) {
        Log.d(TAG, "parseJsonToStoreInfo");
        ArrayList<KakaoStoreInfo> dataList = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray jsonArray = (JsonArray) jsonObject.get("documents");
        for(int i=0 ; i<jsonArray.size(); i++){
            KakaoStoreInfo object = gson.fromJson(jsonArray.get(i), KakaoStoreInfo.class);
            Log.d(TAG, "info  : " + object.place_name);
            dataList.add(object);
        }
        if(dataList.isEmpty()){
            return null;
        }
        return dataList;
    }

    //kakao api에서 받아온 jsonObject를 파싱해서 ArrayList<>로 변경 - 그 keyword를 포함한 것만 넣는다.
    public static ArrayList<SearchedData> parseJsonToSearchedInfo(JsonObject jsonObject, String keyword, int mode) {
        //mode 1 : local
        //mode 2 : store
        Log.d(TAG, "parseJsonToSearchedInfo");
        ArrayList<SearchedData> dataList = new ArrayList<>();
        Gson gson = new Gson();

        JsonArray jsonArray = (JsonArray) jsonObject.get("documents");

        for(int i=0 ; i<jsonArray.size(); i++){
            SearchedData data = new SearchedData();
            if(mode == 1){
                KakaoLocalInfo object = gson.fromJson(jsonArray.get(i), KakaoLocalInfo.class);
                data.setPlace_name(object.getAddress_name());
                data.setX(object.getX());
                data.setY(object.getY());
            }
            else{
                KakaoStoreInfo object = gson.fromJson(jsonArray.get(i), KakaoStoreInfo.class);
                data.setPlace_name(object.getPlace_name());
                data.setX(object.getX());
                data.setY(object.getY());
            }

            //해당 데이터에 keyword가 포함되어 있으면 넣는다.
            if(data.getPlace_name().contains(keyword) || data.getPlace_name().contains("대학교"))
                dataList.add(data);
        }

        return dataList;
    }


    public static List<StoreInfo> getStoreListFromJsonList(JSONArray jsonArray){
        List<StoreInfo> list = new ArrayList<>();
        for(int k = 0; k < jsonArray.length(); k++){
            String jsonStr = null;
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(k);
                jsonStr = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            StoreInfo storeInfo = gson.fromJson(jsonStr, StoreInfo.class);

            list.add(storeInfo);
        }
        return list;
    }
}
