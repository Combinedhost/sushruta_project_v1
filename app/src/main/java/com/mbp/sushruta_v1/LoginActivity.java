package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog dialog;
    private EditText Username, Password;
    private Button LoginButton;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference positionRef, userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);
        LoginButton = (Button) findViewById(R.id.b1);

        login();



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

    public void login() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LoginDetails();




    }
});
    }
    private void LoginDetails() {
         dialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setTitle("Logging in");
        dialog.show();
        String emailaddress = Username.getText().toString();
        String password = Password.getText().toString();

        if (emailaddress.isEmpty() && password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username and Password Fields are Empty", Toast.LENGTH_SHORT).show();
        } else if (emailaddress.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Username Field is Empty",  Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Field is Empty",  Toast.LENGTH_SHORT).show();
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
                    Log.i(getLocalClassName(), "UID = "+UID);


                    positionRef = firebaseDatabase.getReference("sushruta/Login/Position").child(UID);
                    userRef = firebaseDatabase.getReference("sushruta/Login/Usernames").child(UID);


                    positionRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String position = dataSnapshot.getValue().toString();

                            Log.i(getLocalClassName(), "Position = "+position);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String userId = dataSnapshot.getValue().toString();
                                    Log.i(getLocalClassName(), "Username = "+userId);
                                    Log.i(getLocalClassName(), userId);

                                    Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_SHORT).show();

                                    final GlobalClass globalClass=(GlobalClass) getApplicationContext();
                                    globalClass.setPosition(position);
                                    Log.i(getLocalClassName(),"Position = "+globalClass.getPosition());

                                    if (position.equals("Head"))
                                        {
                                           dialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this,DoctorListActivity.class);
                                            //intent.putExtra("user", userId);
                                            startActivity(intent);
                                        }
                                    else if(position.equals("Doctor"))
                                    {   dialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this,SubDoctorListActivity.class);
                                        intent.putExtra("user", userId);
                                        startActivity(intent);
                                    }
                                    else if(position.equals("SubDoctor"))
                                    {   dialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this,PatientList.class);
                                        intent.putExtra("user", userId);
                                        startActivity(intent);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }});

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    Toast.makeText(getApplicationContext(), "Incorrect email address", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}