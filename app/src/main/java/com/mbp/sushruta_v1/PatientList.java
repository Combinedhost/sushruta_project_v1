package com.mbp.sushruta_v1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientList extends AppCompatActivity {
    FirebaseDatabase fd;
    DatabaseReference  listref;

    List<GetPatientDetails> patient_obj_list;
    TextView no_results;
    List<String> userList,UIDList;
    RecyclerView recyclerView3;
    PatientRecyclerView obj3;


    LinearLayoutManager mLayoutManager;
    private static final String TAG = "PatientList";
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientlist);

        recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        mLayoutManager = new LinearLayoutManager(this);

        no_results=(TextView) findViewById(R.id.notavailable);
        fd = FirebaseDatabase.getInstance();
        try {

            Bundle b1 = getIntent().getExtras();
            final String subdoctor = b1.getString("user");


            SharedPreferences sharedPref = this.getSharedPreferences("mypref",Context.MODE_PRIVATE);

            String position = sharedPref.getString("Position","SubDoctor");

            Log.d("Test Position",position);

            FloatingActionButton fbar = (FloatingActionButton) findViewById(R.id.fab);

            fbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(getApplicationContext(), Create_Patient.class);
                    i.putExtra("Subdoctor", subdoctor);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });


//            if(position.equals("SubDoctor")){
//                fbar.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                fbar.setVisibility(View.INVISIBLE);
//            }

            listref = fd.getReference("sushruta").child("PatientActivity").child(subdoctor);
            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    patient_obj_list = new ArrayList<GetPatientDetails>();
                    userList = new ArrayList<>();
                    UIDList=new ArrayList<>();

                    for (DataSnapshot ds1 : ds.getChildren()) {

                        String UID =String.valueOf(ds1.getKey());

                        String Username = String.valueOf(ds1.getValue());

                        UIDList.add(UID);

                        DatabaseReference dataref = fd.getReference("sushruta").child("Details").child("Patient").child(Username);
                        dataref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {

                                String Username = String.valueOf(ds2.getKey());
                                if (userList.contains(Username)) {

                                    int pos = userList.indexOf(Username);
                                    Log.i(getLocalClassName(), String.valueOf(pos));
                                    patient_obj_list.remove(pos);
                                    userList.remove(pos);
                                }

                                userList.add(Username);

                                GetPatientDetails obj = new GetPatientDetails();
                                String Name = String.valueOf(ds2.child("Name").getValue());
                                String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                                String PatientID = String.valueOf(ds2.child("PatientId").getValue());
                                String PhoneNumber = String.valueOf(ds2.child("PhoneNo").getValue());
                                obj.setImageUrl(ImageUrl);
                                obj.setName(Name);
                                obj.setUserName(Username);
                                obj.setPatientID(PatientID);
                                obj.setPhoneNumber(PhoneNumber );

                                patient_obj_list.add(obj);

                                if(patient_obj_list.size()==0){
                                    no_results.setText("Not Available");
                                    Log.i(TAG,"Not Available");
                                }
                                else
                                {
                                    no_results.setText("");
                                    Log.i(TAG,"Not Available");
                                }

                                Log.i(TAG, "Value = " + Name + ImageUrl);
                                recyclerView3.setLayoutManager(mLayoutManager);
                                obj3 = new PatientRecyclerView(PatientList.this, patient_obj_list, subdoctor,UIDList);
                                recyclerView3.setAdapter(obj3);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.profile)
        {
            final Dialog dialog=new Dialog(PatientList.this);

            dialog.setContentView(R.layout.popup);

            final ImageView imageView = (ImageView) dialog.findViewById(R.id.view4);
            final TextView name = (TextView) dialog.findViewById(R.id.textView);
            final TextView docid = (TextView) dialog.findViewById(R.id.textView2);
            final TextView spec = (TextView) dialog.findViewById(R.id.textView3);
            final TextView licid =(TextView)dialog.findViewById(R.id.textView6);
            ImageView close = (ImageView) dialog.findViewById(R.id.button);

            SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

            String username=sharedPref.getString("Username","");
            Log.i("test",username);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sushruta").child("Details").child("Doctor").child(username);
            Log.i("test",username);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String Name = String.valueOf(dataSnapshot.child("Name").getValue());
                    String ImageUrl = String.valueOf(dataSnapshot.child("ImageUrl").getValue());
                    String Specialist = String.valueOf(dataSnapshot.child("Specialization").getValue());
                    String DocID=String.valueOf(dataSnapshot.child("DoctorID").getValue());
                    String LicID=String.valueOf(dataSnapshot.child("LicenseID").getValue());

                    Glide.with(getApplicationContext()).load(ImageUrl).into(imageView);
                    name.setText(Name);
                    docid.setText(DocID);
                    spec.setText(Specialist);
                    licid.setText(LicID);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                infodialog.getWindow().setColorMode(Color.TRANSPARENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

        }
        if(id==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "You are Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

}
