package com.mbp.sushruta_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    Button button;
    TextView patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Log.i("Preference", sharedPref.toString());
        String patientId = sharedPref.getString("patient_id", "");
        String patientName = sharedPref.getString("patient_name", "");

        patient = (TextView) findViewById(R.id.textView5);
        patient.setText("Hi " + patientName);

        button = (Button) findViewById(R.id.post_attendance);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        final FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        final DatabaseReference dataRef = dataBase.getReference("sushruta").child("Details").child("Attendance").child(patientId).child(date);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault()).format(new Date());

                String key = dataRef.push().getKey();
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("time", dateFormat);
                dataRef.child(key).setValue(map1);

//                showMessage("Your Attendance Submitted Successfully");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
    }

//    public void showMessage(String data) {
//        Snackbar snackbar = Snackbar
//                .make(coordinatorLayout, data, Snackbar.LENGTH_LONG)
//                .setAction("Okay", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        snackbar.dismiss();
//                    }
//                });
//
//        snackbar.show();
//    }
}
