package com.mbp.sushruta_v1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationWorker extends Worker {
    Context context;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    LocationUtils locationUtils;

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        locationUtils = new LocationUtils(context);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        Context context = getApplicationContext();

        Log.d("RefreshDataWorker", "refreshing data....");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (locationUtils.checkPermissions()) {
            if (locationUtils.isLocationEnabled()) {
                requestNewLocationData();
            }
            else{
//                locationUtils.showNotification("Location is not turned on. Kindly on the location now");

            }
        }else{
//            locationUtils.showNotification("Location permissions are not granted. Kindly grant permissions now");
        }
        return Worker.Result.success();
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(60000 * 10);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(10);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        Intent intent = new Intent(context, LocationUpdateBroadcastReceiver.class);
        intent.setAction(LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mFusedLocationClient.requestLocationUpdates(
                locationRequest, pendingIntent
        );

        Log.i("Location Worker", "New Location requested");

    }

}

