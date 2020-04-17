package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PatientLoginActivity extends AppCompatActivity {

    private String verificationId;

    //The edittext to input the code
    private EditText otpCode, phoneNo;
    private Button sendOtp, verifyOtp;
    SharedPreferences sharedPref;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;
    UtilityClass utilityClass;
    String patientId, doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        utilityClass = new UtilityClass(getApplicationContext());
        loadLocale();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(PatientLoginActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.loading_data));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        otpCode = (EditText) findViewById(R.id.otp);
        phoneNo = (EditText) findViewById(R.id.phone_no);

        sendOtp = (Button) findViewById(R.id.send_sms);
        verifyOtp = (Button) findViewById(R.id.verify_otp);

        otpCode.setVisibility(View.GONE);
        verifyOtp.setVisibility(View.GONE);

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {

            Intent intent = new Intent(getApplicationContext(), PatientInformation.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("Patient", sharedPref.getString("patient_id", null));
            startActivity(intent);
        }

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!utilityClass.isNetworkAvailable()) {
                    utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.no_internet));
                    return;
                }

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Login").child("Patient").child(phoneNo.getText().toString());

                progressDialog.show();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            patientId = dataSnapshot.child("patient_id").getValue().toString();
                            doctorName = dataSnapshot.child("doctor_name").getValue().toString();

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(doctorName);

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String doctorPhoneNumber = dataSnapshot.child("PhoneNo").getValue().toString();
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("doctor_phone_number", doctorPhoneNumber);
                                        editor.apply();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
                                }
                            });

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("doctor_name", doctorName);
                            editor.apply();

                            sendVerificationCode(phoneNo.getText().toString());
                        } else {
                            progressDialog.dismiss();
                            utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.not_registered));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
                    }
                });

            }
        });

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!utilityClass.isNetworkAvailable()) {
                    utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.no_internet));
                    return;
                }
                verifyVerificationCode(otpCode.getText().toString());
            }
        });

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            progressDialog.dismiss();
            otpCode.setVisibility(View.VISIBLE);
            verifyOtp.setVisibility(View.VISIBLE);
            sendOtp.setVisibility(View.GONE);
            verificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(PatientLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(PatientLoginActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("patient_id", patientId);
                            editor.putString("doctor_name", doctorName);
                            editor.putString("Position", "patient");
                            editor.apply();


                            Intent intent = new Intent(getApplicationContext(), PatientInformation.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Patient", patientId);
                            startActivity(intent);

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = getString(R.string.some_error_occurred);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = getString(R.string.invalid_code);
                            }

                            utilityClass.showMessage(findViewById(android.R.id.content), message);

                        }
                    }
                });
    }

    public void loadLocale() {
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        String language = pref.getString("language", "");
        setLocale(language);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("mypref", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

}
