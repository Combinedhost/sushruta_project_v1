package com.mbp.sushruta_v1;


import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Patient_Information extends AppCompatActivity {
    FirebaseDatabase fd4;
    DatabaseReference ref4;
    ImageView imageView;
    TextView Name,Address,BloodGroup,Height,Weight,PatientId,Gender,Age,AadharNo,InsuranceID;
    TableLayout layout;
    ImageButton downb;
    private static final String TAG = "Patient_Information";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__information);

        Bundle b1=getIntent().getExtras();
        String subdoctor=b1.getString("Subdoctor");
        String patient=b1.getString("Patient");
        Log.i(TAG, subdoctor+"   "+patient);

        imageView=(ImageView)findViewById(R.id.imageView2);
        Name=(TextView)findViewById(R.id.Name);
        Address=(TextView)findViewById(R.id.Address);
        BloodGroup=(TextView)findViewById(R.id.BloodGroup);
        Gender=(TextView)findViewById(R.id.Gender);
        Age=(TextView)findViewById(R.id.Age);
        PatientId=(TextView)findViewById(R.id.PatientId);
        AadharNo=(TextView)findViewById(R.id.aadharno);
        Height=(TextView) findViewById(R.id.Height);
        Weight=(TextView)findViewById(R.id.Weight);
        InsuranceID=(TextView)findViewById(R.id.InsuranceId);
        downb=(ImageButton)findViewById(R.id.imageButton2);
        layout=(TableLayout)findViewById(R.id.tablayout);

        fd4 = FirebaseDatabase.getInstance();
        ref4 = fd4.getReference("sushruta").child("PatientActivity").child(subdoctor).child(patient);
        ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();


                String name = String.valueOf(ds.child("Name").getValue(    ));
                String age = String.valueOf(ds.child("Age").getValue());
                String imageUrl = String.valueOf(ds.child("ImageUrl").getValue());
                String gender = String.valueOf(ds.child("Gender").getValue());
                String bloodGroup=String.valueOf(ds.child("Blood Group").getValue());
                String aadhar_no=String.valueOf(ds.child("Aadhar_no").getValue());
                String height=String.valueOf(ds.child("Height").getValue());
                String weigth=String.valueOf(ds.child("Weight").getValue());
                String insurance=String.valueOf(ds.child("Insurance_ID").getValue());
                String patientId=String.valueOf(ds.child("PatientId").getValue());
                String address=String.valueOf(ds.child("Address").getValue());

                Glide.with(Patient_Information.this).load(imageUrl).into(imageView);
                Name.setText(name);
                Age.setText(age);
                BloodGroup.setText(bloodGroup);
                Gender.setText(gender);
                AadharNo.setText(aadhar_no);
                Height.setText(height);
                Weight.setText(weigth);
                InsuranceID.setText(insurance);
                PatientId.setText(patientId);
                Address.setText(address);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }

        });


    }

    public void down(View view) {
        layout.setVisibility(View.INVISIBLE);

    }
}
