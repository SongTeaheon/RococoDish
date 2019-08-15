package com.rococodish.front_ui.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class KakaoUtils {
    private static final String TAG = "TAGKakaoUtils";
    public static void OpenKakaoMap(final Context context, String kakaoId, double lat, double lon){
        Log.d(TAG, "show the kakao map");
        String url;
        if(kakaoId != null){
            url = "daummaps://place?id=" + kakaoId;
        }else{
            url = "daummaps://look?p="+lat +","+lon;
        }
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, "카카오맵 어플이 없어서 지도를 열 수가 없어요. 추후 업데이트 예정입니다", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(context)
                    .setMessage("지도를 열려면 카카오맵이 필요합니다. \n 카카오맵을 설치하시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();



        }
    }
}
