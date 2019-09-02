package com.rococodish.front_ui.Utils;

import android.widget.RatingBar;
import android.widget.TextView;

public class RatingBarUtils {

    //ratingBar Listener
    public static void setupStarRatingBar(final RatingBar mRatingBar,final TextView mStarText){
        //맛
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setStarText(mRatingBar, mStarText);
            }
        });
    }


    private static void setStarText(RatingBar b, TextView v){ //변경
        v.setText(String.valueOf(v));
        float i = b.getRating();
        if (i <= 0.5) {
            v.setText("0.5");
        } else if (i <= 1) {
            v.setText("1.0");
        } else if (i <= 1.5) {
            v.setText("1.5");
        } else if (i <= 2) {
            v.setText("2.0");
        } else if (i <= 2.5) {
            v.setText("2.5");
        } else if (i <= 3) {
            v.setText("3.0");
        } else if (i <= 3.5) {
            v.setText("3.5");
        } else if (i <= 4) {
            v.setText("4.0");
        } else if (i <= 4.5) {
            v.setText("4.5");
        } else if (i <= 5) {
            v.setText("5.0");
        } else {
            v.setText("");
        }
    }
}
