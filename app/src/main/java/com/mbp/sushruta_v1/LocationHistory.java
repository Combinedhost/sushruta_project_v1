package com.mbp.sushruta_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class LocationHistory extends AppCompatActivity {

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    TableLayout tableLayout;
    int datePickerYear, datePickerMonth, datePickerDay;
    TextView dateFilter, date, entries;
    SharedPreferences sharedPref;
    String patientId;
    ImageView noDataFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        patientId = sharedPref.getString("patient_id", "");

        entries = (TextView) findViewById(R.id.textView11);
        dateFilter = (TextView) findViewById(R.id.date_filter_lo);
        noDataFound = (ImageView) findViewById(R.id.no_data);
        noDataFound.setVisibility(View.GONE);

        dateFilter.getPaint().setUnderlineText(true);
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);

        ImageView im = (ImageView) findViewById(R.id.imageView4);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Calendar c = Calendar.getInstance();
        datePickerYear = c.get(Calendar.YEAR);
        datePickerMonth = c.get(Calendar.MONTH);
        datePickerDay = c.get(Calendar.DAY_OF_MONTH);

        dateFilter.setText(getDate(datePickerDay, datePickerMonth, datePickerYear));
        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(LocationHistory.this, R.style.AppCompatAlertDialogStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateFilter.setText(getDate(dayOfMonth, month, year));
                        loadValues(dateFilter.getText().toString());
                        datePickerYear=year;
                        datePickerMonth=month;
                        datePickerDay=dayOfMonth;
                    }
                }, datePickerYear, datePickerMonth, datePickerDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        loadValues(dateFilter.getText().toString());
    }

    public String getDate(int day, int month, int year) {
        return String.format("%02d", day) + "-" + String.format("%02d", month+1) + "-" + String.valueOf(year);
    }

    public void loadValues(String currentDate){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Location").child(patientId).child(currentDate);
        Log.i("test", databaseReference.toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("test", dataSnapshot.toString());
                if (dataSnapshot.exists()) {

                    tableLayout.removeAllViews();
                    TableRow tbrow0 = new TableRow(getApplicationContext());
                    tbrow0.setPadding(50, 10, 50, 10);
                    tbrow0.setGravity(Gravity.CENTER);

                    TextView tv1 = new TextView(getApplicationContext());
                    tv1.setText("           Location        ");
                    tv1.setTextColor(Color.BLACK);
                    tv1.setAllCaps(true);
                    tv1.setTextSize(18);
                    tv1.setTypeface(tv1.getTypeface(), Typeface.BOLD);
                    tbrow0.addView(tv1);
                    tableLayout.addView(tbrow0);

                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        i = i + 1;
                        String time = String.valueOf(dataSnapshot1.child("time").getValue());
                        String com = time;

                        TableRow tbrow = new TableRow(getApplicationContext());
                        tbrow.setGravity(Gravity.CENTER);
                        tbrow.setPadding(50, 10, 50, 10);
                        TextView t1v = new TextView(getApplicationContext());
                        t1v.setText(time);
                        t1v.setTextColor(Color.BLACK);
                        t1v.setGravity(Gravity.CENTER);
                        t1v.setTextSize(18);
                        tbrow.addView(t1v);
                        tbrow.setDividerPadding(20);
                        tableLayout.addView(tbrow);
                    }
                    if (i > 0) {
                        noDataFound.setVisibility(View.GONE);
                    }
                } else {
                    Log.i("Test", " No values");
                    noDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
