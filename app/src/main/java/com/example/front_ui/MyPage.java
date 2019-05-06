package com.example.front_ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MyPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageView);
        Intent intent = getIntent();

        int img[] = {
                R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang,
                R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang, R.mipmap.dalbang
        };

        MyAdapter adapter = new MyAdapter(
                getApplicationContext(),
                R.layout.polar_style,       // GridView 항목의 레이아웃 row.xml
                img);    // 데이터

        GridView gv = (GridView)findViewById(R.id.gridview);
        gv.setAdapter(adapter);  // 커스텀 아답타를 GridView 에 적용

        imageButton.setOnClickListener(new View.OnClickListener() {
            final String[] profiles = new String[] {"사진촬영", "앨범에서 사진 선택", "기본 이미지로 변경"};
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView :
                        new AlertDialog.Builder(MyPage.this)
                            .setTitle("프로필")
                            .setItems(profiles, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (profiles[which]) {
                                        case "사진촬영":
                                            Toast.makeText(MyPage.this, "사진촬영", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "앨범에서 사진 선택":
                                            Toast.makeText(MyPage.this, "앨범에서 사진 선택", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "기본 이미지로 변경":
                                            Toast.makeText(MyPage.this, "기본 이미지로 변경", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            })
                            .show();
                        break;
                }
            }
        });

        imageButton.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            imageButton.setClipToOutline(true);
        }


    }
}

class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    int img[];
    LayoutInflater inf;

    public MyAdapter(Context context, int layout, int[] img) {
        this.context = context;
        this.layout = layout;
        this.img = img;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public Object getItem(int position) {
        return img[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imagefood);
        iv.setImageResource(img[position]);

        return convertView;
    }
}