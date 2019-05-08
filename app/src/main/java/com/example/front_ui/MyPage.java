package com.example.front_ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.front_ui.Utils.GlideApp;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyPage extends AppCompatActivity {

    private int RC_GALLERY = 1;
    private int RC_CROP = 2;
    private int RC_CAMERA = 1001;
    public ImageButton imageButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_page);
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

        imageButton = (ImageButton) findViewById(R.id.imageView);
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
//                                            Toast.makeText(MyPage.this, "사진촬영", Toast.LENGTH_SHORT).show();
                                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(cameraIntent, RC_CAMERA);
                                            break;
                                        case "앨범에서 사진 선택":
//                                            Toast.makeText(MyPage.this, "앨범에서 사진 선택", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(intent, RC_GALLERY);
                                            break;
                                        case "기본 이미지로 변경":
//                                            Toast.makeText(MyPage.this, "기본 이미지로 변경", Toast.LENGTH_SHORT).show();
                                            GlideApp.with(getApplicationContext())
                                                    .load(R.drawable.basic_user_image)
                                                    .into(imageButton);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GALLERY && resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImageFromGallery = data.getData();

            Intent intent = CropImage.activity(selectedImageFromGallery)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .getIntent(getApplicationContext());
            startActivityForResult(intent, RC_CROP);
        }
        if(requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK && data != null){
//            Uri selectedCamera = data.getData();
//            InputStream imageStream = null;
//            try{
//                imageStream = getContentResolver().openInputStream(selectedCamera);
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
//            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
            Uri cameraUri = data.getData();
            Log.d("Test", "일단 카메라 사진의 이미지는 받았습니다.");
            Intent intent = CropImage.activity(cameraUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .getIntent(getApplicationContext());
            startActivityForResult(intent, RC_CROP);
        }
        if(requestCode == RC_CROP){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == Activity.RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    GlideApp.with(this)
                            .load(bmp)
                            .into(imageButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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