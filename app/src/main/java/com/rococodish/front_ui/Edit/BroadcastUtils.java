package com.rococodish.front_ui.Edit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.rococodish.front_ui.DataModel.PostingInfo;

import java.util.HashMap;

public class BroadcastUtils {

    private static final String TAG = "TAGBroadcastUtils";

    private static BroadcastReceiver broadcastReceiver_posting;

    public static BroadcastReceiver getBrdCastReceiver_posting(final PostingInfo postingInfo){
        broadcastReceiver_posting = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // intent ..
                Log.d(TAG, "brdCastRecevie : " + intent.getStringExtra("hashTags"));
                postingInfo.setHashTags(intent.getStringExtra("hashTags"));
                postingInfo.setAver_star(intent.getFloatExtra("aver_star", 0.0f));
                postingInfo.setTag((HashMap)intent.getSerializableExtra("tag"));
            }
        };
        return broadcastReceiver_posting;
    }

    public static void UnregBrdcastReceiver_posting(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver_posting);
    }
}
