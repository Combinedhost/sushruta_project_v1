package com.mbp.sushruta_v1;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    TextView textView;
    EditText Username,pass,cpass,email;
    private static final String TAG="Screen1";
    ImageView imageView;
    Button button;
FirebaseAuth firebaseAuth;
FirebaseDatabase firebaseDatabase;
DatabaseReference user_ref;
String  user,mail,password,cpassword;

int flag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Username=(EditText)findViewById(R.id.Username);
        pass=(EditText)findViewById(R.id.Password);
        cpass=(EditText)findViewById(R.id.CPassword);
        email=(EditText)findViewById(R.id.editText);
        button=(Button)findViewById(R.id.b3);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        user_ref=firebaseDatabase.getReference("sushruta").child("Login").child("Info");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               user=Username.getText().toString();
                password=pass.getText().toString();
                cpassword=cpass.getText().toString();
                mail=email.getText().toString();
                if(user.isEmpty() || password.isEmpty() || cpassword.isEmpty() || mail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill the required details",Toast.LENGTH_SHORT).show();



                }
                else{
                    if(password.equals(cpassword)){


                        user_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<String> usernames_list=new ArrayList<>();
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    Log.i(getLocalClassName(),ds.getValue().toString());
                                    usernames_list.add(ds.child("Username").getValue().toString());
                                    if(usernames_list.contains(user))
                                    {
                                        Log.i(getLocalClassName(),"Username Exists");
                                        flag=1;
                                        break;
                                    }


                                }

                                if(flag==1)
                                {
                                  Toast.makeText(getApplicationContext(),"Username already exists",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    register();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Passwords does not match",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });







    }


    private  void  register(){



            firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG,"Registeration Successfull");

                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(RegisterActivity.this, RegisterActivity2.class);
                        intent.putExtra("Username",user);
                        startActivity(intent);


                    }
                    if (!task.isSuccessful())
                    {
                        try
                        {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthWeakPasswordException weakPassword)
                        {
                            Log.d(TAG, "onComplete: weak_password");
                            Toast.makeText(RegisterActivity.this, "The password you entered is weak", Toast.LENGTH_LONG).show();
                            // TODO: take your actions!
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            Log.d(TAG, "onComplete: malformed_email");
                            Toast.makeText(RegisterActivity.this, "The email is malformed", Toast.LENGTH_LONG).show();
                            // TODO: Take your action
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            Log.d(TAG, "onComplete: exist_email");
                            Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_LONG).show();
                            // TODO: Take your action
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "onComplete: " + e.getMessage());
                        }
                    }


                }
            });
        }
    }




