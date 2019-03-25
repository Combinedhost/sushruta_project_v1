package com.mbp.sushruta_v1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference listref,dataref;
    String TName = "";
    List<GetDoctorDetails> doctor_obj_list;
    List<String> userList;

    RecyclerView recyclerView;
    DoctorRecyclerView obj1;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "DoctorListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);


        fd = FirebaseDatabase.getInstance();
        listref = fd.getReference("sushruta").child("DoctorActivity").child("Head");
        listref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                doctor_obj_list = new ArrayList<GetDoctorDetails>();
                userList=new ArrayList<>();
                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();

                for (DataSnapshot ds1 : ds.getChildren()) {

                    String Username = String.valueOf(ds1.getValue());



                    dataref=fd.getReference("sushruta").child("Details").child("Doctor").child(Username);

                    dataref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds2) {

                            String Username = String.valueOf(ds2.getKey());
                            Log.i(TAG, Username);
                            if(userList.contains(Username)){

                                int pos=userList.indexOf(Username);
                                Log.i(getLocalClassName(),String.valueOf(pos));
                                doctor_obj_list.remove(pos);
                                userList.remove(pos);
                            }
                            userList.add(Username);

                            GetDoctorDetails obj = new GetDoctorDetails();
                            String Name = String.valueOf(ds2.child("Name").getValue());
                            TName = Name;
                            String Age = String.valueOf(ds2.child("Age").getValue());
                            String Designation = String.valueOf(ds2.child("Designation").getValue());
                            String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                            String Qualification = String.valueOf(ds2.child("Qualification").getValue());
                            String Gender = String.valueOf(ds2.child("Gender").getValue());
                            String Specialization=String.valueOf(ds2.child("Specialization").getValue());
                            String DoctorId=String.valueOf(ds2.child("DoctorID").getValue());
                            obj.setAge(Age);
                            obj.setUsername(Username);
                            obj.setDesignation(Designation);
                            obj.setGender(Gender);
                            obj.setImageUrl(ImageUrl);
                            obj.setName(Name);
                            obj.setQualification(Qualification);
                            obj.setDoctorID(DoctorId);
                            obj.setSpecialization(Specialization);



                            doctor_obj_list.add(obj);
                            Log.i(TAG, "Value = " + Name + "  " + Age + " " + Gender + " " + Designation + " " + ImageUrl + " " + Qualification);

                            recyclerView.setLayoutManager(mLayoutManager);

                            obj1 = new DoctorRecyclerView(getApplicationContext(), doctor_obj_list,DoctorListActivity.this);
                            recyclerView.setAdapter(obj1);
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




}