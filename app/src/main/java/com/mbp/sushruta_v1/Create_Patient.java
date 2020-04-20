package com.mbp.sushruta_v1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private static final int SELECT_LOCATION_PERMISSION_ID = 11;
    FirebaseDatabase fd4;
    DatabaseReference ref4;
    ImageView imageView;
    EditText Name, Address, BloodGroup, Height, Weight, PatientId, Gender, Age, AadharNo, InsuranceID, Medicine, PhoneNo, quarantineLatitude, quarantineLongitude;
    RadioButton radioButton1, radioButton2, radioButton3;
    Button b;
    String gender = "";
    private static final String TAG = "Create_Patient";
    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private static final int TAKE_PHOTO_CODE = 34;

    Map<String, String> map;

    String subdoctor;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Dialog camdialog;
    Button selectLocation;

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
        PhoneNo = (EditText) findViewById(R.id.phone_number);
        quarantineLatitude = (EditText) findViewById(R.id.quarentine_latitude);
        quarantineLongitude = (EditText) findViewById(R.id.quarentine_longitude);
        selectLocation = (Button) findViewById(R.id.select_location);

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Create_Patient.this, SelectLocationActivity.class);
                intent.putExtra("quarantine_latitude", quarantineLatitude.getText().toString());
                intent.putExtra("quarantine_longitude", quarantineLongitude.getText().toString());
                startActivityForResult(intent, SELECT_LOCATION_PERMISSION_ID);
            }
        });


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


                camdialog = new Dialog(Create_Patient.this);
                camdialog.setContentView(R.layout.popup_camera);

                Window window = camdialog.getWindow();
                if (window != null) {
                    window.setGravity(Gravity.CENTER);
                }

                camdialog.setTitle("Select any on of the options");

                ImageView close = (ImageView) camdialog.findViewById(R.id.close);
                ImageView camera = (ImageView) camdialog.findViewById(R.id.camera);
                ImageView gallery = (ImageView) camdialog.findViewById(R.id.gallery);


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
                        if (!rootPath.exists()) {
                            rootPath.mkdirs();
                        }
                        String file = rootPath + "/" + UUID.randomUUID().toString() + ".jpg";
                        Log.i("Test", file);
                        File newfile = new File(file);
                        try {
                            newfile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //     Uri outputFileUri = Uri.fromFile(newfile);
                        Uri outputFileUri = FileProvider.getUriForFile(Create_Patient.this, "com.mbp.sushruta_v1.fileprovider", newfile);
                        filePath = outputFileUri;
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

        if (requestCode == SELECT_LOCATION_PERMISSION_ID && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle.getString("latitude", null) != null) {
                quarantineLatitude.setText(bundle.getString("latitude"));
            }
            if (bundle.getString("longitude", null) != null) {
                quarantineLongitude.setText(bundle.getString("longitude"));
            }

            Toast.makeText(Create_Patient.this, "Location selected", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isValidLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            return false;
        } else if (lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }

    private Boolean isDataValid() {

        if (Name.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid name", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Age.getText().toString().trim().equals("") || (Integer.parseInt(Age.getText().toString()) <= 0)) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid age", Toast.LENGTH_LONG).show();
            return false;
        }

        if (gender.trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly select a gender", Toast.LENGTH_LONG).show();
            return false;
        }

        if (AadharNo.getText().toString().trim().equals("") || AadharNo.getText().toString().length() < 12 || AadharNo.getText().toString().length() > 12) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid aadhar no", Toast.LENGTH_LONG).show();
            return false;
        }

        if (PatientId.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid patient id", Toast.LENGTH_LONG).show();
            return false;
        }

        if (BloodGroup.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid blood group", Toast.LENGTH_LONG).show();
            return false;
        }
        if (InsuranceID.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid insurance id", Toast.LENGTH_LONG).show();
            return false;
        }


        if (Height.getText().toString().trim().equals("") || (Integer.parseInt(Height.getText().toString()) < 40)) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid height", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Weight.getText().toString().trim().equals("") || (Integer.parseInt(Weight.getText().toString()) < 2)) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid weigth", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Address.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid address", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Medicine.getText().toString().trim().equals("")) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid medicine", Toast.LENGTH_LONG).show();
            return false;
        }

        if (PhoneNo.getText().toString().trim().equals("") || PhoneNo.getText().toString().length() < 10 || PhoneNo.getText().toString().length() > 10) {
            Toast.makeText(Create_Patient.this, "Kindly enter a valid phone number", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isValidLatLng(Double.parseDouble(quarantineLatitude.getText().toString()), Double.parseDouble(quarantineLongitude.getText().toString()))) {
            Toast.makeText(Create_Patient.this, "Quarentine location is not vlaid", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void uploadImage() {
        try {

            if (!isDataValid()) {
                return;
            }

            if (filePath != null) {
                map = new HashMap<String, String>();

                final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setTitle("Registering Patient");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
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
                                        map.put("PhoneNo", PhoneNo.getText().toString());
                                        map.put("Quarentine_Latitude", quarantineLatitude.getText().toString());
                                        map.put("Quarentine_Longitude", quarantineLongitude.getText().toString());
                                        if (gender.equals("Male")) {
                                            map.put("Name", "Mr. " + Name.getText().toString());
                                        }
                                        if (gender.equals("Female")) {
                                            map.put("Name", "Mrs. " + Name.getText().toString());
                                        }


                                        dataref.setValue(map);

                                        DatabaseReference loginRef = fd4.getReference("sushruta").child("Login").child("Patient").child(PhoneNo.getText().toString());


                                        Map<String, String> loginMap = new HashMap<String, String>();
                                        loginMap.put("doctor_name", PhoneNo.getText().toString());
                                        loginMap.put("patient_id", PatientId.getText().toString());
                                        loginRef.setValue(loginMap);


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
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Upload a Profile Image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
