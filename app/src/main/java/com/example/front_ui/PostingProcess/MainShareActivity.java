package com.example.front_ui.PostingProcess;

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

import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.Permissions;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;


public class MainShareActivity extends AppCompatActivity {

    private static final String TAG = "TAGShareActivity";
    private static final int VERIFY_PERMISSIONS_REQUEST = 10001;

    public PostingInfo postingInfo;

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
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null){
//            ProgressDialog loading = ProgressDialog.show(this, "로딩중", "잠시만 기다려주세요...");
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            try {
                Bitmap selectedBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                OutputStream outputStream = new ByteArrayOutputStream();
                selectedBmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                byte[] byteArray = ((ByteArrayOutputStream) outputStream).toByteArray();
//                Intent passByte = new Intent(getContext(), LastShareFragment.class);
//                passByte.putExtra("byteArray", byteArray);
//                startActivity(passByte);
                //fragment로 데이터 전송 및 변경
                LastShareFragment lastShareFragment = new LastShareFragment();
                Bundle args = new Bundle();
                args.putByteArray("byteArray", byteArray);
                Log.d(TAG, "성공적으로 바이트어레이 이동");
                lastShareFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relLayout1, lastShareFragment).commit();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
