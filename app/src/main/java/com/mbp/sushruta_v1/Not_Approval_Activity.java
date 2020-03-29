package com.mbp.sushruta_v1;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
public class Not_Approval_Activity extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference listref,dataref;

    List<GetDoctorDetails> sub_doctor_obj_list;

    String doctor;

    TextView no_results;

    RecyclerView recyclerView2;
    Not_Approval_SubDoctor_Recyclerview obj2;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "SubDoctor_NotApproved";
    List< String> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_doctor_list);

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        mLayoutManager = new LinearLayoutManager(this);
        fd = FirebaseDatabase.getInstance();

        no_results=(TextView) findViewById(R.id.notavailable);

        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mTopToolbar);
        Bundle b1=getIntent().getExtras();
        doctor=b1.getString("user");

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.doctorspinner);
        spinner.setItems("List of Not Approved SubDoctors","List of Approved SubDoctors");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {


                if(item.equals("List of Approved SubDoctors")){
                    finish();
                }
//                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        try{





            listref = fd.getReference("sushruta").child("SubDoctorActivity").child(doctor);
            listref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {

                    //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();
                    sub_doctor_obj_list = new ArrayList<GetDoctorDetails>();
                    userList=new ArrayList<>();
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        GetDoctorDetails obj = new GetDoctorDetails();
                        String Username = String.valueOf(ds1.getValue());

                        dataref=fd.getReference("sushruta").child("Details").child("Doctor").child(Username);

                        dataref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ds2) {

                                String Username = String.valueOf(ds2.getKey());

                                GetDoctorDetails obj = new GetDoctorDetails();
                                String Name = String.valueOf(ds2.child("Name").getValue());

                                String Age = String.valueOf(ds2.child("Age").getValue());
                                String Designation = String.valueOf(ds2.child("Designation").getValue());
                                String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                                String Qualification = String.valueOf(ds2.child("Qualification").getValue());
                                String Gender = String.valueOf(ds2.child("Gender").getValue());
                                String Specialization=String.valueOf(ds2.child("Specialization").getValue());
                                String DoctorId=String.valueOf(ds2.child("DoctorID").getValue());
                                String PhoneNo=String.valueOf(ds2.child("PhoneNo").getValue());
                                String License=String.valueOf(ds2.child("LicenseID").getValue());

                                obj.setAge(Age);
                                obj.setUsername(Username);
                                obj.setDesignation(Designation);
                                obj.setGender(Gender);
                                obj.setImageUrl(ImageUrl);
                                obj.setName(Name);
                                obj.setQualification(Qualification);
                                obj.setDoctorID(DoctorId);
                                obj.setSpecialization(Specialization);
                                obj.setPhoneNo(PhoneNo);
                                obj.setLicense(License);



                                String approval=String.valueOf(ds2.child("Approval").getValue());
                                if(approval.equals("Not Approved")){
                                    sub_doctor_obj_list.add(obj);
                                    if(userList.contains(Username)){

                                        int pos=userList.indexOf(Username);
                                        Log.i(getLocalClassName(),String.valueOf(pos));
                                        sub_doctor_obj_list.remove(pos);
                                        userList.remove(pos);
                                    }
                                    userList.add(Username);
                                }
                                if(approval.equals("Approved")){

                                    if(userList.contains(Username)){

                                        int pos=userList.indexOf(Username);
                                        Log.i(getLocalClassName(),String.valueOf(pos));
                                        sub_doctor_obj_list.remove(pos);
                                        userList.remove(pos);
                                    }

                                }

                                Log.i(TAG, "Value = " + Name + "  " + Age + " " + Gender + " " + Designation + " " + ImageUrl + " " + Qualification);

                                recyclerView2.setLayoutManager(mLayoutManager);
                                obj2 = new Not_Approval_SubDoctor_Recyclerview(Not_Approval_Activity.this, sub_doctor_obj_list,Not_Approval_Activity.this,doctor);
                                recyclerView2.setAdapter(obj2);

                                if(userList.size()==0){
                                    no_results.setText("Not Available");
                                }
                                else
                                {
                                    no_results.setText("");
                                }

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




        }catch (Exception e){
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
                final Dialog dialog=new Dialog(Not_Approval_Activity.this);

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
                SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

                String username=sharedPref.getString("Username","");
                if(username!=null){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(username);
                }

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "You are Logged Out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
            return super.onOptionsItemSelected(item);
        }
}

