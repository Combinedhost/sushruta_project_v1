package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

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
    UtilityClass utilityClass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_patients);

        ImageView back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(mTopToolbar);

        utilityClass = new UtilityClass(ParametersList.this);
        progressDialog = new ProgressDialog(ParametersList.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle(getString(R.string.loading_data));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        param_list = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.listview);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");
        databaseReference = firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user);

        arrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), android.R.layout.simple_list_item_1, param_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

//                             Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(18);
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);

        loadParameters();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_param_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                a = new Dialog(ParametersList.this, R.style.AppCompatAlertDialogStyle);
                a.setContentView(R.layout.getnamelayout);
                text = (EditText) a.findViewById(R.id.getname);

                b1 = (Button) a.findViewById(R.id.button);
                b1.setText(getString(R.string.create));
                heading = (TextView) a.findViewById(R.id.textView5);
                heading.setText(getString(R.string.name_of_parameter));

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = text.getText().toString();
                        param_list.add(string);
                        arrayAdapter.notifyDataSetChanged();
                        a.dismiss();
                    }
                });

                a.show();

            }
        });


        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String position = sharedPref.getString("Position", "SubDoctor");
        if (position.equals("SubDoctor")) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }

    }


    public void loadParameters() {
        if (!utilityClass.isNetworkAvailable()) {
            showMessage(getString(R.string.no_internet), true);
            return;
        }

        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                param_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String cm = dataSnapshot1.getKey();
                    param_list.add(cm);
                    arrayAdapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                utilityClass.showMessage(findViewById(android.R.id.content), getString(R.string.some_error_occurred));
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ParameterValues.class);
                intent.putExtra("param", String.valueOf(listView.getItemAtPosition(position)));
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_menu, menu);
        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        String position = sharedPref.getString("Position", "SubDoctor");
        if (!position.equals("patient")) {
            menu.findItem(R.id.message).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);

        if (id == R.id.logout) {
            String username = sharedPref.getString("Username", null);
            if (username != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(username);
            }

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), getString(R.string.log_out), Toast.LENGTH_LONG).show();
            String userType = sharedPref.getString("user_type", null);
            if (userType != null) {
                if (userType.equals("doctor")) {
                    Intent i = new Intent(ParametersList.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                if (userType.equals("patient")) {
                    Intent i = new Intent(ParametersList.this, PatientLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        }

        if (id == R.id.message) {
            String text = "Hi ";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String doctorPhoneNumber = sharedPref.getString("doctor_phone_number", null);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=91" + doctorPhoneNumber + "&text=" + text));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMessage(String data, Boolean refreshData) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), data, Snackbar.LENGTH_INDEFINITE);
        snackbar.setTextColor(ContextCompat.getColor(this,R.color.popup));

        snackbar.setAction(getString(R.string.refresh), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadParameters();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}
