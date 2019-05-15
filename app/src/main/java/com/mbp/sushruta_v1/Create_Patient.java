package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Create_Patient extends AppCompatActivity {

    FirebaseDatabase fd4;
    DatabaseReference ref4;
    ImageView imageView;
    EditText Name, Address, BloodGroup, Height, Weight, PatientId, Gender, Age, AadharNo, InsuranceID, Medicine;
    RadioButton radioButton1, radioButton2, radioButton3;
    Button b;
    String gender;
    private static final String TAG = "Create_Patient";
    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final int TAKE_PHOTO_CODE = 34;

    Map<String, String> map;

    String subdoctor;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Dialog camdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__patient);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        radioButton1 = (RadioButton) findViewById(R.id.Male);
        radioButton2 = (RadioButton) findViewById(R.id.Female);
        radioButton3 = (RadioButton) findViewById(R.id.Other);
        Bundle b1 = getIntent().getExtras();
        subdoctor = b1.getString("Subdoctor");

        Log.i(TAG, subdoctor);

        b = (Button) findViewById(R.id.button2);

        imageView = (ImageView) findViewById(R.id.Patient_profile);
        Name = (EditText) findViewById(R.id.Patient_name);
        Address = (EditText) findViewById(R.id.addressid);
        BloodGroup = (EditText) findViewById(R.id.bloodgroup);

        Age = (EditText) findViewById(R.id.age);

        PatientId = (EditText) findViewById(R.id.idnumber);
        AadharNo = (EditText) findViewById(R.id.adhaarnumber);
        Height = (EditText) findViewById(R.id.heightinches);
        Weight = (EditText) findViewById(R.id.weightinkg);
        InsuranceID = (EditText) findViewById(R.id.insuranceid);
        Medicine = (EditText) findViewById(R.id.medicineid);

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Male";//Male
                }
            }
        });


        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Female";//Female
                }

            }
        });


        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Other";//Other
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                camdialog= new Dialog(Create_Patient.this);
                camdialog.setContentView(R.layout.popup_camera);

                Window window = camdialog.getWindow();
                if(window!=null){
                    window.setGravity(Gravity.CENTER);
                }

                camdialog.setTitle("Select any on of the options");

                ImageView close=(ImageView)camdialog.findViewById(R.id.close);
                ImageView camera=(ImageView)camdialog.findViewById(R.id.camera);
                ImageView gallery=(ImageView)camdialog.findViewById(R.id.gallery);


                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        camdialog.dismiss();
                    }
                });


                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File rootPath = new File(Environment.getExternalStorageDirectory(), "Sushruta");
                        if(!rootPath.exists()) {
                            rootPath.mkdirs();
                        }
                        String file = rootPath+"/"+UUID.randomUUID().toString()+".jpg";
                        Log.i("Test",file);
                        File newfile = new File(file);
                        try {
                            newfile.createNewFile();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        //     Uri outputFileUri = Uri.fromFile(newfile);
                        Uri outputFileUri = FileProvider.getUriForFile(Create_Patient.this, "com.mbp.sushruta_v1.fileprovider", newfile);
                        filePath=outputFileUri;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent intent = new Intent();
                         intent.setType("image/*");
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         startActivityForResult(Intent.createChooser(intent, "Choose a image"), PICK_IMAGE_REQUEST);
                    }
                });

                camdialog.show();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                uploadImage();


            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            String s = data.getScheme();
            Log.i(TAG, "onActivityResult: " + s);

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                camdialog.dismiss();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                camdialog.dismiss();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        try {


            if (filePath != null) {
                map = new HashMap<String, String>();
                final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setTitle("Registering Patient");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
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
                                        Log.i(TAG, "onSuccess: " + String.valueOf(uri));


                                        fd4 = FirebaseDatabase.getInstance();
                                        ref4 = fd4.getReference("sushruta").child("PatientActivity").child(subdoctor);
                                        String key = ref4.push().getKey();
                                        ref4.child(key).setValue(PatientId.getText().toString());

                                        DatabaseReference dataref = fd4.getReference("sushruta").child("Details").child("Patient").child(PatientId.getText().toString());


                                        Map<String, String> map = new HashMap<String, String>();

                                        map.put("Aadhar_no", AadharNo.getText().toString());
                                        map.put("Age", Age.getText().toString());
                                        map.put("Blood Group", BloodGroup.getText().toString());
                                        map.put("Height", Height.getText().toString());
                                        map.put("Weight", Weight.getText().toString());
                                        map.put("ImageUrl", String.valueOf(uri));
                                        map.put("Insurance_ID", InsuranceID.getText().toString());
                                        map.put("PatientId", PatientId.getText().toString());
                                        map.put("Address", Address.getText().toString());
                                        map.put("Gender", gender);
                                        map.put("Medicines", Medicine.getText().toString());
                                        if(gender.equals("Male")){
                                            map.put("Name", "Mr. "+Name.getText().toString());
                                        }
                                        if(gender.equals("Female")){
                                            map.put("Name", "Mrs. "+Name.getText().toString());
                                        }


                                        dataref.setValue(map);

                                        Intent intent = new Intent(getApplicationContext(), PatientList.class);
                                        intent.putExtra("user", subdoctor);
                                        startActivity(intent);


                                    }
                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
//        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                progressDialog.setMessage("Uploading");
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Upload a Profile Image", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
