package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mbp.sushruta_v1.LoginActivity.LOCATION_PERIODIC_WORK;

public class Patient_Information extends AppCompatActivity {
    FirebaseDatabase fd;
    DatabaseReference dataref, listref;
    ImageView imageView;
    TextView Name, Gender, Age;
    EditText Address, BloodGroup, Height, Weight, PatientId, AadharNo, InsuranceID, Medicines, PhoneNo, quarentineLatitude, quarentineLongitude;
    TableLayout layout;
    RelativeLayout documentrl, parameterrl, locationrl, attendancerl, locationhistoryrl;
    String patient,imageUrl;
    Dialog picdialog;
    int PERMISSION_ID = 44;

    String userType;
    SharedPreferences sharedPref;

    private static final String TAG = "Patient_Information";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__information);

        checkPermissionsAndTriggerWorker();
        triggerAttendanceWorker();

        documentrl=(RelativeLayout)findViewById(R.id.documents_rl) ;
        parameterrl=(RelativeLayout)findViewById(R.id.parameters_rl);
        attendancerl=(RelativeLayout)findViewById(R.id.attendance_rl);
        locationrl=(RelativeLayout)findViewById(R.id.location_rl);
        locationhistoryrl=(RelativeLayout)findViewById(R.id.location_history_rl);

        imageView=(ImageView)findViewById(R.id.Patient_profile);
        Name=(TextView) findViewById(R.id.Patient_name);
        Address=(EditText) findViewById(R.id.addressid);
        BloodGroup=(EditText) findViewById(R.id.bloodgroup);
        Gender=(TextView) findViewById(R.id.Gender);
        Age=(TextView) findViewById(R.id.Age);
        Medicines=(EditText) findViewById(R.id.medicineid);
        PatientId=(EditText) findViewById(R.id.idnumber);
        AadharNo=(EditText) findViewById(R.id.adhaarnumber);
        Height=(EditText) findViewById(R.id.heightinches);
        Weight=(EditText) findViewById(R.id.weightinkg);
        InsuranceID=(EditText) findViewById(R.id.insuranceid);
        PhoneNo=(EditText)findViewById(R.id.phone_number);
        quarentineLatitude = (EditText) findViewById(R.id.quarentine_latitude);
        quarentineLongitude = (EditText) findViewById(R.id.quarentine_longitude);

        Name.setEnabled(false);
        Name.setScrollY(40);
        BloodGroup.setEnabled(false);
        Gender.setEnabled(false);
        Age.setEnabled(false);
        AadharNo.setEnabled(false);
        Height.setEnabled(false);
        Weight.setEnabled(false);
        InsuranceID.setEnabled(false);
        PatientId.setEnabled(false);
        Medicines.setEnabled(false);
        Address.setEnabled(false);
        PhoneNo.setEnabled(false);
        quarentineLatitude.setEnabled(false);
        quarentineLongitude.setEnabled(false);

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

       userType = sharedPref.getString("user_type", null);


        fd = FirebaseDatabase.getInstance();

        Bundle b1 = getIntent().getExtras();
        try {
            patient = b1.getString("Patient");

            listref = fd.getReference("sushruta").child("Details").child("Patient").child(patient);

            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds1) {


                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("patient_name", ds1.child("Name").getValue().toString());
                    editor.putString("patient_phone_no", ds1.child("PhoneNo").getValue().toString());
                    editor.putString("quarantine_latitude", ds1.child("Quarentine_Latitude").getValue().toString());
                    editor.putString("quarantine_longitude", ds1.child("Quarentine_Longitude").getValue().toString());
                    editor.apply();


                    String name = String.valueOf(ds1.child("Name").getValue());
                    String age = String.valueOf(ds1.child("Age").getValue());
                    imageUrl = String.valueOf(ds1.child("ImageUrl").getValue());
                    String gender = String.valueOf(ds1.child("Gender").getValue());
                    String bloodGroup=String.valueOf(ds1.child("Blood Group").getValue());
                    String aadhar_no=String.valueOf(ds1.child("Aadhar_no").getValue());
                    String height=String.valueOf(ds1.child("Height").getValue());
                    String weigth=String.valueOf(ds1.child("Weight").getValue());
                    String insurance=String.valueOf(ds1.child("Insurance_ID").getValue());
                    String patientId=String.valueOf(ds1.child("PatientId").getValue());
                    String address=String.valueOf(ds1.child("Address").getValue());
                    String medicine=String.valueOf(ds1.child("Medicines").getValue());
                    String Phoneno=String.valueOf(ds1.child("PhoneNo").getValue());
                    String latitude = String.valueOf(ds1.child("Quarentine_Latitude").getValue());
                    String longitude = String.valueOf(ds1.child("Quarentine_Longitude").getValue());

                    Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                    Name.setText(name);
                    BloodGroup.setText(bloodGroup);
                    Gender.setText(gender);
                    Age.setText(age);
                    AadharNo.setText(aadhar_no);
                    Height.setText(height);
                    Weight.setText(weigth);
                    InsuranceID.setText(insurance);
                    PatientId.setText(patientId);
                    Address.setText(address);
                    Medicines.setText(medicine);
                    PhoneNo.setText(Phoneno);
                    quarentineLatitude.setText(latitude);
                    quarentineLongitude.setText(longitude);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ImageView close_button, zoom_image;
                            picdialog = new Dialog(Patient_Information.this, R.style.AppCompatAlertDialogStyle);
                            picdialog.setContentView(R.layout.popup_image);
                            zoom_image = (ImageView) picdialog.findViewById(R.id.image);
                            close_button = (ImageView) picdialog.findViewById(R.id.delete);

//                            zoom_image.setImageResource(R.drawable.parameters);
                            Glide.with(getApplicationContext()).load(imageUrl).into(zoom_image);
                            picdialog.show();

                            close_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    picdialog.dismiss();
                                }
                            });
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        parameterrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParametersList.class);
                intent.putExtra("user", patient);
                startActivity(intent);
            }
        });

        documentrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Documents_patients.class);
                intent.putExtra("user", patient);
                startActivity(intent);
            }
        });

        attendancerl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Attendance_history.class);
                startActivity(intent);
            }
        });

        locationhistoryrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LocationHistory.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_info_menu, menu);
        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        String position = sharedPref.getString("Position", "SubDoctor");

        Log.d(getLocalClassName() + " position", position);

        if(!position.equals("SubDoctor")){
                for (int i = 0; i < menu.size(); i++)
                    if(menu.getItem(i).getItemId() == R.id.save || menu.getItem(i).getItemId() == R.id.edit){
                        menu.getItem(i).setVisible(false);
                    }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {

            Toast.makeText(getApplicationContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
            FirebaseDatabase fd4 = FirebaseDatabase.getInstance();
            DatabaseReference dataref = fd4.getReference("sushruta").child("Details").child("Patient").child(PatientId.getText().toString());

            Map<String, String> map = new HashMap<String, String>();
            map.put("Name", Name.getText().toString());
            map.put("Aadhar_no", AadharNo.getText().toString());
            map.put("Age", Age.getText().toString());
            map.put("Blood Group", BloodGroup.getText().toString());
            map.put("Height", Height.getText().toString());
            map.put("Weight", Weight.getText().toString());
            map.put("ImageUrl", imageUrl);
            map.put("Insurance_ID", InsuranceID.getText().toString());
            map.put("PatientId", PatientId.getText().toString());
            map.put("Address", Address.getText().toString());
            map.put("Gender", Gender.getText().toString());
            map.put("Medicines", Medicines.getText().toString());
            map.put("PhoneNo", PhoneNo.getText().toString());
            map.put("Quarentine_Latitude", quarentineLatitude.getText().toString());
            map.put("Quarentine_Longitude", quarentineLongitude.getText().toString());
            dataref.setValue(map);

            Name.setEnabled(false);
            BloodGroup.setEnabled(false);
            Gender.setEnabled(false);
            Age.setEnabled(false);
            AadharNo.setEnabled(false);
            Height.setEnabled(false);
            Weight.setEnabled(false);
            InsuranceID.setEnabled(false);
            PatientId.setEnabled(false);
            Medicines.setEnabled(false);
            Address.setEnabled(false);
            PhoneNo.setEnabled(false);
            quarentineLatitude.setEnabled(false);
            quarentineLongitude.setEnabled(false);
        }

        if (id == R.id.edit) {
            Toast.makeText(getApplicationContext(), "Edit Mode On", Toast.LENGTH_SHORT).show();
            Name.setEnabled(true);
            BloodGroup.setEnabled(true);
            Gender.setEnabled(true);
            Age.setEnabled(true);
            AadharNo.setEnabled(true);
            Height.setEnabled(true);
            Weight.setEnabled(true);
            InsuranceID.setEnabled(true);
            Medicines.setEnabled(true);
            Address.setEnabled(true);
            PhoneNo.setEnabled(true);
            quarentineLatitude.setEnabled(true);
            quarentineLongitude.setEnabled(true);
        }

        if (id == R.id.profile) {
            final Dialog dialog = new Dialog(Patient_Information.this);

            dialog.setContentView(R.layout.popup);

            final ImageView imageView = (ImageView) dialog.findViewById(R.id.view4);
            final TextView name = (TextView) dialog.findViewById(R.id.textView);
            final TextView docid = (TextView) dialog.findViewById(R.id.textView2);
            final TextView spec = (TextView) dialog.findViewById(R.id.textView3);
            final TextView licid = (TextView) dialog.findViewById(R.id.textView6);
            ImageView close = (ImageView) dialog.findViewById(R.id.button);

            SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

            String username = sharedPref.getString("Username", "");
            Log.i("test", username);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sushruta").child("Details").child("Doctor").child(username);
            Log.i("test", username);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String Name = String.valueOf(dataSnapshot.child("Name").getValue());
                    String ImageUrl = String.valueOf(dataSnapshot.child("ImageUrl").getValue());
                    String Specialist = String.valueOf(dataSnapshot.child("Specialization").getValue());
                    String DocID = String.valueOf(dataSnapshot.child("DoctorID").getValue());
                    String LicID = String.valueOf(dataSnapshot.child("LicenseID").getValue());

                    Glide.with(getApplicationContext()).load(ImageUrl).into(imageView);
                    name.setText(Name);
                    docid.setText(DocID);
                    spec.setText(Specialist);
                    licid.setText(LicID);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                infodialog.getWindow().setColorMode(Color.TRANSPARENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

        }
        if (id == R.id.logout) {
            SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

            String username = sharedPref.getString("Username", null);
            if (username != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(username);
            }

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "You are Logged Out", Toast.LENGTH_LONG).show();
            if (userType != null) {
                if (userType.equals("doctor")) {
                    Intent i = new Intent(Patient_Information.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (userType.equals("patient")) {
                    Intent i = new Intent(Patient_Information.this, PatientLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

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
            AlertDialog.Builder builder = new AlertDialog.Builder(Patient_Information.this, R.style.AppCompatAlertDialogStyle);
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
                new PeriodicWorkRequest.Builder(LocationWorker.class, 2, TimeUnit.MINUTES, 2, TimeUnit.MINUTES)
                        .build();

//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(LOCATION_PERIODIC_WORK, ExistingPeriodicWorkPolicy.KEEP, locationWork);

        startLocationAlarm();
        Log.i("Test", "Location Worker Triggered");
    }


    private void triggerAttendanceWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresStorageNotLow(false)
                .build();

        PeriodicWorkRequest.Builder dayWorkBuilder =
                new PeriodicWorkRequest.Builder(AttendanceWorker.class, 2, TimeUnit.MINUTES, 2, TimeUnit.MINUTES);

        PeriodicWorkRequest dayWork = dayWorkBuilder.build();

        WorkManager.getInstance(Patient_Information.this).enqueue(dayWork);
    }


    public void startLocationAlarm() {
        Intent intent = new Intent(this, LocationWorkReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 280192, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1000 * 60
                , pendingIntent);

    }
}
