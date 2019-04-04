package com.mbp.sushruta_v1;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Documents_patients extends Activity {

    private static final int PICK_FILE = 40;
    private Uri filePath;
    Map<String, String> map;
    FirebaseStorage storage;
    StorageReference storageReference;
    String user;
    List<String> imageList,UIDList,NameList,MimeList;
    String filename,mimetype;
    Recyclerview_images recyclerviewImages;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    FirebaseDatabase putfirebaseDatabase,getfirebaseDatabase;
    DatabaseReference putdatabaseReference,getdatabaseReference,moddatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_patients);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        Bundle bundle=getIntent().getExtras();
         user=bundle.getString("user");

        getfirebaseDatabase=FirebaseDatabase.getInstance();
        getdatabaseReference=getfirebaseDatabase.getReference("sushruta").child("Details").child("Documents").child(user);



        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);

        gridLayoutManager=new GridLayoutManager(getApplicationContext(),2);

        recyclerView.setLayoutManager(gridLayoutManager);

        getdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageList=new ArrayList<>();
                UIDList=new ArrayList<>();
                NameList=new ArrayList<>();
                MimeList=new ArrayList<>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Log.i(getLocalClassName(),"key = "+dataSnapshot1.getKey());
                    UIDList.add(String.valueOf(dataSnapshot1.getKey()));
                    String img=String.valueOf(dataSnapshot1.child("ImageURL").getValue());
                    String name=String.valueOf(dataSnapshot1.child("Name").getValue());
                    String mime=String.valueOf(dataSnapshot1.child("Mime").getValue());
                    //Log.i(TAG,"Images "+String.valueOf(dataSnapshot1.child("ImageURL").getValue()));

                    if(!img.isEmpty() && !img.equals("null")){
                        imageList.add(String.valueOf(dataSnapshot1.child("ImageURL").getValue()));
                        NameList.add(name);
                        MimeList.add(mime);

                    }
                }


                Log.i(getLocalClassName(),"List size "+imageList.size());

                Recyclerview_images recyclerview_images=new Recyclerview_images(Documents_patients.this,imageList,NameList,UIDList,MimeList,user);
                recyclerView.setAdapter(recyclerview_images);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        FloatingActionButton Add_file_fab=(FloatingActionButton)findViewById(R.id.add_file_fab);

      Add_file_fab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent();
              intent.setType("*/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(Intent.createChooser(intent,"Select the File"),PICK_FILE);
          }
      });




        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }



    }

    @Override
    protected void onActivityResult(int requestode,int resultCode,Intent data){
        super.onActivityResult(requestode,resultCode,data);
        if(requestode==PICK_FILE && resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            filePath=data.getData();
            filename=getFileName(filePath);


            Log.i(getLocalClassName(),"Minetype = "+mimetype);

            final Dialog dialog=new Dialog(this,R.style.AppCompatAlertDialogStyle);

            dialog.setContentView(R.layout.getnamelayout);

            final EditText name=(EditText)dialog.findViewById(R.id.getname);
            name.setText(filename);

            Button button=(Button)dialog.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(getLocalClassName(),name.getText().toString());
                    dialog.dismiss();
                    filename=name.getText().toString();
                    uploadImage(filename);
                }
            });

            dialog.show();


        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void uploadImage(final String file) {

        if(filePath != null)
        {
            map = new HashMap<String, String>();
            final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+file);
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
                                    Log.i(getLocalClassName(), "onSuccess: "+String.valueOf(uri));
                                    mimetype=getMimeType(filePath);
                                    map.put("ImageURL",String.valueOf(uri));
                                    map.put("Name",file);
                                    map.put("Mime",mimetype);


                                    putfirebaseDatabase=FirebaseDatabase.getInstance();
                                    putdatabaseReference=putfirebaseDatabase.getReference("sushruta").child("Details").child("Documents").child(user);
                                    String key=putdatabaseReference.push().getKey();
                                    putdatabaseReference.child(key).setValue(map);



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
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(getLocalClassName(), "external public folder access granted");
                } else {
                    Log.e(getLocalClassName(), "external public folder access denied sending user to main screen");
                    Toast.makeText(this,
                            "Please grant permission to access public folders to use the feature",
                            Toast.LENGTH_SHORT).show();

                }

            }
        }
    }

}
