package com.example.front_ui.Utils;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;

import com.example.front_ui.R;

public class GlidePlaceHolder {


    public static CircularProgressDrawable circularPlaceHolder(Context context){

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.setBackgroundColor(R.color.MainColor);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.start();


        return circularProgressDrawable;
    }
}
