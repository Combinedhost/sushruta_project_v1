package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParametersList extends Activity {
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


        firebaseDatabase=FirebaseDatabase.getInstance();
        param_list=new ArrayList<String>();

        listView=(ListView)findViewById(R.id.listview);

        Bundle bundle=getIntent().getExtras();
        user=bundle.getString("user");
        databaseReference=firebaseDatabase.getReference("sushruta").child("Details").child("Patient").child(user).child("Parameters");

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
    }

}
