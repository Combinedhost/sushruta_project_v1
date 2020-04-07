package com.mbp.sushruta_v1;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

        Log.d("Location Worker", "refreshing data....");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Intent intent = new Intent(context, PatientLoginActivity.class);
        if (locationUtils.checkPermissions()) {
            if (locationUtils.isLocationEnabled()) {
                if(locationUtils.isQuarantineLocationSet())
                requestNewLocationData();
            } else {
                NotificationUtils.sendNotification(context, "Location Request", "Location  is not turned on, Kindly turn on te location", intent);
            }
        } else {
            NotificationUtils.sendNotification(context, "Location Request", "Location permissions are not granted. Kindly grant permissions now", intent);
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

