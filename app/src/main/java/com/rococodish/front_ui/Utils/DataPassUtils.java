package com.rococodish.front_ui.Utils;

import android.content.Intent;
import android.util.Log;

import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.SerializableStoreInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;

public class DataPassUtils {
    private static final String TAG = "TAGDataPassUtils";

    public static void makeIntentForData(Intent intent, PostingInfo postingInfo, StoreInfo storeInfo){
        SerializableStoreInfo serializableStoreInfo = new SerializableStoreInfo(storeInfo);
        Log.d(TAG, "data check : " + postingInfo.getHashTags());
        intent.putExtra("postingInfo", postingInfo);
        intent.putExtra("storeInfo", serializableStoreInfo);
    }
}
