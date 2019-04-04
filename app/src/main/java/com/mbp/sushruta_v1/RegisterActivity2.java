package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity2 extends AppCompatActivity {
    TextView textView;
    EditText Name,Age,DoctorID,PhoneNumber,LicenseID;
    private static final String TAG="Screen1";
    ImageView imageView;
    RadioButton radioButton1,radioButton2,radioButton3;
    int gender;

    private final int PICK_IMAGE_REQUEST = 71;

    private ListView UsersList;
    private ArrayList<String> Usernames = new ArrayList<>();

    String reg_under;
    EditText Specialization;
    Uri filePath;
    RadioButton doctorButton,sdoctorButton;
    int position;
    ImageView imageButton;
    RelativeLayout relativeLayout;
    Button b1;
    Map<String,String> map;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference register_reference,position_ref,username_ref;
    String specialisation,name,age,Id,license,phnumber,user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Bundle bundle=getIntent().getExtras();

         user=bundle.getString("Username");


        UsersList =(ListView)findViewById(R.id.listview);

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        firebaseDatabase=FirebaseDatabase.getInstance();


        Specialization=(EditText)findViewById(R.id.Specialisation);
        b1=(Button)findViewById(R.id.b3);

        relativeLayout=(RelativeLayout)findViewById(R.id.rl);


        doctorButton=(RadioButton)findViewById(R.id.Doctor);
        sdoctorButton=(RadioButton)findViewById(R.id.SubDoctor);
        imageButton=(ImageView) findViewById(R.id.image);

        Name=(EditText)findViewById(R.id.Name);
        Age=(EditText)findViewById(R.id.Age);
        DoctorID=(EditText)findViewById(R.id.DoctorId);

        PhoneNumber=(EditText)findViewById(R.id.phonenumber);
        LicenseID=(EditText)findViewById(R.id.licenseid);
        radioButton1=(RadioButton)findViewById(R.id.Male);
        radioButton2=(RadioButton)findViewById(R.id.Female);
        radioButton3=(RadioButton)findViewById(R.id.Other);
        gender=0;


        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        final DatabaseReference dataRef = dataBase.getReference().child("sushruta/DoctorActivity/Head");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Usernames);
        UsersList.setAdapter(arrayAdapter);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String baseValue = ds.getValue().toString();
                    Log.i(TAG, "onChildAdded: " + baseValue);
                    Usernames.add(baseValue);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


        doctorButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    position=1;//Doctor
                    relativeLayout.setVisibility(View.INVISIBLE);
                }

            }
        });

        sdoctorButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    position =2;//Subdoctor
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onViewCreated: " + Specialization.getText().toString());


                name = Name.getText().toString();
                age = Age.getText().toString();
                Id = DoctorID.getText().toString();
                license = LicenseID.getText().toString();
                phnumber = PhoneNumber.getText().toString();
                specialisation = Specialization.getText().toString();

                Log.i(TAG, "onViewCreated: " + String.valueOf(position));
                if (gender == 0 || name.isEmpty() || age.isEmpty() || Id.isEmpty() || license.isEmpty() || phnumber.isEmpty() || specialisation.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), "Please fill the details", Toast.LENGTH_SHORT).show();
                } else {
                    if (filePath != null && !filePath.equals(Uri.EMPTY)) {
                        uploadImage();

                        }
                }
            }



        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose a image"),PICK_IMAGE_REQUEST);


            }
        });

        UsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reg_under=String.valueOf(UsersList.getItemAtPosition(position));
            }
        });


//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.i(TAG, "onViewCreated: "+name);
//                Log.i(TAG, "onViewCreated: "+age);
//                Log.i(TAG, "onViewCreated: "+Id);
//
//
//                Log.i(TAG, "onViewCreated: "+String.valueOf(gender));
//
//                if(gender==0 || name.isEmpty() || age.isEmpty() || Id.isEmpty()){
//                    Toast.makeText(getApplicationContext(),"Please fill the details",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Intent intent= new Intent(getApplicationContext(),RegisterActivity3.class);
//                    intent.putExtra("user",user);
//                    intent.putExtra("name",name);
//                    intent.putExtra("age",age);
//                    intent.putExtra("id",Id);
//                    intent.putExtra("license",license);
//                    intent.putExtra("phonenumber",phnumber);
//                    intent.putExtra("gender",String.valueOf(gender));
//                    startActivity(intent);
//
//                }
//
//
//            }
//        });




    }




    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){

            filePath=data.getData();

            String s=data.getScheme();
            Log.i(TAG, "onActivityResult: "+s);

            try{

                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),filePath);
                imageButton.setImageBitmap(bitmap);


            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private void uploadImage() {

        if(filePath != null)
        {
            map= new HashMap<String,String>();
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(RegisterActivity3.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            //Log.i(TAG, "onSuccess: "+String.valueOf(taskSnapshot.getMetadata().getName()));


                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i(TAG, "onSuccess: "+String.valueOf(uri));
                                    if(position==0) {


                                        if (position == 1) {

                                            register_reference = firebaseDatabase.getReference("sushruta").child("DoctorActivity").child("Head").child(user);
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("ImageUrl", String.valueOf(uri));
                                            map.put("Name", name);
                                            map.put("Age", age);
                                            map.put("Specialization", specialisation);
                                            map.put("DoctorID", Id);

                                            register_reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            String UID = currentFirebaseUser.getUid();
                                            //Toast.makeText(getApplicationContext(), "" + UID, Toast.LENGTH_SHORT).show();
                                            position_ref = firebaseDatabase.getReference("sushruta").child("Login").child("Position").child(UID);
                                            position_ref.setValue("Doctor");


                                            username_ref = firebaseDatabase.getReference("sushruta").child("Login").child("Usernames").child(UID);
                                            username_ref.setValue(user);
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);


                                        }
                                        if (position == 2) {
                                            if (!reg_under.isEmpty()) {
                                                register_reference = firebaseDatabase.getReference("sushruta").child("SubDoctorActivity").child(reg_under).child(user);
                                                Map<String, String> map = new HashMap<String, String>();
                                                map.put("ImageUrl", String.valueOf(uri));
                                                map.put("Name", name);
                                                map.put("Age", age);
                                                map.put("Specialization", specialisation);
                                                map.put("DoctorID", Id);

                                                register_reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                String UID = currentFirebaseUser.getUid();
                                                //Toast.makeText(getApplicationContext(), "" + UID, Toast.LENGTH_SHORT).show();

                                                position_ref = firebaseDatabase.getReference("sushruta").child("Login").child("Position").child(UID);
                                                position_ref.setValue("SubDoctor");


                                                username_ref = firebaseDatabase.getReference("sushruta").child("Login").child("Usernames").child(UID);
                                                username_ref.setValue(user);

                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Select the doctor", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Select a position",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity2.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}

