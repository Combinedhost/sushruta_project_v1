package com.mbp.sushruta_v1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Create_Patient extends AppCompatActivity {
    FirebaseDatabase fd4;
    DatabaseReference ref4;
    ImageView imageView;
    EditText Name,Address,BloodGroup,Height,Weight,PatientId,Gender,Age,AadharNo,InsuranceID;
    Button b;
    private static final String TAG = "Create_Patient";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__patient);



        Bundle b1=getIntent().getExtras();
        final String subdoctor=b1.getString("Subdoctor");

        Log.i(TAG, subdoctor);

        b=(Button)findViewById(R.id.button4);
        imageView=(ImageView)findViewById(R.id.imageView2);
        Name=(EditText) findViewById(R.id.Name);
        Address=(EditText)findViewById(R.id.Address);
        BloodGroup=(EditText)findViewById(R.id.BloodGroup);
        Gender=(EditText)findViewById(R.id.Gender);
        Age=(EditText)findViewById(R.id.Age);
        PatientId=(EditText)findViewById(R.id.PatientId);
        AadharNo=(EditText)findViewById(R.id.aadharno);
        Height=(EditText) findViewById(R.id.Height);
        Weight=(EditText)findViewById(R.id.Weight);
        InsuranceID=(EditText)findViewById(R.id.InsuranceId);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                GetPatientDetails obj=new GetPatientDetails();
//                obj.setName(Name.getText().toString());
//                obj.setAadhar_no(AadharNo.getText().toString());
//                obj.setAddress(Address.getText().toString());
//                obj.setAge(Age.getText().toString());
//                obj.setBloodGroup(BloodGroup.getText().toString());
//                obj.setGender(Gender.getText().toString());
//                obj.setHeight(Height.getText().toString());
//                obj.setWeight(Weight.getText().toString());
//                obj.setImageUrl(Weight.getText().toString());
//                obj.setInsurance_ID(InsuranceID.getText().toString());
//                obj.setPatientID(PatientId.getText().toString());

                fd4=FirebaseDatabase.getInstance();
                ref4=fd4.getReference("sushruta").child("PatientActivity").child(subdoctor).child(Name.getText().toString());
                Map<String,String> map=new HashMap<String,String>();
                map.put("Name",Name.getText().toString());
                map.put("Aadhar_no",AadharNo.getText().toString());
                map.put("Age",Age.getText().toString());
                map.put("Blood Group",BloodGroup.getText().toString());
                map.put("Height",Height.getText().toString());
                map.put("Weight",Weight.getText().toString());
                map.put("ImageUrl","   ");
                map.put("Insurance_ID",InsuranceID.getText().toString());
                map.put("PatientId",PatientId.getText().toString());
                map.put("Gender",Gender.getText().toString());
                ref4.setValue(map);




            }
        });


    }
}
