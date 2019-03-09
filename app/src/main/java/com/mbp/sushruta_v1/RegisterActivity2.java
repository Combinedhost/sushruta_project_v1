package com.mbp.sushruta_v1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity2 extends AppCompatActivity {
    TextView textView;
    EditText Name,Age,DoctorID,PhoneNumber,LicenseID;
    private static final String TAG="Screen1";
    ImageView imageView;
    RadioButton radioButton1,radioButton2,radioButton3;
    int gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Bundle bundle=getIntent().getExtras();

        final String user=bundle.getString("Username");


        Name=(EditText)findViewById(R.id.Name);
        Age=(EditText)findViewById(R.id.Age);
        DoctorID=(EditText)findViewById(R.id.DoctorId);
        imageView=(ImageView)findViewById(R.id.imageView3);
        PhoneNumber=(EditText)findViewById(R.id.phonenumber);
        LicenseID=(EditText)findViewById(R.id.licenseid);
        radioButton1=(RadioButton)findViewById(R.id.Male);
        radioButton2=(RadioButton)findViewById(R.id.Female);
        radioButton3=(RadioButton)findViewById(R.id.Other);
        gender=0;

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender=1;//Male
                }
            }
        });


        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender=2;//Female
                }

            }
        });


        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender=3;//Other
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=Name.getText().toString();
                String age=Age.getText().toString();
                String Id=DoctorID.getText().toString();
                String license=LicenseID.getText().toString();
                String phnumber=PhoneNumber.getText().toString();
                Log.i(TAG, "onViewCreated: "+name);
                Log.i(TAG, "onViewCreated: "+age);
                Log.i(TAG, "onViewCreated: "+Id);


                Log.i(TAG, "onViewCreated: "+String.valueOf(gender));

                if(gender==0 || name.isEmpty() || age.isEmpty() || Id.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill the details",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent= new Intent(getApplicationContext(),RegisterActivity3.class);
                    intent.putExtra("user",user);
                    intent.putExtra("name",name);
                    intent.putExtra("age",age);
                    intent.putExtra("id",Id);
                    intent.putExtra("license",license);
                    intent.putExtra("phonenumber",phnumber);
                    intent.putExtra("gender",String.valueOf(gender));
                    startActivity(intent);

                }


            }
        });




    }







}

