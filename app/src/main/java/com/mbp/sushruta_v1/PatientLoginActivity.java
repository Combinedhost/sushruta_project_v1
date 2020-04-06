package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.snackbar.Snackbar;
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

import java.util.concurrent.TimeUnit;

public class PatientLoginActivity extends AppCompatActivity {

    private String verificationId;

    //The edittext to input the code
    private EditText otpCode, phoneNo;
    private Button sendOtp, verifyOtp;
    SharedPreferences sharedPref;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;

    String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(PatientLoginActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait..");
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

            Intent intent = new Intent(getApplicationContext(), Patient_Information.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("Patient", sharedPref.getString("patient_id", null));
            startActivity(intent);

        }

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = firebaseDatabase.getReference("sushruta").child("Login").child("Patient").child(phoneNo.getText().toString());
                Log.i("test", databaseReference.toString());
                progressDialog.show();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //Shared Preferences
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("patient_id", dataSnapshot.child("patient_id").getValue().toString());
                            editor.putString("doctor_name", dataSnapshot.child("doctor_name").getValue().toString());
                            editor.apply();

                            patientId = dataSnapshot.child("patient_id").getValue().toString();
                            sendVerificationCode(phoneNo.getText().toString());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(PatientLoginActivity.this, "Phone no is not registered in the databse", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });

            }
        });

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        Log.i("Phone no ", mobile);
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

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                Log.i("PatientLoginActivity", code);
//                editTextCode.setText(code);
                //verifying the code
//                verifyVerificationCode(code);
            }
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

            Log.i("PatientLoginActivity", "code sent -  " + s);
            //storing the verification id that is sent to the user
            verificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        progressDialog.show();
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(PatientLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(PatientLoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), Patient_Information.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Patient", patientId);
                            startActivity(intent);


                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                           Toast.makeText(PatientLoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
