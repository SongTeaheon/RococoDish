package com.example.front_ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.IntentCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.front_ui.DataModel.UserInfo;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginDialog extends AppCompatActivity {

    private String TAG = "TAGLoginDialog";

    ConstraintLayout emailLogin;
    ConstraintLayout googleLogin;
    ConstraintLayout facebookLogin;

    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder()
            .setAllowNewAccounts(true)
            .setRequireName(true)
            .build());
    private int RC_EMAIL_LOGIN = 1001;
    private int RC_GOOGLE_LOGIN = 1002;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Nullable
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_login);


        emailLogin = findViewById(R.id.emailLogin_textview_signUpActivity);
        googleLogin = findViewById(R.id.googleLogin_textview_signUpActivity);
        facebookLogin = findViewById(R.id.facebookLogin_textview_signUpActivity);



        //이메일로 시작하기
        emailLogin = findViewById(R.id.emailLogin_textview_signUpActivity);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build());
                startActivityForResult(intent, RC_EMAIL_LOGIN);
            }
        });

        //페이스북으로 시작하기
        facebookLogin = findViewById(R.id.facebookLogin_textview_signUpActivity);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Boolean isLogged = accessToken != null && !accessToken.isExpired(); //토큰이 사용가능한지 파악
                LoginManager.getInstance()
                        .logInWithReadPermissions(
                                LoginDialog.this,
                                Arrays.asList("email", "public_profile"));//페이스북에서 가져올 정보 선택하기 , "user_friends" 앱 검수 필요(완전히 앱을 만드는게 조건)
                setFacebookLogin();//로그인 실행
            }
        });

        //구글로 시작하기
        googleLogin = findViewById(R.id.googleLogin_textview_signUpActivity);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoogleLogin(LoginDialog.this);
            }
        });


    }

    public void setGoogleLogin(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    public void setFacebookLogin(){
        //페이스북에서 응답을 받기 위한 콜백
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "페이스북 로그인 성공");
                handleFacebookAccessToken(loginResult.getAccessToken());//토큰을 이용해서 credential로 로그인 위한 함수
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "페이스북 로그인 실패");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "페이스북 로그인 에러 => "+ error.getMessage());
            }

            private void handleFacebookAccessToken(AccessToken token){
                Log.d(TAG, "handleFacebookAccessToken : "+token);
                if(token != null){
                    final ProgressDialog progressDialog = new ProgressDialog(LoginDialog.this);
                    progressDialog.setMessage("회원정보 확인중입니다");
                    progressDialog.show();

                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "signInWithCredential이 성공적");

                                final FirebaseUser user = auth.getCurrentUser();
                                final DocumentReference userRef = firestore.collection("사용자").document(auth.getUid());

                                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(!documentSnapshot.exists()){
                                            final UserInfo newUser = new UserInfo(
                                                    FirebaseAuth.getInstance().getUid(),
                                                    user.getEmail(),
                                                    user.getDisplayName(),
                                                    null,
                                                    null,
                                                    null,
                                                    0
                                            );
                                            AlgoliaUtils.addObject("user", newUser);

                                            userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "새로운 유저 등록에 성공했습니다.");
                                                    Intent intent = new Intent(LoginDialog.this, Login2Activity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("userInfo", newUser);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "새로운 유저 등록에 실패했습니다.");
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                        else{
                                            Log.d(TAG, "이미 가입한 유저입니다.");
                                            Intent intent = new Intent(LoginDialog.this, SubActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            }
                            else{
                                Log.d(TAG, "signInWithCredential이 실패 => "+ task.getException());
                                Toast.makeText(LoginDialog.this, "페이스북 인증에 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            //생략가능
            public void requestMe(AccessToken token){
                GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "토큰요청 성공의 결과물 = "+ object.toString());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 이메일 로그인 응답
         * **/
        if(requestCode == RC_EMAIL_LOGIN){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("회원정보 확인중입니다.");
            progressDialog.show();

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
                                    FirebaseAuth.getInstance().getUid(),
                                    auth.getCurrentUser().getEmail(),
                                    auth.getCurrentUser().getDisplayName(),
                                    null,
                                    null,
                                    null,
                                    0);

                            AlgoliaUtils.addObject("user", newUser);

                            userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "새로운 유저 등록에 성공했습니다.");
                                    Intent intent = new Intent(LoginDialog.this, Login2Activity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("userInfo", newUser);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "새로운 유저 등록에 실패했습니다.");
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else{
                            Log.d(TAG, "이미 가입한 유저입니다.");
                            Intent intent = new Intent(LoginDialog.this, SubActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }
                });
            }
            else{
//                Toast.makeText(this, "다른 계정으로 가입한 이메일입니다.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
        /**
         * 페이스북 로그인 응답
         * **/
        if(callbackManager != null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        /**
         * 구글 로그인 응답
         * **/
        if(requestCode == RC_GOOGLE_LOGIN){
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);

            Log.d(TAG, "구글로그인에서 onActivityResult는 진입");

            final ProgressDialog progressDialog = new ProgressDialog(LoginDialog.this);
            progressDialog.setMessage("회원정보 확인중입니다.");
            progressDialog.setCancelable(false);
            progressDialog.show();
            try{
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                Log.d(TAG, "구글에서 받은 계정정보 = "+ account.toString());
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "계정정보 받은 후 signInWithCredential:성공");

                                    final DocumentReference userRef = firestore.collection("사용자").document(auth.getUid());

                                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(!documentSnapshot.exists()){
                                                final UserInfo newUser = new UserInfo(
                                                        FirebaseAuth.getInstance().getUid(),
                                                        auth.getCurrentUser().getEmail(),
                                                        auth.getCurrentUser().getDisplayName(),
                                                        null,
                                                        null,
                                                        null,
                                                        0
                                                );
                                                AlgoliaUtils.addObject("user", newUser);

                                                userRef.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "새로운 유저 등록에 성공했습니다.");
                                                        Intent intent = new Intent(LoginDialog.this, Login2Activity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putSerializable("userInfo", newUser);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                        progressDialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "새로운 유저 등록에 실패했습니다.");
                                                         progressDialog.dismiss();
                                                    }
                                                });
                                            }
                                            else{
                                                Log.d(TAG, "이미 가입한 유저입니다.");
                                                Intent intent = new Intent(LoginDialog.this, SubActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Log.d(TAG, throwable.getMessage());
                progressDialog.dismiss();
            }
        }
    }
}
