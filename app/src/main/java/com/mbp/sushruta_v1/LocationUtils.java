package com.mbp.sushruta_v1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocationUtils {
    public Context context;
    public LocationUtils(Context context){
        this.context = context;
    }

    public boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public void findDistance(Double latitude, Double longitude){
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(11.034435);
        endPoint.setLongitude(78.086872);

        double distance = startPoint.distanceTo(endPoint);
        Log.i("Distance", String.valueOf(distance));
        if (distance > 100) {
            Log.i("Alert the Patient", String.valueOf(distance));
        }

    }



}
