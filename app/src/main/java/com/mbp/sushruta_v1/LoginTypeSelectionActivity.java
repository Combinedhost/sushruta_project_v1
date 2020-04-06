package com.mbp.sushruta_v1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginTypeSelectionActivity extends AppCompatActivity {

    private Button doctorBtn, patientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_6);


        doctorBtn = (Button) findViewById(R.id.doctor);
        patientBtn = (Button) findViewById(R.id.patient);

        final SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user_type", "doctor");
                editor.apply();
                startDoctorLoginActivity();
            }
        });

        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user_type", "patient");
                editor.apply();
                startPatientLoginActivity();
            }
        });
    }

    public void startDoctorLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void startPatientLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), PatientLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
