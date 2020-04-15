package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Arrays;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "SelectLocation";
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationClient;
    Button selectLocation, cancel;
    Double quarantineLatitude = 0.0d, quarantineLongitude = 0.0d;
    DecimalFormat df = new DecimalFormat("#.######");
    public final int REQUEST_LOCATION_PERMISSION_ID = 22;
    public final int ENABLE_LOCATION_PERMISSION_ID = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize the SDK
        Places.initialize(getApplicationContext(), this.getString(R.string.google_maps_key));

// Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        setContentView(R.layout.activity_select_location);

        Bundle bundle = getIntent().getExtras();
        String quarantineLatitudeString = bundle.getString("quarantine_latitude", null);
        String quarantineLongitudeString = bundle.getString("quarantine_longitude", null);

        quarantineLatitude = (quarantineLatitudeString == null || quarantineLatitudeString.equals("")) ? 0.0d : Double.parseDouble(quarantineLatitudeString);
        quarantineLongitude = (quarantineLongitudeString== null || quarantineLongitudeString.equals("")) ? 0.0d : Double.parseDouble(quarantineLongitudeString);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        selectLocation = (Button) findViewById(R.id.select_location);
        cancel = (Button) findViewById(R.id.cancel);

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quarantineLatitude == 0.0 || quarantineLongitude == 0.0) {
                    Toast.makeText(SelectLocationActivity.this, "Kindly long press to select a location", Toast.LENGTH_LONG).show();
                    return;
                }

                SharedPreferences sharedPref = getSharedPreferences("mypref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("quarantine_latitude", df.format(quarantineLatitude));
                editor.putString("quarantine_longitude", df.format(quarantineLongitude));
                editor.apply();

                Intent intent = new Intent();
                intent.putExtra("latitude", df.format(quarantineLatitude)).putExtra("longitude", df.format(quarantineLongitude));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//
//
//// Specify the types of place data to return.
////        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//
//// Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng defaultLocation = new LatLng(22.494204, 77.991709);

        if (quarantineLongitude != 0.0 && quarantineLongitude != 0.0) {
            defaultLocation = new LatLng(quarantineLatitude, quarantineLongitude);
        }

        mMap.addMarker(new MarkerOptions().position(defaultLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));
        if (quarantineLongitude != 0.0 && quarantineLongitude != 0.0) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.0f));
        }
//        mMap.setMinZoomPreference(11);

        LocationUtils locationUtils = new LocationUtils(SelectLocationActivity.this);
        if (locationUtils.checkPermissions()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            requestPermissions(REQUEST_LOCATION_PERMISSION_ID);
        }


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return !checkLocationPermissions();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                quarantineLatitude = latLng.latitude;
                quarantineLongitude = latLng.longitude;
                mMap.addMarker(new MarkerOptions().position(latLng).title("Quarantine Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
            }
        });
    }

    private Boolean checkLocationPermissions() {
        LocationUtils locationUtils = new LocationUtils(SelectLocationActivity.this);
        if (locationUtils.checkPermissions()) {
            if (locationUtils.isLocationEnabled()) {
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectLocationActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setMessage("Kindly turn on location to continue")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .show();
                return false;
            }
        } else {
            requestPermissions(ENABLE_LOCATION_PERMISSION_ID);
            return false;
        }
    }

    private void checkLocations(LocationUtils locationUtils) {
        if (!locationUtils.isLocationEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectLocationActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage("Kindly turn on location to continue")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void requestPermissions(int requestCode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                requestCode
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ENABLE_LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocations(new LocationUtils(this));
        } else if (requestCode == REQUEST_LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "Location permission is mandatory. Kindly grant permssion to continue", Toast.LENGTH_LONG).show();
        }
    }
}
