package com.example.front_ui.Edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.front_ui.DataModel.PostingInfo;

public class BroadcastUtils {

    private static final String TAG = "TAGBroadcastUtils";

    private static BroadcastReceiver broadcastReceiver_posting;
    public static BroadcastReceiver getBrdCastReceiver_posting(final PostingInfo singleItem){
        broadcastReceiver_posting = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // intent ..
                Log.d(TAG, "brdCastRecevie : " + intent.getStringExtra("hashTags"));
                singleItem.setHashTags(intent.getStringExtra("hashTags"));
                singleItem.setAver_star(intent.getFloatExtra("aver_star", 0.0f));
            }
        };
        return broadcastReceiver_posting;
    }

    public static void UnregBrdcastReceiver_posting(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver_posting);
    }
}
