package com.example.front_ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.front_ui.R.id.login_button_activityLogin
import com.example.front_ui.Util_Kotlin.Firestore
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.util.CollectionUtils.listOf
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

//앱을 실행시킬 시 가장 처음으로 나오는 창입니다.(로그인 및 회원가입 담당)
class LoginActivity : AppCompatActivity() {

    private val TAG = "TAG_LoginActivity"
    private val signInProviders =
            listOf(
                    AuthUI.IdpConfig.EmailBuilder()
                    .setAllowNewAccounts(true)
                    .setRequireName(true)
                    .build()
//                    AuthUI.IdpConfig.PhoneBuilder()
//                            .build()
//                    AuthUI.IdpConfig.GoogleBuilder()
//                            .setSignInOptions(googleSignInOption)
//                            .build()

            )
    val RC_AUTHUI_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_activityLogin.setOnClickListener {
            val intent =AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setTheme(R.style.ThemeOverlay_AppCompat_Dark)
                    .build()
            startActivityForResult(intent, RC_AUTHUI_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_AUTHUI_REQUEST_CODE){

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK){

                //여기서 로딩창을 켜줍니다.
                val progressDialog = indeterminateProgressDialog("잠시만 기다려주세요...", "회원 정보 확인중")

                Firestore.loginFirstTime {//여기서 onComplete()역할을 알 수 있다.(이 함수를 실행시키고 나서 이어서 할 이벤트를 적어주게 함.)
                    startActivity(intentFor<SubActivity>().newTask().clearTask())//이건 단순히 MainActivitry로 넘어가는 intent입니다.
                    Log.d(TAG, "로그인 완료(DB에 업로드 완료) 후 서브액티비티로 이동했습니다.")
                    progressDialog.dismiss()//로딩창을 꺼줍니다.
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){

                if(response == null) {
                    return//아무것도 반환안함(결국 response가 null이면 아무일도 안일어남, 에러도 안 일어남)
                }

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK -> longSnackbar(relativeLayout(), "와이파이나 데이터를 연결해주세요.")
                    ErrorCodes.UNKNOWN_ERROR -> longSnackbar(relativeLayout(), "알 수 없는 에러")
                }
            }
            else{
                Log.d(TAG, "결과 요청코드 에러")
            }
        }
    }
}
