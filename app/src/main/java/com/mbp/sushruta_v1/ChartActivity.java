package com.mbp.sushruta_v1;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    String user,param,date;
    List<String> x_axis=new ArrayList();
    List<String> y_axis=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        Bundle bundle=getIntent().getExtras();
        user=bundle.getString("user");
        param=bundle.getString("param");
        date=bundle.getString("date");

        Log.i("Test",user+"  "+param+"  "+date);


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(date);

        final GraphView graph = (GraphView) findViewById(R.id.graph);

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                graph.removeAllSeries();
                 LineGraphSeries<DataPoint> series1=new LineGraphSeries<>();


                 series1.setThickness(10);
                graph.addSeries(series1);
                graph.setTitle("Line Chart");
                graph.setClickable(true);
                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
                graph.setBackgroundColor(Color.GRAY);

                Viewport viewport = graph.getViewport();
                viewport.setScrollable(true);


                Log.i("Test",String.valueOf(dataSnapshot.getValue()));
                int i=0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String time = String.valueOf(dataSnapshot1.child("time").getValue());
                    String val = String.valueOf(dataSnapshot1.child("value").getValue());
//                        value.add(new BarEntry(Float.parseFloat(val),i));

                    Log.i("Test",val+"   "+i);


                    series1.appendData(new DataPoint(i, Double.parseDouble(val)), true, 20);


                    i=i+1;

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    public DataPoint[] data(){
        int n=x_axis.size();     //to find out the no. of data-points
        DataPoint[] values = new DataPoint[n];     //creating an object of type DataPoint[] of size 'n'
        for(int i=0;i<n;i++){
            DataPoint v = new DataPoint(Double.parseDouble(x_axis.get(i)),Double.parseDouble(y_axis.get(i)));
            values[i] = v;
        }
        return values;
    }
}
