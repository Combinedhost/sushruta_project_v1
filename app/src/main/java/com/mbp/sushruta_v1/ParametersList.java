package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ParametersList extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> param_list;
    Dialog a;
    ListView listView;
    ArrayAdapter arrayAdapter;
    EditText text;
    Button b1;
    TextView heading;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_patients);

        ImageView back=(ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(mTopToolbar);

        firebaseDatabase=FirebaseDatabase.getInstance();
        param_list=new ArrayList<String>();

        listView=(ListView)findViewById(R.id.listview);

        Bundle bundle=getIntent().getExtras();
        user=bundle.getString("user");
        databaseReference=firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user);

        arrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_list_item_1, param_list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

//                             Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(18);
//                            tv.setGravity(Gravity.CENTER);
//                            if(position==0){
//                                tv.setTypeface(null, Typeface.BOLD);
//                            }
//                             Generate ListView Item using TextView
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);


        DateFormat df2 = new SimpleDateFormat("d MMM yyyy");

        String date = df2.format(Calendar.getInstance().getTime());

        Log.i("Test",date.toString());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                param_list.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                    String cm=dataSnapshot1.getKey();
                    param_list.add(cm);
                    Log.i(getLocalClassName(),cm);
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),ParameterValues.class);
                intent.putExtra("param",String.valueOf(listView.getItemAtPosition(position)));
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_param_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                a=new Dialog(ParametersList.this,R.style.AppCompatAlertDialogStyle);
                a.setContentView(R.layout.getnamelayout);
                text=(EditText)a.findViewById(R.id.getname);

                b1=(Button)a.findViewById(R.id.button);
                b1.setText("Create");
                heading=(TextView)a.findViewById(R.id.textView5) ;
                heading.setText("Name of the Parameter");

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String string=text.getText().toString();
                        param_list.add(string);
                        arrayAdapter.notifyDataSetChanged();
                        a.dismiss();
                    }
                });

                a.show();

            }
        });


        SharedPreferences sharedPref = this.getSharedPreferences("mypref",Context.MODE_PRIVATE);
        String position = sharedPref.getString("Position","SubDoctor");
        if(position.equals("SubDoctor")){
            fab.setVisibility(View.VISIBLE);
        }
        else
        {
            fab.setVisibility(View.INVISIBLE);
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
            final Dialog dialog=new Dialog(ParametersList.this);

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
