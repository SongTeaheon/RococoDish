package com.example.front_ui.Edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;

public class BroadcastUtils {

    private static final String TAG = "TAGBroadcastUtils";

    private static BroadcastReceiver broadcastReceiver_posting;
    private static BroadcastReceiver broadcastReceiver_store;

    public static BroadcastReceiver getBrdCastReceiver_posting(final PostingInfo postingInfo){
        broadcastReceiver_posting = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // intent ..
                Log.d(TAG, "brdCastRecevie : " + intent.getStringExtra("hashTags"));
                postingInfo.setHashTags(intent.getStringExtra("hashTags"));
                postingInfo.setAver_star(intent.getFloatExtra("aver_star", 0.0f));
            }
        };
        return broadcastReceiver_posting;
    }
    public static BroadcastReceiver getBrdCastReceiver_store(final StoreInfo storeInfo){
        broadcastReceiver_store = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // intent ..
                Log.d(TAG, "brdCastRecevie : " + intent.getDoubleExtra("aver_star", 0.0));
                storeInfo.setAver_star(intent.getDoubleExtra("aver_star", 0.0));
            }
        };
        return broadcastReceiver_store;
    }

    public static void UnregBrdcastReceiver_posting(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver_posting);
    }
    public static void UnregBrdcastReceiver_store(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver_store);
    }
}
