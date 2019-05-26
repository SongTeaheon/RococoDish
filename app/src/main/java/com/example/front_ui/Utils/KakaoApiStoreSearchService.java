package com.example.front_ui.Utils;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApiStoreSearchService {

    public static final String API_URL = "https://dapi.kakao.com/v2/local/search/";

    @GET("keyword.json")
    Call<JsonObject> getKakaoStoreInfo(@Header("Authorization") String id,
                                       @Query("query") String searchWord);

    @GET("keyword.json")
    Call<JsonObject> getKakaoStoreInfo(@Header("Authorization") String id,
                                       @Query("query") String searchWord,
                                       @Query("category_group_code") String code);

    @GET("address.json")
    Call<JsonObject> getKakaoLocalInfo(@Header("Authorization") String id,
                                       @Query("query") String searchWord);

}
