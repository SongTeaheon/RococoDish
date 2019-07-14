package com.example.front_ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.front_ui.DataModel.UserInfo;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private String TAG = "TAGSignUpActivity";
    TextView emailLogin;
    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .setRequireName(true)
            .build());
    private int RC_EMAIL_LOGIN = 1001;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //이메일로 시작하기
        emailLogin = findViewById(R.id.emailLogin_textview_signUpActivity);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build());
                startActivityForResult(intent, RC_EMAIL_LOGIN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_EMAIL_LOGIN){
            final ProgressDialog progressDialog = new ProgressDialog(this);

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                progressDialog.setMessage("회원정보를 확인중입니다.");
                progressDialog.show();

                final DocumentReference userRef = firestore.collection("사용자").document(auth.getUid());

                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            final UserInfo newUser = new UserInfo(
                                    auth.getCurrentUser().getEmail(),
                                    auth.getCurrentUser().getDisplayName(),
                                    MyPage.basicProfile,
                                    null,
                                    null,
                                    0);

                            userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "새로운 유저 등록에 성공했습니다.");
                                    startActivity(new Intent(SignUpActivity.this, SubActivity.class));
                                    finish();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else{
                            Log.d(TAG, "이미 가입한 유저입니다.");
                            startActivity(new Intent(SignUpActivity.this, SubActivity.class));
                            finish();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "에러발생", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }
}
