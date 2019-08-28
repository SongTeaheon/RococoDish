package com.rococodish.front_ui.FCM;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    String serverKey = "AAAAsqwD-Ag:APA91bFp8RVejD5mPooZACmSbly2fQTad7w_LWA5w5thdhx7COMOxxWvyCQCDfNzNGCOJdE0Q99dOn4WI96-5SetQQxNLIQxnFNB_MKEJrNYPKT2BUtXvsCyZC6MI8SXinn-BgwqnTK5";

    @Headers({"Authorization: key=" + serverKey, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);

}
