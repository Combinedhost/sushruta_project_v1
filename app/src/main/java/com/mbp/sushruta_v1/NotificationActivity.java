package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NotificationActivity extends AppCompatActivity {

    Button button;
    TextView patient, takeSelfieMessage;
    ImageView selfieImage;
    public static int TAKE_PHOTO_CODE = 10;
    Uri filePath;
    ProgressDialog progressDialog;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
    public static int SELFIE_FREQUENCY_IN_MINUTES = 3 * 60;
    private Boolean takeSelfie = false;
    FirebaseDatabase dataBase;
    DatabaseReference databaseRef;
    String patientId;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Log.i("Preference", sharedPref.toString());
        patientId = sharedPref.getString("patient_id", "");
        String patientName = sharedPref.getString("patient_name", "");

        progressDialog = new ProgressDialog(NotificationActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle("Submitting Attendance");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        takeSelfieMessage = (TextView) findViewById(R.id.take_selfie_message);
        takeSelfieMessage.setVisibility(View.GONE);
        selfieImage = (ImageView) findViewById(R.id.patient_selfie);
        patient = (TextView) findViewById(R.id.textView5);
        patient.setText("Hi " + patientName);

        button = (Button) findViewById(R.id.post_attendance);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String lastUpdateValue = sharedPref.getString("last_attendance_time", null);
        if (!(lastUpdateValue == null)) {
            if (findDifferenceInMinutesWithCurrentTime(lastUpdateValue) > SELFIE_FREQUENCY_IN_MINUTES) {
                takeSelfie = true;
                takeSelfieMessage.setVisibility(View.VISIBLE);
            }
            Log.i("test", String.valueOf(findDifferenceInMinutesWithCurrentTime(lastUpdateValue)));
        }else{
            takeSelfie = true;
            takeSelfieMessage.setVisibility(View.VISIBLE);
        }

        dataBase = FirebaseDatabase.getInstance();
        databaseRef = dataBase.getReference("sushruta").child("Details").child("Attendance").child(patientId).child(date);

        selfieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermissionsAndCaptureSelfie();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takeSelfie) {
                    uploadImage();
                } else {
                    postAttendance();
                }
            }
        });
    }

    public void postAttendance(String selfieUrl) {
        String date = dateFormat.format(new Date());
        String key = databaseRef.push().getKey();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("time", date);
        map1.put("selfie_url", selfieUrl);
        databaseRef.child(key).setValue(map1);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_attendance_time", date);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Your attendance has been posted successfully", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        button.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);

    }

    public void postAttendance() {
        String date = dateFormat.format(new Date());
        String key = databaseRef.push().getKey();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("time", date);
        databaseRef.child(key).setValue(map1);

        Toast.makeText(getApplicationContext(), "Your attendance has been posted successfully", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_attendance_time", date);
        editor.apply();

        button.setEnabled(false);
        progressDialog.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    public int findDifferenceInMinutesWithCurrentTime(String date) {
        Date d1, d2;
        d2 = new Date();
        try {
            d1 = dateFormat.parse(date);
            long difference = d2.getTime() - d1.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int mins = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours))
                    / (1000 * 60);
            return mins;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void startSelfieIntent() {
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
        Uri outputFileUri = FileProvider.getUriForFile(NotificationActivity.this, "com.mbp.sushruta_v1.fileprovider", newfile);
        filePath = outputFileUri;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSelfieIntent();

                } else {
                    Toast.makeText(NotificationActivity.this, "Kindly grant permission to create bill.", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public void CheckPermissionsAndCaptureSelfie() {
        if (ContextCompat.checkSelfPermission(NotificationActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        } else {
            startSelfieIntent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            try {
                Log.i("FilePath", filePath.toString());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                selfieImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        try {

            if (filePath != null) {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                Map<String, String> map = new HashMap<String, String>();
                progressDialog.show();

                StorageReference ref = firebaseStorage.getReference().child("attendance").child(patientId).child(UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.i("test", "onSuccess: " + String.valueOf(uri));
                                        postAttendance(String.valueOf(uri));
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
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload a selfie image to complete attendance", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Some error occurred. Kindly try after some time.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}


