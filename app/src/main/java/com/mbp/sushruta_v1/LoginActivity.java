package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private EditText Username, Password;
    private Button LoginButton;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference positionRef, approvalRefsync, approvalRef, approvalspref;
    SharedPreferences sharedPref;
    String position, userId;
    static boolean active = false;
    static String ATTENDANCE_PERIODIC_WORK = "attendance_periodic_work";
    static String LOCATION_PERIODIC_WORK = "locaion_periodic_work";
    int PERMISSION_ID = 44;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);
        LoginButton = (Button) findViewById(R.id.b1);


        firebaseDatabase = FirebaseDatabase.getInstance();

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            final String usernamesp = sharedPref.getString("Username", "");
            if (!usernamesp.isEmpty()) {

                dialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
                //        dialog.setTitle("Logging in");
                dialog.show();
                dialog.setMessage("Checking for valid session");
                dialog.setCancelable(false);
                approvalspref = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(usernamesp).child("Approval");

                approvalspref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String approval = dataSnapshot.getValue().toString();
                        if (approval.equals("Approved")) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Session Valid", Toast.LENGTH_SHORT).show();
                            String position = sharedPref.getString("Position", "SubDoctor");
                            Log.i("test", position);
                            if (position.equals("Head")) {

                                Intent intent = new Intent(LoginActivity.this, DoctorListActivity.class);
                                //intent.putExtra("user", userId);
                                startActivity(intent);
                            } else if (position.equals("Doctor")) {
                                Intent intent = new Intent(LoginActivity.this, SubDoctorListActivity.class);
                                intent.putExtra("user", usernamesp);
                                startActivity(intent);
                            } else if (position.equals("SubDoctor")) {
                                Intent intent = new Intent(LoginActivity.this, PatientList.class);
                                intent.putExtra("user", usernamesp);
                                startActivity(intent);
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No Valid Session", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                approvalspref = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(usernamesp).child("Approval");

                approvalspref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String approval = dataSnapshot.getValue().toString();
                        if (approval.equals("Not Approved")) {
                            Toast.makeText(getApplicationContext(), "Your Account disapproved by doctor. Please contact Doctor", Toast.LENGTH_LONG).show();
                            if (!active) {
                                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        }
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDetails();
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
// dont call **super**, if u want disable back button in current screen.
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    private void LoginDetails() {
        dialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
//        dialog.setTitle("Logging in");
        dialog.show();
        dialog.setMessage("Logging in");
        String emailaddress = Username.getText().toString();
        String password = Password.getText().toString();

        if (emailaddress.isEmpty() && password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and Password Fields are Empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (emailaddress.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username Field is Empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Field is Empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            Validate(emailaddress, password);
        }
    }

    private void Validate(final String userName, String passWord) {

        firebaseAuth.signInWithEmailAndPassword(userName, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String UID = currentFirebaseUser.getUid();

                    if (currentFirebaseUser.isEmailVerified()) {
                        positionRef = firebaseDatabase.getReference("sushruta").child("Login").child("Info").child(UID);
                        positionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userId = dataSnapshot.child("Username").getValue().toString();
                                Log.i(getLocalClassName(), "Username = " + userId);
                                Log.i(getLocalClassName(), userId);

                                //Subscribe for notification
                                FirebaseMessaging.getInstance().subscribeToTopic(userId);
                                FirebaseMessaging.getInstance().subscribeToTopic("All");


                                position = dataSnapshot.child("Position").getValue().toString();

                                //Shared Preferences
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("Username", userId);
                                editor.putString("Position", position);
                                editor.apply();


                                approvalRef = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(userId).child("Approval");


                                approvalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        String approval = dataSnapshot1.getValue().toString();
                                        if (approval.equals("Approved")) {
                                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                                            final GlobalClass globalClass = (GlobalClass) getApplicationContext();
                                            globalClass.setPosition(position);
                                            Log.i(getLocalClassName(), "Position = " + globalClass.getPosition());

                                            if (position.equals("Head")) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, DoctorListActivity.class);
                                                //intent.putExtra("user", userId);
                                                FirebaseMessaging.getInstance().subscribeToTopic("Head");
                                                startActivity(intent);
                                            } else if (position.equals("Doctor")) {
                                                dialog.dismiss();
                                                FirebaseMessaging.getInstance().subscribeToTopic(userId);
                                                Intent intent = new Intent(LoginActivity.this, SubDoctorListActivity.class);
                                                intent.putExtra("user", userId);
                                                startActivity(intent);
                                            } else if (position.equals("SubDoctor")) {
                                                dialog.dismiss();
                                                FirebaseMessaging.getInstance().subscribeToTopic(userId);
                                                Intent intent = new Intent(LoginActivity.this, PatientList.class);
                                                intent.putExtra("user", userId);
                                                startActivity(intent);
                                            }

                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Account is not Approved. Please contact Doctor", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                approvalspref = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(userId).child("Approval");

                                approvalspref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String approval = dataSnapshot.getValue().toString();
                                        if (approval.equals("Not Approved")) {
                                            Toast.makeText(getApplicationContext(), "Your Account disapproved by doctor. Please contact Doctor", Toast.LENGTH_LONG).show();
                                            if (!active) {
                                                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                approvalRef.keepSynced(false);

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        positionRef.keepSynced(false);

                    } else {
                        Toast.makeText(getApplicationContext(), "Email is not verified", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Incorrect email address", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}