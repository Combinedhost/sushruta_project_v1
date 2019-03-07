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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Create_Patient extends AppCompatActivity {

    FirebaseDatabase fd4;
    DatabaseReference ref4;
    ImageView imageView;
    EditText Name,Address,BloodGroup,Height,Weight,PatientId,Gender,Age,AadharNo,InsuranceID;
    RadioButton radioButton1,radioButton2,radioButton3;
    Button b;
    String gender;
    private static final String TAG = "Create_Patient";
    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    Map<String,String> map;

    String subdoctor;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__patient);



        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();


        radioButton1=(RadioButton)findViewById(R.id.Male);
        radioButton2=(RadioButton)findViewById(R.id.Female);
        radioButton3=(RadioButton)findViewById(R.id.Other);
        Bundle b1=getIntent().getExtras();
         subdoctor=b1.getString("Subdoctor");

        Log.i(TAG, subdoctor);

        b=(Button)findViewById(R.id.button2);

        imageView=(ImageView)findViewById(R.id.Patient_profile);
        Name=(EditText) findViewById(R.id.Patient_name);
        Address=(EditText)findViewById(R.id.addressid);
        BloodGroup=(EditText)findViewById(R.id.bloodgroup);

        Age=(EditText)findViewById(R.id.age);

        PatientId=(EditText)findViewById(R.id.idnumber);
        AadharNo=(EditText)findViewById(R.id.adhaarnumber);
        Height=(EditText) findViewById(R.id.heightinches);
        Weight=(EditText)findViewById(R.id.weightinkg);
        InsuranceID=(EditText)findViewById(R.id.insuranceid);
//        downb=(ImageButton)findViewById(R.id.imageButton2);

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender="Male";//Male
                }
            }
        });


        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender="Female";//Female
                }

            }
        });


        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    gender="Other";//Other
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose a image"),PICK_IMAGE_REQUEST);


            }
        });

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



                uploadImage();



            }
        });


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
                imageView.setImageBitmap(bitmap);


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
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setTitle("Registering Patient");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                            //Log.i(TAG, "onSuccess: "+String.valueOf(taskSnapshot.getMetadata().getName()));


                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.i(TAG, "onSuccess: "+String.valueOf(uri));


                                        fd4=FirebaseDatabase.getInstance();
                                        ref4=fd4.getReference("sushruta").child("PatientActivity").child(subdoctor).child(PatientId.getText().toString());
                                        Map<String,String> map=new HashMap<String,String>();
                                        map.put("Name",Name.getText().toString());
                                        map.put("Aadhar_no",AadharNo.getText().toString());
                                        map.put("Age",Age.getText().toString());
                                        map.put("Blood Group",BloodGroup.getText().toString());
                                        map.put("Height",Height.getText().toString());
                                        map.put("Weight",Weight.getText().toString());
                                        map.put("ImageUrl",String.valueOf(uri));
                                        map.put("Insurance_ID",InsuranceID.getText().toString());
                                        map.put("PatientId",PatientId.getText().toString());
                                        map.put("Address",Address.getText().toString());
                                        map.put("Gender",gender);
                                        ref4.setValue(map);

                                        Intent intent=new Intent(getApplicationContext(),PatientList.class);
                                        intent.putExtra("user",subdoctor);
                                        startActivity(intent);



                                }
                            });




                        }
                    })
        .addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
@Override
public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
        .getTotalByteCount());
//        progressDialog.setMessage("Uploaded "+(int)progress+"%");
    progressDialog.setMessage("Uploading");
        }
        });
        }
        else {
        Toast.makeText(getApplicationContext(), "Upload a Profile Image", Toast.LENGTH_SHORT).show();
        }
        }
}
