package com.mbp.sushruta_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientList extends AppCompatActivity {
    FirebaseDatabase fd3;
    DatabaseReference ref3;

    List<GetPatientDetails> patient_obj_list;


    RecyclerView recyclerView3;
    PatientRecyclerView obj3;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "PatientList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b1=getIntent().getExtras();
        final String subdoctor=b1.getString("user");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(getApplicationContext(),Create_Patient.class);
                i.putExtra("Subdoctor",subdoctor);
                startActivity(i);
            }
        });





        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        mLayoutManager = new LinearLayoutManager(this);


        fd3 = FirebaseDatabase.getInstance();
        ref3 = fd3.getReference("sushruta").child("PatientActivity").child(subdoctor);
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                patient_obj_list = new ArrayList<GetPatientDetails>();
                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();

                for (DataSnapshot ds1 : ds.getChildren()) {
                    GetPatientDetails obj = new GetPatientDetails();
                    String Username = String.valueOf(ds1.getKey());
                    Log.i(TAG, Username);
                    String Name = String.valueOf(ds1.child("Name").getValue());
                    String ImageUrl = String.valueOf(ds1.child("ImageUrl").getValue());

                    obj.setImageUrl(ImageUrl);
                    obj.setName(Name);
                    obj.setUserName(Username);

                    patient_obj_list.add(obj);
                    Log.i(TAG, "Value = " + Name + ImageUrl);


                }

                recyclerView3.setLayoutManager(mLayoutManager);
                obj3 = new PatientRecyclerView(PatientList.this, patient_obj_list,subdoctor);
                recyclerView3.setAdapter(obj3);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }

        });

    }

}
