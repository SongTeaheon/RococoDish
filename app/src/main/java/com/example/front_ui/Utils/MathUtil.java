package com.example.front_ui.Utils;


import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

public class MathUtil {

    //웹툰형 별점. 5점은 모두가 5점이지 않는한 나올 수 없음!
    public static double roundOnePlace(double a) {
        double res = Math.round(a * 100.0) / 100.0;//둘째 자리에서 반올림
        res = Math.floor(res * 10.0) / 10.0;
        Log.d("MathUtil", " : " + a + " to " + res);
        return res;
    }

    //미터를 파라미터로 받는다. 1000보다 작으면 반올림해서 출력. 1000보다 크면 1000으로 나누고 소수 둘째 자리에서 반올림
    public static String adjustedDistance(double distance){
        String distanceStr;
        double disKm;
        if(((int)(distance/1000)) <= 0){
            long resDis = Math.round(distance);
            distanceStr = String.valueOf(resDis) + "m";
        }else {
            disKm = Math.round(distance/100.)/10.;
            distanceStr = Double.toString(disKm) + "km";
        }
        Log.d("MathUtil", " : " + distance + " to " + distanceStr);

        return distanceStr;
    }

}
