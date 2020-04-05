package com.mbp.sushruta_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Log.i("Preference", sharedPref.toString());
        String patientId = sharedPref.getString("patient_id", "");

        button = (Button) findViewById(R.id.post_attendance);

//        String dateParameter = new SimpleDateFormat("DD MMM YYYY", Locale.getDefault()).format(new Date());
        DateFormat df = new SimpleDateFormat("d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());

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
            }
        });

    }
}
