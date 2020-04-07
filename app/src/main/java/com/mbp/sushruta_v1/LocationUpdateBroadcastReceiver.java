package com.mbp.sushruta_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

                    if(location.getAccuracy() < 50 ) {
                        SharedPreferences sharedPref = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
                        String patientId = sharedPref.getString("patient_id", null);
                        Log.i("LocationUpdateReceiver", patientId);
                        if(patientId != null){
                            FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
                            String dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            final DatabaseReference dataRef = dataBase.getReference().child("sushruta/Details/Location").child(patientId).child(dateFormat);
                            String timeFormat = new SimpleDateFormat("h:mm:ss a", Locale.getDefault()).format(new Date());
                            String key = dataRef.push().getKey();
                            Map<String, String> map1 = new HashMap<String, String>();
                            map1.put("time", timeFormat);
                            map1.put("location", location.getLatitude() + " " + location.getLongitude());
                            dataRef.child(key).setValue(map1);
                            String doctorName = sharedPref.getString("doctor_name", null);
                            NotificationUtils.sendFCMPush(context, doctorName,"Quarentine Circle exceeded ","Sir, Patient Id "+ patientId +" has gone out of his quarentine location. Click the notification to take action","Patient", "Geofence");
//                            if( locationUtils.findDistance(location.getLatitude(), location.getLongitude()) > 100 && doctorName != null) {
//                                NotificationUtils.sendFCMPush(context, doctorName,"Quarentine Circle exceeded ","Sir, Patient Id "+ patientId +" has gone out of his quarentine location. Click the notification to take action","Patient", "Geofence");
//                            }
                        }
                    }
                }
                else{
                    Log.i("Location Receiver", "Null result");
                }
            }
        }
    }
}
