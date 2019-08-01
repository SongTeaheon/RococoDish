package com.example.front_ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUpAdapter extends PagerAdapter {

    private int[] images = {R.drawable.rococo_logo2 ,R.drawable.ic_store, R.drawable.ic_food, R.drawable.ic_breakfast};
    private String[] texts = {
            "로코코디쉬",
            "다른 사람들이 모아둔 식사를 모아보세요. \n 당신의 식사들을 소중히 담아두세요.",
            "맛있는 식사 \n 퀄리티 좋은 가게 \n 로코코디쉬에서 찾아보세요.",
            "이제 시작해보세요 \n 로코코디쉬"
    };
    LayoutInflater inflater;
    private Context context;

    public SignUpAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ConstraintLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_sign_up_page, container, false);

        ImageView image = view.findViewById(R.id.image_activitySignUpPage);
        TextView text = view.findViewById(R.id.text_activitySignUpPage);
        Button startBtn = view.findViewById(R.id.startBtn_btn_activitySignUpPage);

        if(position != 3){
            startBtn.setVisibility(View.GONE);
        }
        else{
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LoginDialog.class);
                    context.startActivity(intent);
                }
            });
        }

        image.setImageResource(images[position]);
        text.setText(texts[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
