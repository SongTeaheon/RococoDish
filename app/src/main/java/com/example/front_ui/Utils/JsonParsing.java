package com.example.front_ui.Utils;

import android.util.Log;

import com.example.front_ui.DataModel.AlgoliaTagData;
import com.example.front_ui.DataModel.KakaoLocalInfo;
import com.example.front_ui.DataModel.KakaoMetaData;
import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.SearchedData;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.DataModel.UserInfo;
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


    public static ArrayList<StoreInfo> getStoreListFromJsonList(JSONArray jsonArray){
        ArrayList<StoreInfo> list = new ArrayList<>();
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

    public static ArrayList<UserInfo> getUserListFromJsonList(JSONArray jsonArray){
        ArrayList<UserInfo> list = new ArrayList<>();

        for(int k = 0; k < jsonArray.length(); k++){
            String jsonStr = null;
//            UserInfo userInfo = new UserInfo();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(k);
//                JSONObject jsonObject = jsonArray.getJSONObject(k).getJSONObject("_highlightResult");
//                userInfo.nickname = (String)jsonObject.get("nickname");
//                userInfo.eMail = (String)jsonObject.get("eMail");
                jsonStr = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            UserInfo userInfo = gson.fromJson(jsonStr, UserInfo.class);

            list.add(userInfo);
        }
        return list;
    }

    public static ArrayList<AlgoliaTagData> getTagListFromJsonList(JSONArray jsonArray){
        Log.d(TAG, "getTagListFromJsonList");

        ArrayList<AlgoliaTagData> list = new ArrayList<>();
        for(int k = 0; k < jsonArray.length(); k++){
            AlgoliaTagData data = new AlgoliaTagData();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(k).getJSONObject("_highlightResult");
                String postingIdsStr = jsonObject.getJSONObject("postingIds").getString("value");
                String numStr = jsonObject.getJSONObject("num").getString("value");
                String textStr = jsonArray.getJSONObject(k).getString("text");
                numStr = numStr.replace("<em>", "");
                numStr = numStr.replace("</em>", "");
                postingIdsStr = postingIdsStr.replace("<em>", "");
                postingIdsStr = postingIdsStr.replace("</em>", "");
                textStr = textStr.replace("<em>", "");
                textStr = textStr.replace("</em>", "");

                Log.d(TAG, "getTagList numSTr : " +numStr);
                Log.d(TAG, "getTagList postingIdsStr : " +postingIdsStr);
                Log.d(TAG, "getTagList textStr : " +textStr);

                data.setPostingIds(postingIdsStr);
                data.setPostingNum(Integer.parseInt(numStr));
                data.setText(textStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(data);
        }
        return list;
    }

    public static String getFirstAlgoliaId(JSONArray jsonArray){
        String algId = null;
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            algId = jsonObject.getString("objectID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return algId;
    }

    public static String getFirstAlgoliaNum(JSONArray jsonArray){
        String algId = null;
        String numStr = "0";
        try {
            numStr = jsonArray.getJSONObject(0).getJSONObject("_highlightResult").getJSONObject("num").getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getNum : " + numStr);
        return numStr;
    }

    public static String getFirstAlgoliaText(JSONArray jsonArray){
        String algId = null;
        String text = null;
        try {
            text = jsonArray.getJSONObject(0).getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getNum : " + text);
        return text;
    }

    public static String getFirstAlgoliaPostingIds(JSONArray jsonArray){
        String algId = null;
        String postingIds = null;
        try {
            postingIds = jsonArray.getJSONObject(0).getJSONObject("_highlightResult").getJSONObject("postingIds").getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "postingIds : " + postingIds);
        return postingIds;
    }

}
