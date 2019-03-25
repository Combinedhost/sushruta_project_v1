package com.mbp.sushruta_v1;


import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    FirebaseDatabase fd;
    DatabaseReference dataref,listref;
    ImageView imageView;
    TextView Name,Gender_age;
    EditText Address,BloodGroup,Height,Weight,PatientId,AadharNo,InsuranceID,Medicines;
    TableLayout layout;

    private static final String TAG = "Patient_Information";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__information);

        Bundle b1=getIntent().getExtras();
        String subdoctor=b1.getString("Subdoctor");
        String patient=b1.getString("Patient");
        Log.i(TAG, subdoctor+"   "+patient);

        imageView=(ImageView)findViewById(R.id.Patient_profile);
        Name=(TextView) findViewById(R.id.Patient_name);
        Address=(EditText) findViewById(R.id.addressid);
        BloodGroup=(EditText) findViewById(R.id.bloodgroup);
        Gender_age=(TextView) findViewById(R.id.Gender_Age);
        Medicines=(EditText) findViewById(R.id.medicineid);
        PatientId=(EditText) findViewById(R.id.idnumber);
        AadharNo=(EditText) findViewById(R.id.adhaarnumber);
        Height=(EditText) findViewById(R.id.heightinches);
        Weight=(EditText) findViewById(R.id.weightinkg);
        InsuranceID=(EditText) findViewById(R.id.insuranceid);

        fd = FirebaseDatabase.getInstance();
        listref = fd.getReference("sushruta").child("Details").child("Patient").child(patient);

        listref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds1) {



                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();





                        String name = String.valueOf(ds1.child("Name").getValue());
                        String age = String.valueOf(ds1.child("Age").getValue());
                        String imageUrl = String.valueOf(ds1.child("ImageUrl").getValue());
                        String gender = String.valueOf(ds1.child("Gender").getValue());
                        String bloodGroup=String.valueOf(ds1.child("Blood Group").getValue());
                        String aadhar_no=String.valueOf(ds1.child("Aadhar_no").getValue());
                        String height=String.valueOf(ds1.child("Height").getValue());
                        String weigth=String.valueOf(ds1.child("Weight").getValue());
                        String insurance=String.valueOf(ds1.child("Insurance_ID").getValue());
                        String patientId=String.valueOf(ds1.child("PatientId").getValue());
                        String address=String.valueOf(ds1.child("Address").getValue());
                        String medicine=String.valueOf(ds1.child("Medicines").getValue());
                        Log.i(TAG,name+"  "+age+"  "+imageUrl);

                        Glide.with(Patient_Information.this).load(imageUrl).into(imageView);
                        Name.setText(name);
                        BloodGroup.setText(bloodGroup);
                        Gender_age.setText(gender+"  "+age);
                        AadharNo.setText(aadhar_no);
                        Height.setText(height);
                        Weight.setText(weigth);
                        InsuranceID.setText(insurance);
                        PatientId.setText(patientId);
                        Address.setText(address);
                        Medicines.setText(medicine);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

//    public void down(View view) {
//        layout.setVisibility(View.INVISIBLE);
//    }
}
