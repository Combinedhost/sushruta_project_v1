package com.mbp.sushruta_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    FirebaseDatabase fd;
    DatabaseReference  listref;

    List<GetPatientDetails> patient_obj_list;

    List<String> userList;
    RecyclerView recyclerView3;
    PatientRecyclerView obj3;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "PatientList";
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientlist);

        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        mLayoutManager = new LinearLayoutManager(this);


        fd = FirebaseDatabase.getInstance();
        //        if(position.equals("SubDoctor")){
//
//        }
//       else{
//            fbar.setVisibility(View.INVISIBLE);
//        }
        try {

            Bundle b1 = getIntent().getExtras();
            final String subdoctor = b1.getString("user");
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

            String position = sharedPref.getString("Position","SubDoctor");

            Log.d("Test Position",position);

            FloatingActionButton fbar = (FloatingActionButton) findViewById(R.id.fab);

            fbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(getApplicationContext(), Create_Patient.class);
                    i.putExtra("Subdoctor", subdoctor);
                    startActivity(i);
                }
            });


            if(position.equals("Subdoctor")){
                fbar.setVisibility(View.VISIBLE);
            }
            else
            {
                fbar.setVisibility(View.INVISIBLE);
            }

            listref = fd.getReference("sushruta").child("PatientActivity").child(subdoctor);
            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    patient_obj_list = new ArrayList<GetPatientDetails>();
                    userList = new ArrayList<>();
                    //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();

                    for (DataSnapshot ds1 : ds.getChildren()) {


                        String Username = String.valueOf(ds1.getValue());
                        Log.i(TAG, Username);

                        DatabaseReference dataref = fd.getReference("sushruta").child("Details").child("Patient").child(Username);
                        dataref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {


                                String Username = String.valueOf(ds2.getKey());
                                if (userList.contains(Username)) {

                                    int pos = userList.indexOf(Username);
                                    Log.i(getLocalClassName(), String.valueOf(pos));
                                    patient_obj_list.remove(pos);
                                    userList.remove(pos);
                                }
                                userList.add(Username);
                                GetPatientDetails obj = new GetPatientDetails();
                                String Name = String.valueOf(ds2.child("Name").getValue());
                                String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                                String PatientID=String.valueOf(ds2.child("PatientId").getValue());
                                obj.setImageUrl(ImageUrl);
                                obj.setName(Name);
                                obj.setUserName(Username);
                                obj.setPatientID(PatientID);

                                patient_obj_list.add(obj);
                                Log.i(TAG, "Value = " + Name + ImageUrl);

                                recyclerView3.setLayoutManager(mLayoutManager);
                                obj3 = new PatientRecyclerView(PatientList.this, patient_obj_list, subdoctor);
                                recyclerView3.setAdapter(obj3);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }

            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
