package com.mbp.sushruta_v1;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationWorkReceiver extends BroadcastReceiver {

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    UtilityClass locationUtils;
    @Override
    public void onReceive(Context context, Intent intent) {
        locationUtils = new UtilityClass(context);

        Log.d("Location Alarm", "refreshing data....");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Intent permissionIntent = new Intent(context, PatientLoginActivity.class);
        if (locationUtils.checkPermissions()) {
            if (locationUtils.isLocationEnabled()) {
                if(locationUtils.isQuarantineLocationSet())
                    requestNewLocationData(context);
            } else {
                NotificationUtils.sendNotification(context, context.getString(R.string.location_request), context.getString(R.string.turn_on_location), permissionIntent);
            }
        } else {
            NotificationUtils.sendNotification(context, context.getString(R.string.location_request), context.getString(R.string.location_permission), permissionIntent);
        }

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(Context context) {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(60000 * 10);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(10);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        Intent intent = new Intent(context, LocationUpdateBroadcastReceiver.class);
        intent.setAction(LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mFusedLocationClient.requestLocationUpdates(
                locationRequest, pendingIntent
        );

        Log.i("Location Worker", "New Location requested");

    }

}
