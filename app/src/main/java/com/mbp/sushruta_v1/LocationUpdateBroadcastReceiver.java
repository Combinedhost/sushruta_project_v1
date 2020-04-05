package com.mbp.sushruta_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {


    static final String ACTION_PROCESS_UPDATES =
            "com.mbp.sushruta_v1.location_update_pending_intent.action";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Recieved");
        if (intent != null) {
            final String action = intent.getAction();
            Log.i("Receiver", action);
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    LocationUtils locationUtils = new LocationUtils(context);
                    Log.i("Longitude", String.valueOf(location.getLongitude()));
                    Log.i("Latitude", String.valueOf(location.getLatitude()));
                    Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
                    locationUtils.findDistance(location.getLatitude(), location.getLongitude());
                    Log.i("Location Provider", String.valueOf(location.getProvider()));
                }
                else{
                    Log.i("Location Receiver", "Null result");
                }
            }
        }
    }
}
