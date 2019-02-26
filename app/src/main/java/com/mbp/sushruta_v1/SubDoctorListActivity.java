package com.mbp.sushruta_v1;


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
public class SubDoctorListActivity extends AppCompatActivity {

    FirebaseDatabase fd2;
    DatabaseReference ref2;

    List<GetDoctorDetails> sub_doctor_obj_list;


    RecyclerView recyclerView2;
    SubDoctorRecyclerView obj2;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "Main2Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_doctor_list);

        Bundle b1=getIntent().getExtras();
        String doctor=b1.getString("user");

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mLayoutManager = new LinearLayoutManager(this);


        fd2 = FirebaseDatabase.getInstance();
        ref2 = fd2.getReference("sushruta").child("SubDoctorActivity").child(doctor);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                sub_doctor_obj_list = new ArrayList<GetDoctorDetails>();
                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();

                for (DataSnapshot ds1 : ds.getChildren()) {
                    GetDoctorDetails obj = new GetDoctorDetails();
                    String Username = String.valueOf(ds1.getKey());
                    Log.i(TAG, Username);
                    String Name = String.valueOf(ds1.child("Name").getValue());
                    String Age = String.valueOf(ds1.child("Age").getValue());
                    String Designation = String.valueOf(ds1.child("Designation").getValue());
                    String ImageUrl = String.valueOf(ds1.child("ImageUrl").getValue());
                    String Qualification = String.valueOf(ds1.child("Qualification").getValue());
                    String Gender = String.valueOf(ds1.child("Gender").getValue());
                    String Specialization=String.valueOf(ds1.child("Specialization").getValue());
                    String DoctorId=String.valueOf(ds1.child("DoctorID").getValue());
                    obj.setAge(Age);
                    obj.setUsername(Username);
                    obj.setDesignation(Designation);
                    obj.setGender(Gender);
                    obj.setImageUrl(ImageUrl);
                    obj.setName(Name);
                    obj.setQualification(Qualification);
                    obj.setDoctorID(DoctorId);
                    obj.setSpecialization(Specialization);
                    //doctorDetailsMap.put(Username,obj);
                    sub_doctor_obj_list.add(obj);
                    Log.i(TAG, "Value = " + Name + "  " + Age + " " + Gender + " " + Designation + " " + ImageUrl + " " + Qualification);


                }

                recyclerView2.setLayoutManager(mLayoutManager);
                obj2 = new SubDoctorRecyclerView(SubDoctorListActivity.this, sub_doctor_obj_list,SubDoctorListActivity.this);
                recyclerView2.setAdapter(obj2);


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }

        });
        // Log.i(TAG, String.valueOf(doctor_obj_list.size()));


        /*
        Log.i(TAG,String.valueOf(doctorDetailsMap.size()));
        Set keys=doctorDetailsMap.keySet();
        Log.i(TAG,String.valueOf(keys.size()));
        Iterator i=keys.iterator();
        if(i.hasNext()){
            Log.i(TAG, String.valueOf(i.next()));
        }
*/





    }


}

