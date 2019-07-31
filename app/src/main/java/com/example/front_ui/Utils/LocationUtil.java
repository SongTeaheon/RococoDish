package com.example.front_ui.Utils;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class LocationUtil {
    public static double getDistanceFromMe(Location mCurrent, GeoPoint geoPoint){
        double distance;
        Location location = new Location("dummyprovider");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());

        distance = mCurrent.distanceTo(location);

        return distance;
    }

    public static double getDistanceFromMe(Location mCurrent, double lat, double lon){
        double distance;
        Location location = new Location("dummyprovider");
        location.setLatitude(lat);
        location.setLongitude(lon);

        distance = mCurrent.distanceTo(location);

        return distance;
    }

    public static double getDistanceFromMe(double currentLat, double currentLon, GeoPoint geoPoint){
        double distance;
        Location location = new Location("dummyprovider");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());

        Location mCurrent = new Location("dummyprovider");
        mCurrent.setLatitude(currentLat);
        mCurrent.setLongitude(currentLon);

        distance = mCurrent.distanceTo(location);

        return distance;
    }
}
