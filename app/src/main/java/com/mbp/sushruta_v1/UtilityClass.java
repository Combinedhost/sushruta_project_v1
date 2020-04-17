package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class UtilityClass {
    public Context context;

    SharedPreferences sharedPreferences;
    public UtilityClass(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
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

    public double findDistance(Double latitude, Double longitude){
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(Double.parseDouble(sharedPreferences.getString("quarantine_latitude", null)));
        endPoint.setLongitude(Double.parseDouble(sharedPreferences.getString("quarantine_longitude", null)));
        endPoint.setLongitude(78.086872);

        double distance = startPoint.distanceTo(endPoint);
        Log.i("Distance", String.valueOf(distance));

        return distance;

    }

    public boolean isQuarantineLocationSet(){
        return (sharedPreferences.getString("quarantine_latitude", null) !=null) && (sharedPreferences.getString("quarantine_longitude", null) != null);
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showMessage(View view, String data) {
        final Snackbar snackbar = Snackbar.make(view, data, Snackbar.LENGTH_SHORT);
        snackbar.setTextColor(ContextCompat.getColor(context,R.color.popup));
        snackbar.show();
    }
}

