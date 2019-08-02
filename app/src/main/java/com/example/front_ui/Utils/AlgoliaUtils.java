package com.example.front_ui.Utils;

import android.util.Log;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.front_ui.DataModel.AlgoliaTagData;
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

    public static void addObject(String indexName, Object data) {
        Log.d(TAG, indexName + " upload");
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
    public static void searchData(String indexName, String attr, String searchText, final AlgoliaSearchPredicate asp) {
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

    public static void saveData(String indexName, Object data, String objectId) {
        Log.d(TAG, indexName + " save");
        Index index = client.getIndex(indexName);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(data);
        Log.d(TAG, jsonStr);
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            index.saveObjectAsync(json, objectId, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //algolia에서 가게 데이터를 지운다.
    public static void deleteInAlgolia(String tagName, String attr, String keyword){

        final Index index = AlgoliaUtils.client.getIndex("store");

        //1. 먼저 가게 데이터를 찾는다.
        AlgoliaUtils.searchData(tagName, attr, keyword, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                for(int k = 0; k < jsonArray.length(); k++){
                    String jsonStr = null;
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(k);
                        String idStr = jsonObject.getString("objectID");
                        Log.d(TAG, "algolia 삭제! objectId" + idStr );
                        index.deleteObjectAsync(idStr, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Gson gson = new Gson();
                    StoreInfo storeInfo = gson.fromJson(jsonStr, StoreInfo.class);

                }
            }
        });
    }


    /*
     * 1. 추가하려는 태그가 이미 있는지 확인한다.
     * 2. 이미 있으면 가져와서 postingId와 postingNum만 추가한다.
     * 3. 없으면 단순 추가.
     * 알골리아에 추가 및 삭제할 때는 ,id 의 형식으로 붙이고 뺀다.
     * */
    public static void addTagData(final String tag, final String postingId){
        Log.d(TAG, "addTagData : " + tag);
        final Index index = client.getIndex("tag");

        AlgoliaUtils.searchData("tag", "text", tag, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                String preTag = JsonParsing.getFirstAlgoliaText(jsonArray);
                if(preTag != null && tag.equals(preTag)){
                    //태그 데이터 존재.
                    Log.d(TAG, "tag가 이미 존재합니다. : " + tag);
                    String numStr = JsonParsing.getFirstAlgoliaNum(jsonArray);
                    String idStr = JsonParsing.getFirstAlgoliaId(jsonArray);
                    String postingIdsStr = JsonParsing.getFirstAlgoliaPostingIds(jsonArray);

                    Log.d(TAG, "tag num : " + numStr +  " idStr : " + idStr + " postingIds : " + postingIdsStr);
                    try {
                        //<em>이 붙어서 와서 없애준다.
                        numStr = numStr.replace("<em>", "");
                        numStr = numStr.replace("</em>", "");
                        idStr = idStr.replace("<em>", "");
                        idStr = idStr.replace("</em>", "");
                        postingIdsStr = postingIdsStr.replace("<em>", "");
                        postingIdsStr = postingIdsStr.replace("</em>", "");
                        postingIdsStr = postingIdsStr + "," + postingId;

                        int num = Integer.parseInt(numStr) + 1;//1개 증가
                        JSONObject object = new JSONObject().put("text", tag).put("num", num).put("postingIds", postingIdsStr);
                        index.saveObjectAsync(object, idStr, null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    //태그 데이터 없음
                    try {
                        JSONObject object = new JSONObject().put("text", tag).put("num", 1).put("postingIds", ","+postingId);
                        Index index = client.getIndex("tag");
                        index.addObjectAsync(object, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void deleteTagInAlgolia(final String tag, final String postingId){
        Log.d(TAG, "delete tag in algolia : " + tag);

        //1.찾는다.
        //2.1인지 확인한다.
        //3.1이면 그냥 삭제
        //4.1보다 더 크면 id하나 뺀다.
        final Index index = AlgoliaUtils.client.getIndex("tag");

        AlgoliaUtils.searchData("tag", "text", tag, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                String preTag = JsonParsing.getFirstAlgoliaText(jsonArray);
                if(preTag != null && tag.equals(preTag)){
                    //태그 데이터 존재.
                    Log.d(TAG, "tag가 이미 존재합니다. : " + tag);
                    String numStr = JsonParsing.getFirstAlgoliaNum(jsonArray);
                    String idStr = JsonParsing.getFirstAlgoliaId(jsonArray);
                    String postingIdsStr = JsonParsing.getFirstAlgoliaPostingIds(jsonArray);

                    Log.d(TAG, "tag num : " + numStr +  " idStr : " + idStr);
                    //<em>이 붙어서 와서 없애준다.
                    numStr = numStr.replace("<em>", "");
                    numStr = numStr.replace("</em>", "");
                    idStr = idStr.replace("<em>", "");
                    idStr = idStr.replace("</em>", "");
                    postingIdsStr = postingIdsStr.replace("<em>", "");
                    postingIdsStr = postingIdsStr.replace("</em>", "");
                    postingIdsStr = postingIdsStr.replace(","+postingId, "");//postingId제거

                    int num = Integer.parseInt(numStr);//1개 증가
                    if(num == 1){
                        //그냥 삭제
                        Log.d(TAG, "algolia 삭제! tag : " + tag);
                        index.deleteObjectAsync(idStr, null);
                    }else{
                        try {
                            int newNum = Integer.parseInt(numStr) - 1;//1개 감소
                            JSONObject object = new JSONObject().put("text", tag).put("num", newNum).put("postingIds", postingIdsStr);
                            index.saveObjectAsync(object, idStr, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Log.e(TAG, "삭제하려는 태그가 알골리아에 없음 : " + tag);
                }
            }
        });
    }

    //email에 해당하는 objectId를 가져오고 userInfo를 다시 보내준다.
    public static void changeProfileImagePath(final UserInfo userInfo, final String path){
        Log.d(TAG, "changeProfileImagePath : " + userInfo.eMail);
        final Index index = client.getIndex("user");

        AlgoliaUtils.searchData("user", "eMail", userInfo.eMail, new AlgoliaSearchPredicate() {
            @Override
            public void gettingJSONArrayCompleted(JSONArray jsonArray) {
                String preMail = JsonParsing.getFirstAlgoliaEmail(jsonArray);
                if(preMail != null && userInfo.eMail.equals(preMail)){
                    //태그 데이터 존재.
                    Log.d(TAG, "email 찾았음 : " + userInfo.eMail);
                    Log.d(TAG, "image uri :  " + path);

                    String objectId = JsonParsing.getFirstAlgoliaId(jsonArray);
                    Log.d(TAG, " objectId :  " + objectId);

                    userInfo.profileImage = path;
                    //storeInfo를 바꾸어준다.
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(userInfo);
                    Log.d(TAG, jsonStr);
                    JSONObject json;
                    try {
                        json = new JSONObject(jsonStr);
                        index.saveObjectAsync(json, objectId, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e(TAG, "no user data in algolia : " + userInfo.eMail);
                }
            }
        });
    }


}
