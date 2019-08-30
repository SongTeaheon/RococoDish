package com.rococodish.front_ui.Utils;

import android.util.Log;

import com.rococodish.front_ui.DataModel.AlgoliaTagData;
import com.rococodish.front_ui.DataModel.KakaoLocalInfo;
import com.rococodish.front_ui.DataModel.KakaoMetaData;
import com.rococodish.front_ui.DataModel.KakaoStoreInfo;
import com.rococodish.front_ui.DataModel.SearchedData;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.DataModel.UserInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
                data.setAddress(object.getAddress_name());
                data.setX(object.getX());
                data.setY(object.getY());
            }
            else{
                KakaoStoreInfo object = gson.fromJson(jsonArray.get(i), KakaoStoreInfo.class);
                data.setPlace_name(object.getPlace_name());
                data.setAddress(object.getAddress_name());
                data.setX(object.getX());
                data.setY(object.getY());
                Log.d(TAG, "x : " + object.getX() + " y : " + object.getY());
            }

            //해당 데이터에 keyword가 포함되어 있으면 넣는다.
            if(data.getPlace_name().contains(keyword) || data.getPlace_name().contains("대학교"))
                dataList.add(data);
        }

        return dataList;
    }


    public static ArrayList<StoreInfo> getStoreListFromJsonList(JSONArray jsonArray){
        ArrayList<StoreInfo> list = new ArrayList<>();
        Log.d(TAG, "store algolia json array : " + jsonArray.toString());
        for(int k = 0; k < jsonArray.length(); k++){
            StoreInfo storeInfo = new StoreInfo();
            try {
                JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                JSONObject jsonObject2 = jsonArray.getJSONObject(k).getJSONObject("_highlightResult");


                String nameStr = jsonObject1.getString("name");
                String address = jsonObject2.getJSONObject("address").getString("value");
                String idStr = jsonObject2.getJSONObject("storeId").getString("value");
                nameStr = nameStr.replace("<em>", "");
                nameStr = nameStr.replace("</em>", "");
                address = address.replace("<em>", "");
                address = address.replace("</em>", "");
                idStr = idStr.replace("<em>", "");
                idStr = idStr.replace("</em>", "");

                storeInfo.setName(nameStr);
                storeInfo.setAddress(address);
                storeInfo.setStoreId(idStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            list.add(storeInfo);
        }
        return list;
    }

    public static ArrayList<UserInfo> getUserListFromJsonList(JSONArray jsonArray){
        ArrayList<UserInfo> list = new ArrayList<>();
        Log.d(TAG, "user json array : " + jsonArray.toString());
        for(int k = 0; k < jsonArray.length(); k++){
            String jsonStr = null;
            UserInfo userInfo = new UserInfo();
            try {
                JSONObject jsonObject1 = jsonArray.getJSONObject(k);
                JSONObject jsonObject2 = jsonArray.getJSONObject(k).getJSONObject("_highlightResult");

                String nickname = jsonObject1.getString("nickname");
                String eMail = jsonObject2.getJSONObject("eMail").getString("value");
                String idStr = jsonObject2.getJSONObject("uid").getString("value");
                String profilePath = null;
                try {
                    profilePath = jsonObject2.getJSONObject("profileImage").getString("value");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, nickname +" " + eMail + " " + idStr);

                nickname = nickname.replace("<em>", "");
                nickname = nickname.replace("</em>", "");
                eMail = eMail.replace("<em>", "");
                eMail = eMail.replace("</em>", "");
                idStr = idStr.replace("<em>", "");
                idStr = idStr.replace("</em>", "");

                Log.d(TAG, nickname +" " + eMail + " " + idStr);
                userInfo.nickname = nickname;
                userInfo.eMail = eMail;
                userInfo.uid = idStr;
                if(profilePath != null) {
                    profilePath = profilePath.replace("<em>", "");
                    profilePath = profilePath.replace("</em>", "");
                    userInfo.profileImage = profilePath;
                    Log.d(TAG, "profilePath is not null " + profilePath);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    public static String getFirstAlgoliaEmail(JSONArray jsonArray){
        String eMail = null;
        Log.d(TAG, "getFirstAlgoliaEmail" + jsonArray.toString());
        try {
            eMail = jsonArray.getJSONObject(0).getString("eMail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "eMail : " + eMail);
        return eMail;
    }


}
