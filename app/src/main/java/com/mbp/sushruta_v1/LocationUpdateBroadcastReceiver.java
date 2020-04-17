package com.mbp.sushruta_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    UtilityClass utilityClass;

    @Override
    public void onReceive(Context context, Intent intent) {
        utilityClass = new UtilityClass(context);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Intent permissionIntent = new Intent(context, PatientLoginActivity.class);
                if (!utilityClass.isNetworkAvailable()) {
                    NotificationUtils.sendNotification(context, "Location Request", "Kindly turn on internet to post your current location.", permissionIntent);
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    NotificationUtils.sendNotification(context, "Location Request", "Active session not available. Kindly login again.", permissionIntent);
                    return;
                }

                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    UtilityClass locationUtils = new UtilityClass(context);
                    Log.i("Longitude", String.valueOf(location.getLongitude()));
                    Log.i("Latitude", String.valueOf(location.getLatitude()));
                    Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
                    locationUtils.findDistance(location.getLatitude(), location.getLongitude());
                    Log.i("Location Provider", String.valueOf(location.getProvider()));

                    if (location.getAccuracy() < 50) {
                        SharedPreferences sharedPref = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
                        String patientId = sharedPref.getString("patient_id", null);
                        if (patientId != null) {
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
                            if (locationUtils.findDistance(location.getLatitude(), location.getLongitude()) > 100 && doctorName != null) {
                                NotificationUtils.sendFCMPush(context, doctorName, "Quarantine Circle exceeded ", "Sir, Patient Id " + patientId + " has gone out of his quarentine location. Click the notification to take action", "Patient", "Geofence");
                            }
                        }
                    }
                } else {
                    Log.i("Location Receiver", "Null result");
                }
            }
        }
    }
}
