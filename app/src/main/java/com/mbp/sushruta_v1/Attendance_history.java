package com.mbp.sushruta_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Attendance_history extends AppCompatActivity {

    String param, user;
    TableLayout tableLayout;
    int datePickerYear, datePickerMonth, datePickerDay;
    TextView dateFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);
        Bundle bundle = getIntent().getExtras();
        param = bundle.getString("param");
        user = bundle.getString("user");
        Log.i(getLocalClassName(), param);

        dateFilter = (TextView) findViewById(R.id.date_filter);
        dateFilter.getPaint().setUnderlineText(true);
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);

        final Calendar c = Calendar.getInstance();
        datePickerYear = c.get(Calendar.YEAR);
        datePickerMonth = c.get(Calendar.MONTH);
        datePickerDay = c.get(Calendar.DAY_OF_MONTH);

        dateFilter.setText(getDate(datePickerDay, datePickerMonth, datePickerYear));
        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ParameterValuesNew.this, new DatePickerDialog.OnDateSetListener() {
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
        DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Attendance").child(user).child(param).child(currentDate);
        Log.i("test", databaseReference.toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    tableLayout.removeAllViews();
                    TableRow tbrow0 = new TableRow(getApplicationContext());
                    tbrow0.setPadding(50, 10, 50, 10);
                    tbrow0.setGravity(Gravity.CENTER);
                    TextView tv0 = new TextView(getApplicationContext());
                    tv0.setText("           Time             ");
                    tv0.setAllCaps(true);
                    tv0.setTextColor(Color.BLACK);
                    tv0.setTextSize(18);
                    tv0.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                    tbrow0.addView(tv0);


                    TextView tv1 = new TextView(getApplicationContext());
                    tv1.setText("           Value        ");
                    tv1.setTextColor(Color.BLACK);
                    tv1.setAllCaps(true);
                    tv1.setTextSize(18);
                    tv1.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                    tbrow0.addView(tv1);
                    tableLayout.addView(tbrow0);

                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        i = i + 1;
                        String time = String.valueOf(dataSnapshot1.child("time").getValue());
                        String pred = String.valueOf(dataSnapshot1.child("value").getValue());
                        String com = time + pred;


                        TableRow tbrow = new TableRow(getApplicationContext());
                        tbrow.setGravity(Gravity.CENTER);
                        tbrow.setPadding(50, 10, 50, 10);
                        TextView t1v = new TextView(getApplicationContext());
                        t1v.setText(time);
                        t1v.setTextColor(Color.BLACK);
                        t1v.setGravity(Gravity.CENTER);
                        t1v.setTextSize(18);
                        tbrow.addView(t1v);
                        TextView t2v = new TextView(getApplicationContext());
                        t2v.setText(pred);
                        t2v.setTextColor(Color.BLACK);
                        t2v.setGravity(Gravity.CENTER);
                        t2v.setTextSize(18);
                        tbrow.setDividerPadding(20);
                        tbrow.addView(t2v);

                        tableLayout.addView(tbrow);

                    }
                } else {
                    Log.i("Test", " No values");
                    // Add No data Image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
