package com.rococodish.front_ui;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpAdapter extends PagerAdapter {

    private int[] images = {R.drawable.ic_logo2 ,R.mipmap.plususe, R.mipmap.mapuse, R.mipmap.ic_app_icon};
    private String[] texts = {
            "로코코디쉬",
            "디저트처럼 가볍게 \n 당신의 식사를 공유하세요.",
            "다른 사람들이 공유한 맛집이 \n 어디에 있는지 바로 확인하세요.",
            "이제 시작해보세요. \n 로코코디쉬"
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
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_sign_up_page, container, false);

        ImageView image = view.findViewById(R.id.image_activitySignUpPage);
        ImageView pre = view.findViewById(R.id.iv_pre);
        ImageView next = view.findViewById(R.id.iv_next);
        TextView text = view.findViewById(R.id.text_activitySignUpPage);
        Button startBtn = view.findViewById(R.id.startBtn_btn_activitySignUpPage);
//        ImageView moveToRight = view.findViewById(R.id.moveToRight_iv_activitySignUp);

        switch (position) {
            case 0: {
                startBtn.setVisibility(View.GONE);
                pre.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                break;
            }
            case 1:
            case 2: {
                startBtn.setVisibility(View.GONE);
                pre.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            }
            case 3: {
                startBtn.setVisibility(View.VISIBLE);
                pre.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, LoginDialog.class);
                        context.startActivity(intent);
                    }
                });
                break;
            }
            default: {
                startBtn.setVisibility(View.VISIBLE);
                pre.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, LoginDialog.class);
                        context.startActivity(intent);
                    }
                });
            }
        }

//        if(position != 3){
//            //시작하기 버튼 숨기기
//            startBtn.setVisibility(View.GONE);
//        }
//        else{
//            startBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, LoginDialog.class);
//                    context.startActivity(intent);
//                }
//            });
//        }

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
