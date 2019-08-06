package com.example.front_ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.example.front_ui.Utils.GlideApp;
import com.example.front_ui.Utils.GlidePlaceHolder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Login2Activity extends AppCompatActivity {

    CardView profileImage_CardView;
    ImageView addIcon;
    ImageView profileImageView;
    EditText nameEditText;
    Button doneBtn;
    Button backToStart;

    private int RC_GALLERY = 1234;

    DishViewProfileImgPass dishViewProfileImgPass;

    public void OnGetStringData(DishViewProfileImgPass _dishViewProfileImgPass){
        dishViewProfileImgPass = _dishViewProfileImgPass;
    }

    String imagePath;

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");

        if(userInfo == null){
            Toast.makeText(this, "회원가입에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login2Activity.this, SignUpActivity.class));
            finish();
        }

        //todo : 로그인 후 여기로 넘어와서 프로필 사진이랑 이름 변경가능하게 함.

        profileImage_CardView = findViewById(R.id.cardview_login2Activity);
        addIcon = findViewById(R.id.addIcon_iv_login2Activity);
        profileImageView = findViewById(R.id.profileImage_iv_login2Activity);
        nameEditText = findViewById(R.id.userName_etv_login2Activity);
        doneBtn = findViewById(R.id.allDone_btn_login2Activity);
        backToStart = findViewById(R.id.goToStart_login2Activity);

        //이미지 부분 누를경우
        profileImage_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RC_GALLERY);
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("잠시만 기다려주세요.");


        OnGetStringData(new DishViewProfileImgPass() {
            @Override
            public void passProfileImgPath(String imgPath) {

                if(imgPath != null){

                    imagePath = imgPath;
                }
                else{
                    imagePath = null;
                }
            }
        });

        //완료 버튼 누를 경우
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //프로필은 이미 위에서 끝냈으니 괜춘.
                //이름 수정
                final String nameString = nameEditText.getText().toString();
                if(TextUtils.isEmpty(nameString.trim())){
                    Toast.makeText(Login2Activity.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.show();
                    FirebaseFirestore.getInstance()
                            .collection("사용자")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("nickname", nameString)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userInfo.nickname = nameString;
                                    AlgoliaUtils.changeProfileImagePath(userInfo, imagePath);
                                    startActivity(new Intent(Login2Activity.this, SubActivity.class));
                                    finish();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });

        //이전 버튼 누를 경우
        backToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : 이전 버튼 누르면 로그인했던것도 없애버림.
                //auth에서도 기록 없앰
                FirebaseAuth.getInstance().getCurrentUser().delete();
                //디비에서 없앰.
                FirebaseFirestore.getInstance()
                        .collection("사용자")
                        .document(FirebaseAuth.getInstance().getUid())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Login2Activity.this, SignUpActivity.class));
                        finish();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        //취소 눌러서 액티비티 종료하는 거 막음.
//        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //프로필 사진 고를 시 앨범에서 사진 가져올 경우
        if(requestCode == RC_GALLERY && resultCode == Activity.RESULT_OK && data != null){
            Uri selectedImageFromGallery = data.getData();

            UCrop.Options options = new UCrop.Options();
            options.setToolbarWidgetColor(getResources().getColor(R.color.colorWhite));//툴바 글자색
            options.setToolbarColor(getResources().getColor(R.color.MainColor));//툴바 색
            options.setCropFrameColor(getResources().getColor(R.color.MainColor));//테두리
            options.setHideBottomControls(true);//아래 보라색 없애버림


            Uri destinationUri = Uri.fromFile(new File(getApplicationContext().getCacheDir(), "IMG_" + System.currentTimeMillis()));
            UCrop.of(selectedImageFromGallery, destinationUri)
                    .withAspectRatio(1, 1)
                    .withOptions(options)
                    .withMaxResultSize(450, 450)
                    .start(this);
        }

        if(requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            final ProgressDialog progressDialog  = ProgressDialog.show(this,  null,"사진을 등록중입니다.");
            if (resultUri != null) {
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                    byte[] byteArray = outputStream.toByteArray();
                    Storage.INSTANCE.uploadProfileImage(byteArray, progressDialog, new Function1<Uri, Unit>() {
                        @Override
                        public Unit invoke(Uri uri) {
//                            AlgoliaUtils.changeProfileImagePath(userInfo, uri.toString());
                            dishViewProfileImgPass.passProfileImgPath(uri.toString());
                            return null;
                        }
                    });
                    GlideApp.with(this)
                            .load(bmp)
                            .placeholder(GlidePlaceHolder.circularPlaceHolder(this))
                            .into(profileImageView);
                    addIcon.setVisibility(View.INVISIBLE);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
