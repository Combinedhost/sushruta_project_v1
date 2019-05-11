package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private EditText Username, Password;
    private Button LoginButton;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference positionRef, userRef;
    SharedPreferences sharedPref;
    String position,userId;

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

        sharedPref =  this.getPreferences(Context.MODE_PRIVATE);


        //

        //

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Toast.makeText(getApplicationContext(),"Session User",Toast.LENGTH_SHORT).show();
        if (user != null) {

            //selectionProcess();
            Toast.makeText(getApplicationContext(), "Session valid", Toast.LENGTH_SHORT).show();

            String position = sharedPref.getString("Position","SubDoctor");
            Log.i("test",position);
            String username=sharedPref.getString("Username","");


            if (position.equals("Head")) {

                Intent intent = new Intent(LoginActivity.this, DoctorListActivity.class);
                //intent.putExtra("user", userId);
                startActivity(intent);
            }
            else if (position.equals("Doctor")) {

                Intent intent = new Intent(LoginActivity.this, SubDoctorListActivity.class);
                intent.putExtra("user", username);
                startActivity(intent);
            }
            else if (position.equals("SubDoctor")) {

                Intent intent = new Intent(LoginActivity.this, PatientList.class);
                intent.putExtra("user", username);
                startActivity(intent);
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
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }


    private void LoginDetails() {
         dialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
//        dialog.setTitle("Logging in");
        dialog.show();
        dialog.setMessage("Logging in");
        String emailaddress = Username.getText().toString();
        String password = Password.getText().toString();

        if (emailaddress.isEmpty() && password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and Password Fields are Empty", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (emailaddress.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username Field is Empty",  Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Field is Empty",  Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            Validate(emailaddress, password);
        }
    }

    private void Validate(final String userName, String passWord){

        firebaseAuth.signInWithEmailAndPassword(userName,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    String UID=currentFirebaseUser.getUid();

                    if(currentFirebaseUser.isEmailVerified()){
                        positionRef = firebaseDatabase.getReference("sushruta").child("Login").child("Info").child(UID);
                        positionRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userId = dataSnapshot.child("Username").getValue().toString();
                                Log.i(getLocalClassName(), "Username = " + userId);
                                Log.i(getLocalClassName(), userId);

                                //Subscribe for notification
                                FirebaseMessaging.getInstance().subscribeToTopic(userId);



                                position = dataSnapshot.child("Position").getValue().toString();

                                //Shared Preferences
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("Username", userId);
                                editor.putString("Position",position);
                                editor.apply();

                                DatabaseReference approvalRef = firebaseDatabase.getReference("sushruta").child("Details").child("Doctor").child(userId).child("Approval");

                                approvalRef.addValueEventListener(new ValueEventListener() {
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
                                                startActivity(intent);
                                            } else if (position.equals("Doctor")) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, SubDoctorListActivity.class);
                                                intent.putExtra("user", userId);
                                                startActivity(intent);
                                            } else if (position.equals("SubDoctor")) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, PatientList.class);
                                                intent.putExtra("user", userId);
                                                startActivity(intent);
                                            }

                                        }
                                        else
                                        {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Account is not Approved. Please contact Doctor",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });




                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Email is not verified",Toast.LENGTH_SHORT).show();
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