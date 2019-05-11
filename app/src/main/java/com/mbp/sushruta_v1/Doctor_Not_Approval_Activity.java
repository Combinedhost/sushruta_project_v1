package com.mbp.sushruta_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Doctor_Not_Approval_Activity extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference listref,dataref;

    List<GetDoctorDetails> sub_doctor_obj_list;
    String doctor;

    RecyclerView recyclerView2;
    Not_Approval_DoctorRecyclerView obj2;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "Doctor_Not_Approved";
    List< String> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_doctor_list);

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mLayoutManager = new LinearLayoutManager(this);
        fd = FirebaseDatabase.getInstance();



            listref = fd.getReference("sushruta").child("DoctorActivity").child("Head");
            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {

                    //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();
                    sub_doctor_obj_list = new ArrayList<GetDoctorDetails>();
                    userList=new ArrayList<>();
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        GetDoctorDetails obj = new GetDoctorDetails();
                        String Username = String.valueOf(ds1.getValue());

                        dataref=fd.getReference("sushruta").child("Details").child("Doctor").child(Username);

                        dataref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {

                                String Username = String.valueOf(ds2.getKey());


                                GetDoctorDetails obj = new GetDoctorDetails();
                                String Name = String.valueOf(ds2.child("Name").getValue());

                                String Age = String.valueOf(ds2.child("Age").getValue());
                                String Designation = String.valueOf(ds2.child("Designation").getValue());
                                String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                                String Qualification = String.valueOf(ds2.child("Qualification").getValue());
                                String Gender = String.valueOf(ds2.child("Gender").getValue());
                                String Specialization=String.valueOf(ds2.child("Specialization").getValue());
                                String DoctorId=String.valueOf(ds2.child("DoctorID").getValue());
                                String PhoneNo=String.valueOf(ds2.child("PhoneNo").getValue());
                                String License=String.valueOf(ds2.child("LicenseID").getValue());

                                obj.setAge(Age);
                                obj.setUsername(Username);
                                obj.setDesignation(Designation);
                                obj.setGender(Gender);
                                obj.setImageUrl(ImageUrl);
                                obj.setName(Name);
                                obj.setQualification(Qualification);
                                obj.setDoctorID(DoctorId);
                                obj.setSpecialization(Specialization);
                                obj.setPhoneNo(PhoneNo);
                                obj.setLicense(License);

                                String approval=String.valueOf(ds2.child("Approval").getValue());
                                if(approval.equals("Not Approved")){
                                    sub_doctor_obj_list.add(obj);
                                    if(userList.contains(Username)){

                                        int pos=userList.indexOf(Username);
                                        Log.i(getLocalClassName(),"Updated  " +userList.get(pos));
                                        sub_doctor_obj_list.remove(pos);
                                        userList.remove(pos);
                                    }
                                    userList.add(Username);
                                }
                                if(approval.equals("Approved")){

                                    if(userList.contains(Username)){

                                        int pos=userList.indexOf(Username);
                                        Log.i(getLocalClassName(),"Updated  " +userList.get(pos));
                                        sub_doctor_obj_list.remove(pos);
                                        userList.remove(pos);
                                    }

                                }

                                Log.i(TAG, "Value = " + Name + "  " + Age + " " + Gender + " " + Designation + " " + ImageUrl + " " + Qualification);

                                recyclerView2.setLayoutManager(mLayoutManager);
                                obj2 = new Not_Approval_DoctorRecyclerView(Doctor_Not_Approval_Activity.this, sub_doctor_obj_list,Doctor_Not_Approval_Activity.this);
                                recyclerView2.setAdapter(obj2);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.approved_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.approved){
            Intent intent=new Intent(this,DoctorListActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }




}