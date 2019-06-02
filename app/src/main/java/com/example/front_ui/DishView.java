package com.example.front_ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.KotlinCode.PostToMyPage;
import com.example.front_ui.Utils.DishViewUtils;
import com.example.front_ui.Utils.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DishView extends AppCompatActivity {

    private final String TAG = "TAGDishView";
    Button buttonToDetail;
    Button deleteButton;
    ImageView imageView;
    FirebaseFirestore db;
    FirebaseStorage storage;
    PostingInfo postingInfo;
    CircleImageView profileImage;
    TextView profileName;
    @Nullable
    String userImage;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_view);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        buttonToDetail = (Button) findViewById(R.id.toDetail1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        /**
         LastShareFragment 에서 singleitem 을 받음
         * **/
        //LastFragmentShare에서 받은 아이템 정보를 갖고옴.
        Intent intent = this.getIntent();
        final Bundle bundle = intent.getExtras();

        postingInfo = (PostingInfo)bundle.getSerializable("postingInfo");
        Log.d(TAG, "posting Info description " + postingInfo.description +"storage path " + postingInfo.imagePathInStorage
        + " storeId : " + postingInfo.getStoreId() +" postingid : " + postingInfo.postingId);

        /**
         해쉬태그
         **/
        TextView hashTag = (TextView) findViewById(R.id.hashTag_textView_dishView);
        //해쉬태그가 있을 경우에만 실행
        if(postingInfo.hashTags != null){
            setTags(hashTag, postingInfo.hashTags);
        }

        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(this).load(fileReference).into(imageView);

        buttonToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToDetail();
            }
        });
        //우측 상단의 삭제 버튼을 누를 경우 디비를 삭제시킴(포스팅과 포스팅채널 둘다)
        if(postingInfo.getWriterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Log.d(TAG, "내 게시물!!!");
            deleteButton = findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "delete button clicked");
                    String storeId = postingInfo.getStoreId();
                    String postingId = postingInfo.getPostingId();
                    String imagePath = postingInfo.getImagePathInStorage();
                    double postingAverStar = postingInfo.getAver_star();
                    DishViewUtils.deletePosting(db, storage, storeId, postingId, imagePath, postingAverStar);

                }
            });
        }
        /**
         * 포스팅 작성자의 프로필 정보(이미지, 이름) 불러오기 + 우측 상단에 띄우기
         * **/
        profileImage = findViewById(R.id.profile_imageview_dishView);
        profileName = findViewById(R.id.profileNam_dishView);

        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(postingInfo.writerId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //프로필 이미지 업로드
                        userImage = (String) document.get("profileImage");
                        if(userImage != null){
                            GlideApp.with(getApplicationContext())
                                    .load(userImage)
                                    .into(profileImage);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "프로필 이미지가 없네요", Toast.LENGTH_SHORT).show();
                            GlideApp.with(getApplicationContext())
                                    .load(R.drawable.basic_user_image)
                                    .into(profileImage);
                        }
                        //프로필 이름 업로드
                        String userName = (String) document.get("nickname");
                        profileName.setText(userName);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        /**
         * 작성자의 프로필을 클릭시 작성자 마이페이지 이동 + postingInfo 다음 액티비티로 넘겨주기
         * **/
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(DishView.this, PostToMyPage.class);
                final Bundle bundle = new Bundle();
                bundle.putSerializable("allPostingInfo", postingInfo);
                intent.putExtra("writerImage", userImage);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void moveToDetail() {
        Intent intent = new Intent(this, DishViewDetail.class);
        startActivity(intent);
    }
    //디비에 있는 "해쉬태그" 정보를 받아서 #부분만 색깔을 칠해주는 메서드(무조건 #이 있는 스트링만 받아야함.)
    //추가로 해쉬태그 누를시 이벤트도 생성 가능
    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d(TAG, "현재 누른 태그 = " + tag);
                            Toast.makeText(DishView.this, "태그 = "+tag, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#80FF909A"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }
}

