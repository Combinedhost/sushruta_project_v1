package com.mbp.sushruta_v1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    String user, param, date;
    List<Date> x_axis = new ArrayList<Date>();
    List<String> y_axis = new ArrayList<String>();
    GraphView graph;
    DatabaseReference databaseReference;
    DateFormat df;
    UtilityClass utilityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        utilityClass = new UtilityClass(getApplicationContext());

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");
        param = bundle.getString("param");
        date = bundle.getString("date");

        df = new SimpleDateFormat("h:mm:ss a");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(date);

        graph = (GraphView) findViewById(R.id.graph);


        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle(param);

        populateChart(param);
    }

    public void populateChart(final String param) {
        if (!utilityClass.isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), true);
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                graph.removeAllSeries();
                x_axis.clear();
                LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();

                series1.setThickness(10);
                series1.setDataPointsRadius(50);
                graph.addSeries(series1);
                graph.setTitle(param);
                graph.setClickable(true);
                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
                graph.getGridLabelRenderer().setGridColor(Color.BLACK);
                graph.setBackgroundColor(Color.WHITE);
                graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
                graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
                graph.getViewport().setScrollable(true);
                graph.getViewport().setScalable(true);

                int i = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String time = String.valueOf(dataSnapshot1.child("time").getValue());
                    String val = String.valueOf(dataSnapshot1.child("value").getValue());
                    try {
                        Date date = df.parse(time);
                        series1.appendData(new DataPoint(date, Double.parseDouble(val)), true, 20);
                        x_axis.add(date);
                        i = i + 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            return df.format(new Date((long) value));
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });

                if (x_axis.size() > 0) {
                    graph.getViewport().setMinX(x_axis.get(0).getTime());
                    graph.getViewport().setMaxX(x_axis.get(x_axis.size() - 1).getTime());
                    graph.getViewport().setXAxisBoundsManual(true);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
            }
        });
    }

    public void showMessage(String data, Boolean refreshData) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), data, Snackbar.LENGTH_INDEFINITE);
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.popup));
        snackbar.setAction(getString(R.string.refresh), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateChart(param);
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


}
