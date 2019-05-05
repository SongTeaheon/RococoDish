package com.example.front_ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SubActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 10002;
    private static final String TAG = "TAGSubActivity";
    private FusedLocationProviderClient mFusedLocationClient;
    RecyclerView my_recycler_view;
    private Location mCurrentLocation;
    private boolean mLocationPermissionGranted = false;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate is called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        my_recycler_view = (RecyclerView) findViewById(R.id.mrecyclerView);
        my_recycler_view.setHasFixedSize(true);
        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        my_recycler_view.setAdapter(adapter);


    }

    private void getLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }else{
            getCurrentLocation();
            mLocationPermissionGranted = true;
        }
    }


    private void getCurrentLocation() {
        OnCompleteListener<Location> mCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    mCurrentLocation = task.getResult();
                    Toast.makeText(getApplicationContext(), "lat : " + mCurrentLocation.getLatitude() +
                            "\n lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                }else{
                    Log.e(TAG, "getCurrentLocation Exception"+ task.getException());
                }
            }
        };

        try {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(mCompleteListener);
        }catch(SecurityException e){
            e.printStackTrace();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();

                } else {

                    Toast.makeText(getApplicationContext(), "permission is denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }



}
