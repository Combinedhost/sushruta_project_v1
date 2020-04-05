package com.mbp.sushruta_v1;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ParameterValuesNew extends AppCompatActivity {

    String param, user;
    TableLayout tableLayout;
    int datePickerYear, datePickerMonth, datePickerDay;
    TextView dateFilter;
    ImageView noDataFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_values_new);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        Bundle bundle = getIntent().getExtras();
        param = bundle.getString("param");
        user = bundle.getString("user");
        Log.i(getLocalClassName(), param);

        dateFilter = (TextView) findViewById(R.id.date_filter);
        dateFilter.getPaint().setUnderlineText(true);
        tableLayout = (TableLayout) findViewById(R.id.tablelayout);
        noDataFound = (ImageView) findViewById(R.id.no_data);
        noDataFound.setVisibility(View.GONE);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_value);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                final Dialog a = new Dialog(ParameterValuesNew.this, R.style.AppCompatAlertDialogStyle);
                a.setContentView(R.layout.getnamelayout);
                final EditText text = (EditText) a.findViewById(R.id.getname);

                final Button b1 = (Button) a.findViewById(R.id.button);
                b1.setText("Update");

                final TextView heading = (TextView) a.findViewById(R.id.textView5);
                heading.setText("Enter the value");


                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateFormat df = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());
                        String time = df.format(Calendar.getInstance().getTime());
                        String string = text.getText().toString();
                        FirebaseDatabase fd = FirebaseDatabase.getInstance();
                        DatabaseReference addv = fd.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(getDate(datePickerDay, datePickerMonth, datePickerYear));
                        String key = addv.push().getKey();
                        Map map = new HashMap();
                        map.put("time", time);
                        map.put("value", string);
                        addv.child(key).setValue(map);
                        a.dismiss();
                    }
                });

                a.show();

            }
        });

        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ParameterValuesNew.this, new DatePickerDialog.OnDateSetListener() {
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

    public void loadValues(String currentDate) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(currentDate);
        Log.i("test", databaseReference.toString());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tableLayout.removeAllViews();
                if (dataSnapshot.exists()) {
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
                        tbrow.setDividerPadding(10);
                        tbrow.addView(t2v);

                        tableLayout.addView(tbrow);

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

            }
        });


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_parameter_values, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.save) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public String getDate(int day, int month, int year) {
        return String.format("%02d", day) + "-" + String.format("%02d", month + 1) + "-" + String.valueOf(year);
    }

}
