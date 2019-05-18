package com.example.front_ui.Utils;


import android.util.Log;

public class MathUtil {

    public static double roundOnePlace(double a){
        double res = Math.round(a*100.0)/100.0;//둘째 자리에서 반올림
        res = Math.floor(res*10.0)/10.0;
        Log.d("MathUtil"," : " + a +" to " + res);
        return res;
    }
}
