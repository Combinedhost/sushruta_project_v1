package com.mbp.sushruta_v1;


import android.app.Dialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Patient_Information extends AppCompatActivity {
    FirebaseDatabase fd;
    DatabaseReference dataref,listref;
    ImageView imageView,close_button,zoom_image;
    EditText Name,Gender,Age;
    EditText Address,BloodGroup,Height,Weight,PatientId,AadharNo,InsuranceID,Medicines;
    TableLayout layout;
    RelativeLayout documentrl,parameterrl;
    String patient,imageUrl;
    Dialog picdialog;

    private static final String TAG = "Patient_Information";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__information);


        documentrl=(RelativeLayout)findViewById(R.id.documents_rl) ;
        parameterrl=(RelativeLayout)findViewById(R.id.parameters_rl);

        imageView=(ImageView)findViewById(R.id.Patient_profile);
        Name=(EditText) findViewById(R.id.Patient_name);
        Address=(EditText) findViewById(R.id.addressid);
        BloodGroup=(EditText) findViewById(R.id.bloodgroup);
        Gender=(EditText) findViewById(R.id.Gender);
        Age=(EditText) findViewById(R.id.Age);
        Medicines=(EditText) findViewById(R.id.medicineid);
        PatientId=(EditText) findViewById(R.id.idnumber);
        AadharNo=(EditText) findViewById(R.id.adhaarnumber);
        Height=(EditText) findViewById(R.id.heightinches);
        Weight=(EditText) findViewById(R.id.weightinkg);
        InsuranceID=(EditText) findViewById(R.id.insuranceid);

        fd = FirebaseDatabase.getInstance();

        Bundle b1=getIntent().getExtras();
        try {
            patient = b1.getString("Patient");

            listref = fd.getReference("sushruta").child("Details").child("Patient").child(patient);

            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds1) {

                    String name = String.valueOf(ds1.child("Name").getValue());
                    String age = String.valueOf(ds1.child("Age").getValue());
                    imageUrl = String.valueOf(ds1.child("ImageUrl").getValue());
                    String gender = String.valueOf(ds1.child("Gender").getValue());
                    String bloodGroup=String.valueOf(ds1.child("Blood Group").getValue());
                    String aadhar_no=String.valueOf(ds1.child("Aadhar_no").getValue());
                    String height=String.valueOf(ds1.child("Height").getValue());
                    String weigth=String.valueOf(ds1.child("Weight").getValue());
                    String insurance=String.valueOf(ds1.child("Insurance_ID").getValue());
                    String patientId=String.valueOf(ds1.child("PatientId").getValue());
                    String address=String.valueOf(ds1.child("Address").getValue());
                    String medicine=String.valueOf(ds1.child("Medicines").getValue());

                    Log.i(TAG,name);
                    Log.i(TAG,age);
                    Log.i(TAG,imageUrl);
                    Log.i(TAG,gender);
                    Log.i(TAG,bloodGroup);
                    Log.i(TAG,aadhar_no);
                    Log.i(TAG,height);
                    Log.i(TAG,weigth);
                    Log.i(TAG,insurance);
                    Log.i(TAG,patientId);
                    Log.i(TAG,address);
                    Log.i(TAG,medicine);


                    Log.i(TAG,name+"  "+age+"  "+imageUrl);


                    Name.setEnabled(false);
                    BloodGroup.setEnabled(false);
                    Gender.setEnabled(false);
                    Age.setEnabled(false);
                    AadharNo.setEnabled(false);
                    Height.setEnabled(false);
                    Weight.setEnabled(false);
                    InsuranceID.setEnabled(false);
                    PatientId.setEnabled(false);
                    Medicines.setEnabled(false);

                    Glide.with(Patient_Information.this).load(imageUrl).into(imageView);
                    Name.setText(name);
                    BloodGroup.setText(bloodGroup);
                    Gender.setText(gender);
                    Age.setText(age);
                    AadharNo.setText(aadhar_no);
                    Height.setText(height);
                    Weight.setText(weigth);
                    InsuranceID.setText(insurance);
                    PatientId.setText(patientId);
                    Address.setText(address);
                    Medicines.setText(medicine);


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            picdialog = new Dialog(Patient_Information.this);
                            setContentView(R.layout.popup_image);
                            zoom_image=(ImageView)picdialog.findViewById(R.id.imageView3);
                            close_button=(ImageView) picdialog.findViewById(R.id.imageView5);
                            Glide.with(Patient_Information.this).load(imageUrl).into(zoom_image);

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }





        parameterrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ParametersList.class);
                intent.putExtra("user",patient);
                startActivity(intent);
            }
        });

        documentrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Documents_patients.class);
                intent.putExtra("user",patient);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            FirebaseDatabase fd4=FirebaseDatabase.getInstance();
            DatabaseReference dataref = fd4.getReference("sushruta").child("Details").child("Patient").child(PatientId.getText().toString());


            Map<String, String> map = new HashMap<String, String>();
            map.put("Name", Name.getText().toString());
            map.put("Aadhar_no", AadharNo.getText().toString());
            map.put("Age", Age.getText().toString());
            map.put("Blood Group", BloodGroup.getText().toString());
            map.put("Height", Height.getText().toString());
            map.put("Weight", Weight.getText().toString());
            map.put("ImageUrl",imageUrl);
            map.put("Insurance_ID", InsuranceID.getText().toString());
            map.put("PatientId", PatientId.getText().toString());
            map.put("Address", Address.getText().toString());
            map.put("Gender", Gender.getText().toString());
            map.put("Medicines", Medicines.getText().toString());
            dataref.setValue(map);
            return true;
        }
        if (id == R.id.edit) {
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            Name.setEnabled(true);
            BloodGroup.setEnabled(true);
            Gender.setEnabled(true);
            Age.setEnabled(true);
            AadharNo.setEnabled(true);
            Height.setEnabled(true);
            Weight.setEnabled(true);
            InsuranceID.setEnabled(true);
            Medicines.setEnabled(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
