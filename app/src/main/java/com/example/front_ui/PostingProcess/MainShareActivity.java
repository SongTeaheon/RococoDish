package com.example.front_ui.PostingProcess;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.Permissions;
import com.google.android.gms.common.api.Response;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import static com.example.front_ui.PostingProcess.StoreSearchFragment.RC_fromStoreSearchFragment;


public class MainShareActivity extends AppCompatActivity {

    private static final String TAG = "TAGShareActivity";
    private static final int VERIFY_PERMISSIONS_REQUEST = 10001;

    public PostingInfo postingInfo;
    public KakaoStoreInfo kakaoStoreInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //permission check(camera, read and write external storage)
        if(checkPermissionArray(Permissions.POST_PERMISSIONS)){
            setupFragment();
        }else{
            verifyPermissions(Permissions.POST_PERMISSIONS);
        }
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions : verify permissions");
        ActivityCompat.requestPermissions(MainShareActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);

    }
    //check an array of permissions
    public boolean checkPermissionArray(String[] permissions){
        Log.d(TAG, "checkPermissionArray : checking permissions array");

        for(int i  =0 ; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermission(check)){
                return false;
            }
        }
        return true;
    }

    //check single permission
    public boolean checkPermission(String permission){
        Log.d(TAG, "checkPermissionArray : "+ permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(MainShareActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissionArray : \n permission is not granted for"+ permission);
            return false;
        }
        Log.d(TAG, "checkPermissionArray : \n permission is granted for"+ permission);

        return true;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case VERIFY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupFragment();

                } else {
                    Toast.makeText(getApplicationContext(), "no permission. cannot post", Toast.LENGTH_LONG).show();
                    finish();//현재 창을 꺼버린다.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setupFragment(){

        Log.d(TAG, "setupFragment");
        StoreSearchFragment storeSearchFragment = new StoreSearchFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.relLayout1, storeSearchFragment).commit();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult_from_MainShareActivity");
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_fromStoreSearchFragment){
            Log.d(TAG, "요청코드는 받아서 비트맵 변환 시작합니다.");
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    Bundle extras = data.getExtras();
                    if(extras != null){
                        //갤러리에서 받은 파일을 byteArray로 바꿈
                        Bitmap bmp = extras.getParcelable("data");
                        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        byte[] byteArray = outputStream.toByteArray();

                        //fragment로 데이터 전송 및 변경
                        LastShareFragment lastShareFragment = new LastShareFragment();
                        Bundle args = new Bundle();
                        args.putByteArray("byteArray", byteArray);
                        args.putParcelable("storeData", kakaoStoreInfo);
                        Log.d(TAG, "성공적으로 바이트어레이 이동");
                        lastShareFragment.setArguments(args);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.relLayout1, lastShareFragment).commit();
                    }
                }
            }
        }
    }


    public void setKakaoStoreInfo(KakaoStoreInfo kakaoInfo){
        kakaoStoreInfo = kakaoInfo;
    }

}
