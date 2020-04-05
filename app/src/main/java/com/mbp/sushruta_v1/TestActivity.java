package com.mbp.sushruta_v1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.mbp.sushruta_v1.LoginActivity.LOCATION_PERIODIC_WORK;


public class TestActivity extends AppCompatActivity {

    private Button MessageButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test0);

        MessageButton = (Button) findViewById(R.id.message);
        MessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                try {
//                    firebaseDatabase = FirebaseDatabase.getInstance();
//                    dataRef = firebaseDatabase.getReference().child("");
//                    String text = "This is a test";
//                    String toNumber = "919994874831";
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
//                    startActivity(intent);
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//                getLocation();
                checkPermissionsAndTriggerWorker();

//                LocationUtils locationUtils = new LocationUtils(getApplicationContext());
//                if (locationUtils.checkPermissions()) {
//                    requestNewLocationData();
//                } else {
//                    requestPermissions();
//                }
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);





    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        final LocationUtils locationUtils = new LocationUtils(this);
        if (locationUtils.checkPermissions() && locationUtils.isLocationEnabled()) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
//                                requestNewLocationData();
                            } else {
                                Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
                                locationUtils.findDistance(location.getLatitude(), location.getLongitude());

                            }
                            Log.i("Location Provider", String.valueOf(location.getProvider()));

                        }
                    }
            );
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            LocationUtils locationUtils = new LocationUtils(getApplicationContext());
            Log.i("Longitude", String.valueOf(location.getLongitude()));
            Log.i("Latitude", String.valueOf(location.getLatitude()));
            Log.i("Location Accuracy", String.valueOf(location.getAccuracy()));
            locationUtils.findDistance(location.getLatitude(), location.getLongitude());
            Log.i("Location Provider", String.valueOf(location.getProvider()));
        }
    };


    private void checkPermissionsAndTriggerWorker() {
        LocationUtils locationUtils = new LocationUtils(this);
        if (locationUtils.checkPermissions()) {
            checkLocations(locationUtils);
        } else {
            requestPermissions();
        }
    }

    private void checkLocations(LocationUtils locationUtils) {
        if (locationUtils.isLocationEnabled()) {
            triggerLocationWorker();
        } else {
            Toast.makeText(this, "Turn on location to continue login", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                44
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocations(new LocationUtils(this));
        } else {
            Toast.makeText(getApplicationContext(), "Location permission is mandatory. Kindly grant permssion to continue", Toast.LENGTH_LONG).show();
        }
    }

    private void triggerLocationWorker() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false)
                .setRequiresStorageNotLow(false)
                .build();


        PeriodicWorkRequest locationWork =
                new PeriodicWorkRequest.Builder(LocationWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(LOCATION_PERIODIC_WORK, ExistingPeriodicWorkPolicy.KEEP, locationWork);
        Log.i("Test", "Location Worker Triggered");
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (checkPermissions()) {
//            getLocation();
//        }

    }

}




