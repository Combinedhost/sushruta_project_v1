package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
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
    public static long SELFIE_FREQUENCY_IN_MINUTES = 2 * 60;
    private Boolean takeSelfie = false;
    FirebaseDatabase dataBase;
    DatabaseReference databaseRef;
    String patientId;
    SharedPreferences sharedPref;
    UtilityClass utilityClass;
    public static String LAST_UPDATE_MILLIS = "last_attendance_millis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        patientId = sharedPref.getString("patient_id", "");
        String patientName = sharedPref.getString("patient_name", "");

        utilityClass = new UtilityClass(NotificationActivity.this);
        progressDialog = new ProgressDialog(NotificationActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.submitting_attendance));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        takeSelfieMessage = (TextView) findViewById(R.id.take_selfie_message);
        takeSelfieMessage.setVisibility(View.GONE);
        selfieImage = (ImageView) findViewById(R.id.patient_selfie);
        patient = (TextView) findViewById(R.id.textView5);
        patient.setText(getString(R.string.hi) + " "+ patientName);

        button = (Button) findViewById(R.id.post_attendance);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        Long lastUpdateValue = sharedPref.getLong(LAST_UPDATE_MILLIS, 0);
        if (lastUpdateValue != 0) {
            if (findDifferenceInMinutesWithCurrentTime(lastUpdateValue) > SELFIE_FREQUENCY_IN_MINUTES) {
                takeSelfie = true;
                takeSelfieMessage.setVisibility(View.VISIBLE);
            }
            Log.i("Time difference", String.valueOf(findDifferenceInMinutesWithCurrentTime(lastUpdateValue)));
        } else {
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
                if (!utilityClass.isNetworkAvailable()) {
                    utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.no_internet));
                    return;
                }
                if (takeSelfie) {
                    uploadImage();
                } else {
                    postAttendance();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.attendance_mandatory))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }

    public void postAttendance(String selfieUrl) {
        String date = dateFormat.format(new Date());
        String key = databaseRef.push().getKey();
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("time", date);
        map1.put("selfie_url", selfieUrl);
        databaseRef.child(key).setValue(map1);

        if(takeSelfie){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(LAST_UPDATE_MILLIS, new Date().getTime());
            editor.apply();
        }

        utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.attendance_success));
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
        utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.attendance_success));

        if(takeSelfie) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(LAST_UPDATE_MILLIS, new Date().getTime());
            editor.apply();
        }
        button.setEnabled(false);
        progressDialog.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    public long findDifferenceInMinutesWithCurrentTime(Long lastUpdateMillis) {
        Date d2;
        d2 = new Date();
        long difference = d2.getTime() - lastUpdateMillis;
        return (difference / (60 * 1000));
    }

    public void startSelfieIntent() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), "Sushruta");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        String file = rootPath + "/" + UUID.randomUUID().toString() + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                    utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.storage_permission));
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
                utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.upload_selfie));
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
            e.printStackTrace();
        }
    }

}


