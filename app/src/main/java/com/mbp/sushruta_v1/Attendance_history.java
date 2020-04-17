package com.mbp.sushruta_v1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class Attendance_history extends AppCompatActivity {

    TableLayout tableLayout;
    int datePickerYear, datePickerMonth, datePickerDay;
    TextView dateFilter, date, entries;
    SharedPreferences sharedPref;
    String patientId;
    ImageView noDataFound;
    UtilityClass utilityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        utilityClass = new UtilityClass(Attendance_history.this);

        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        patientId = sharedPref.getString("patient_id", "");

        entries = (TextView) findViewById(R.id.textView11);
        dateFilter = (TextView) findViewById(R.id.date_filter_at);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Attendance_history.this, R.style.AppCompatAlertDialogStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateFilter.setText(getDate(dayOfMonth, month, year));
                        loadValues(dateFilter.getText().toString());
                        datePickerYear = year;
                        datePickerMonth = month;
                        datePickerDay = dayOfMonth;
                    }
                }, datePickerYear, datePickerMonth, datePickerDay);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        loadValues(dateFilter.getText().toString());
    }

    public String getDate(int day, int month, int year) {
        return String.format("%02d", day) + "-" + String.format("%02d", month + 1) + "-" + String.valueOf(year);
    }

    public void loadValues(String currentDate) {
        if (!utilityClass.isNetworkAvailable()) {
            showMessage("Kindly connect to a network to access the service", true);
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Attendance").child(patientId).child(currentDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tableLayout.removeAllViews();
                entries.setText("0");

                if (dataSnapshot.exists()) {

                    int count = (int) dataSnapshot.getChildrenCount();
                    entries.setText(Integer.toString(count));

                    TableRow tbrow0 = new TableRow(getApplicationContext());
                    tbrow0.setPadding(10, 5, 100, 5);
                    tbrow0.setGravity(Gravity.CENTER);

                    TextView tv0 = new TextView(getApplicationContext());
                    tv0.setText("Time");
                    tv0.setGravity(Gravity.CENTER);
                    tv0.setAllCaps(true);
                    tv0.setTextColor(Color.BLACK);
                    tv0.setTextSize(16);
                    tv0.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                    tbrow0.addView(tv0);

                    TextView tv1 = new TextView(getApplicationContext());
                    tv1.setText("Selfie");
                    tv1.setGravity(Gravity.CENTER);
                    tv1.setTextColor(Color.BLACK);
                    tv1.setAllCaps(true);
                    tv1.setTextSize(16);
                    tv1.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                    tbrow0.addView(tv1);
                    tableLayout.addView(tbrow0);

                    int i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        i = i + 1;
                        String time = String.valueOf(dataSnapshot1.child("time").getValue());
                        String com = time;

                        TableRow tbrow = new TableRow(getApplicationContext());
                        tbrow.setGravity(Gravity.CENTER);
                        tbrow.setPadding(50, 5, 50, 5);
                        TextView tv = new TextView(getApplicationContext());
                        tv.setText(time);
                        tv.setTextColor(Color.BLACK);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(14);
                        tbrow.addView(tv);

                        ImageView imageView = new ImageView(getApplicationContext());
                        final String image;
                        if (dataSnapshot1.child("selfie_url").getValue() == null) {
                            image = "https://hollandlift.com/images/placeholder.png";
                        } else {
                            image = dataSnapshot1.child("selfie_url").getValue().toString();
                        }
                        Glide.with(getApplicationContext()).load(image).into(imageView);


                        int width = 100;
                        int height = 100;
                        TableRow.LayoutParams parms = new TableRow.LayoutParams(width, height);
                        imageView.setLayoutParams(parms);
                        tbrow.addView(imageView);
                        tbrow.setDividerPadding(10);
                        tableLayout.addView(tbrow);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ImageView close_button, zoom_image;
                                final Dialog picdialog = new Dialog(Attendance_history.this, R.style.AppCompatAlertDialogStyle);
                                picdialog.setContentView(R.layout.popup_image);
                                zoom_image = (ImageView) picdialog.findViewById(R.id.image);
                                close_button = (ImageView) picdialog.findViewById(R.id.delete);

                                Glide.with(getApplicationContext()).load(image).into(zoom_image);
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
                    if (i > 0) {
                        noDataFound.setVisibility(View.GONE);
                    }
                } else {
                    noDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                utilityClass.showMessage(findViewById(android.R.id.content), "Some error occurred. Kindly try after some time.");
            }
        });
    }

    public void showMessage(String data, Boolean refreshData) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), data, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Refresh", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadValues(dateFilter.getText().toString());
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
