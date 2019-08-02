package com.rococodish.front_ui.Utils;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;

public class GlidePlaceHolder {


    public static CircularProgressDrawable circularPlaceHolder(Context context){

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.start();


        return circularProgressDrawable;
    }
}
