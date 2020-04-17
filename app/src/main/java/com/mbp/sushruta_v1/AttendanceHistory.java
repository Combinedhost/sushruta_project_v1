package com.mbp.sushruta_v1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

public class AttendanceHistory extends AppCompatActivity {

    TableLayout tableLayout;
    int datePickerYear, datePickerMonth, datePickerDay;
    TextView dateFilter, date, entries;
    SharedPreferences sharedPref;
    String patientId;
    ImageView noDataFound;
    UtilityClass utilityClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        utilityClass = new UtilityClass(AttendanceHistory.this);
        progressDialog = new ProgressDialog(AttendanceHistory.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.loading_data));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(AttendanceHistory.this, R.style.AppCompatAlertDialogStyle, new DatePickerDialog.OnDateSetListener() {
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
            showMessage(getString(R.string.no_internet), true);
            return;
        }
        progressDialog.show();

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
                                final Dialog picdialog = new Dialog(AttendanceHistory.this, R.style.AppCompatAlertDialogStyle);
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
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
            }
        });
    }

    public void showMessage(String data, Boolean refreshData) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), data, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.refresh), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadValues(dateFilter.getText().toString());
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_menu, menu);
        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String position = sharedPref.getString("Position", "SubDoctor");
        if (!position.equals("patient")) {
            menu.findItem(R.id.message).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        if (id == R.id.logout) {
            String username = sharedPref.getString("Username", null);
            if (username != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(username);
            }

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), getString(R.string.log_out), Toast.LENGTH_LONG).show();
            String userType = sharedPref.getString("user_type", null);
            if (userType != null) {
                if (userType.equals("doctor")) {
                    Intent i = new Intent(AttendanceHistory.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (userType.equals("patient")) {
                    Intent i = new Intent(AttendanceHistory.this, PatientLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        }

        if (id == R.id.message) {
            String text = "Hi ";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String doctorPhoneNumber = sharedPref.getString("doctor_phone_number", null);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=91" + doctorPhoneNumber + "&text=" + text));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
