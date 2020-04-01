package com.mbp.sushruta_v1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
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

        button = (Button) findViewById(R.id.post_attendance);

        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        final DatabaseReference dataRef = dataBase.getReference().child("sushruta/Details/Parameters");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Attendance Time", dateFormat);
                dataRef.setValue(map1);
            }
        });

    }
}
