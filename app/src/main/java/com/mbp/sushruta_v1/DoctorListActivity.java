package com.mbp.sushruta_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {

    FirebaseDatabase fd;
    DatabaseReference listref,dataref;
    String TName = "";
    List<GetDoctorDetails> doctor_obj_list;
    List<String> userList;

    RecyclerView recyclerView;
    DoctorRecyclerView obj1;

    TextView no_results;

    Toolbar mTopToolbar;

    LinearLayoutManager mLayoutManager;
    private static final String TAG = "DoctorListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        no_results=(TextView) findViewById(R.id.notavailable);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mTopToolbar);



        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.doctorspinner);
        spinner.setItems("List of Approved Doctors","List of Not Approved Doctors");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {


                if(item.equals("List of Not Approved Doctors")){
                                Intent intent=new Intent(DoctorListActivity.this,Doctor_Not_Approval_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity(intent);
                }
//                Snackbar.make(view, "Displaying " + item, Snackbar.LENGTH_LONG).show();
            }
        });


        fd = FirebaseDatabase.getInstance();
        listref = fd.getReference("sushruta").child("DoctorActivity").child("Head");
        listref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                doctor_obj_list = new ArrayList<GetDoctorDetails>();
                userList=new ArrayList<>();
                //doctorDetailsMap=new HashMap<String, GetDoctorDetails>();

                for (DataSnapshot ds1 : ds.getChildren()) {

                    String Username = String.valueOf(ds1.getValue());



                    dataref=fd.getReference("sushruta").child("Details").child("Doctor").child(Username);

                    dataref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds2) {

                            String Username = String.valueOf(ds2.getKey());
                            Log.i(TAG, Username);

                            GetDoctorDetails obj = new GetDoctorDetails();
                            String Name = String.valueOf(ds2.child("Name").getValue());
                            TName = Name;
                            String Age = String.valueOf(ds2.child("Age").getValue());
                            String Designation = String.valueOf(ds2.child("Designation").getValue());
                            String ImageUrl = String.valueOf(ds2.child("ImageUrl").getValue());
                            String Qualification = String.valueOf(ds2.child("Qualification").getValue());
                            String Gender = String.valueOf(ds2.child("Gender").getValue());
                            String Specialization=String.valueOf(ds2.child("Specialization").getValue());
                            String DoctorId=String.valueOf(ds2.child("DoctorID").getValue());
                            String PhoneNo =String.valueOf(ds2.child("PhoneNo").getValue());
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






                            Log.i(TAG, "Value = " + Name + "  " + Age + " " + Gender + " " + Designation + " " + ImageUrl + " " + Qualification);



                            String approval=String.valueOf(ds2.child("Approval").getValue());

                            if(approval.equals("Approved")){
                                doctor_obj_list.add(obj);
                                if(userList.contains(Username)){

                                    int pos=userList.indexOf(Username);
                                    Log.i(getLocalClassName(),"Updated  " +userList.get(pos));
                                    doctor_obj_list.remove(pos);
                                    userList.remove(pos);
                                }
                                userList.add(Username);
                            }
                            if(approval.equals("Not Approved")){

                                if(userList.contains(Username)){

                                    int pos=userList.indexOf(Username);
                                    Log.i(getLocalClassName(),"Updated  " +userList.get(pos));
                                    doctor_obj_list.remove(pos);
                                    userList.remove(pos);
                                }

                            }

                            if(userList.size()==0){
                                no_results.setText("Not Available");
                            }
                            else
                            {
                                no_results.setText("");
                            }



                            recyclerView.setLayoutManager(mLayoutManager);

                            obj1 = new DoctorRecyclerView(getApplicationContext(), doctor_obj_list,DoctorListActivity.this);
                            recyclerView.setAdapter(obj1);

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
            final Dialog dialog=new Dialog(DoctorListActivity.this);

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