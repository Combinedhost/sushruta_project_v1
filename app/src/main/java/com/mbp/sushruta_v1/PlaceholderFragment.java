package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Map;


public  class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    List<String> time,value;
    TableLayout stk;
    Dialog a;
    EditText text;
    Button b1;
    TextView heading;
    String user,date,param;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(String date,String user,String param) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString("Dates", date);
        args.putString("User", user);
        args.putString("Param", param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parameter_values, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.date_text);
        textView.setText(getArguments().getString("Dates"));
          stk = (TableLayout) rootView.findViewById(R.id.tablelayout);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        date=getArguments().getString("Dates");
        user=getArguments().getString("User");
        param=getArguments().getString("Param");

        final TextView no_results=(TextView)rootView.findViewById(R.id.no_results);


        ImageView im=(ImageView)rootView.findViewById(R.id.imageView4);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().finish();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.add_value);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                a=new Dialog(getActivity(),R.style.AppCompatAlertDialogStyle);
                a.setContentView(R.layout.getnamelayout);
                text=(EditText)a.findViewById(R.id.getname);

                b1=(Button)a.findViewById(R.id.button);
                b1.setText("Update");

                heading=(TextView)a.findViewById(R.id.textView5) ;
                heading.setText("Enter the value");




                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateFormat df = new SimpleDateFormat("h:mm:ss a");
                        String time = df.format(Calendar.getInstance().getTime());
                        String string = text.getText().toString();
                        FirebaseDatabase fd = FirebaseDatabase.getInstance();
                        DatabaseReference addv=fd.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(date);
                        String key=addv.push().getKey();
                        Map map=new HashMap();
                        map.put("time",time);
                        map.put("value",string);
                        addv.child(key).setValue(map);
                        a.dismiss();
                    }
                });

                a.show();

            }
        });

        ImageButton chart=(ImageButton)rootView.findViewById(R.id.chart);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),ChartActivity.class);
                intent.putExtra("date",date);
                intent.putExtra("user",user);
                intent.putExtra("param",param);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference=firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user).child(param).child(date);


        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stk.removeAllViews();
                TableRow tbrow0 = new TableRow(getActivity());
                tbrow0.setPadding(50, 10, 50, 10);
                tbrow0.setGravity(Gravity.CENTER);
                TextView tv0 = new TextView(getActivity());
                tv0.setText("           Time             ");
                tv0.setAllCaps(true);
                tv0.setTextColor(Color.BLACK);
                tv0.setTextSize(18);
                tv0.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                tbrow0.addView(tv0);


                TextView tv1 = new TextView(getActivity());
                tv1.setText("           Value        ");
                tv1.setTextColor(Color.BLACK);
                tv1.setAllCaps(true);
                tv1.setTextSize(18);
                tv1.setTypeface(tv0.getTypeface(), Typeface.BOLD);
                tbrow0.addView(tv1);
                stk.addView(tbrow0);


                value = new ArrayList<>();
                time = new ArrayList<>();
                int i=0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    i=i+1;
                    String time = String.valueOf(dataSnapshot1.child("time").getValue());
                    String pred = String.valueOf(dataSnapshot1.child("value").getValue());
                    String com = time + pred;


                    TableRow tbrow = new TableRow(getActivity());
                    tbrow.setGravity(Gravity.CENTER);
                    tbrow.setPadding(50, 10, 50, 10);
                    TextView t1v = new TextView(getActivity());
                    t1v.setText(time);
                    t1v.setTextColor(Color.BLACK);
                    t1v.setGravity(Gravity.CENTER);
                    t1v.setTextSize(18);
                    tbrow.addView(t1v);
                    TextView t2v = new TextView(getActivity());
                    t2v.setText(pred);
                    t2v.setTextColor(Color.BLACK);
                    t2v.setGravity(Gravity.CENTER);
                    t2v.setTextSize(18);
                    tbrow.setDividerPadding(20);
                    tbrow.addView(t2v);


                    stk.addView(tbrow);

                }
                if(i>0){
                    no_results.setVisibility(View.INVISIBLE);
                }
                else
                {
                    no_results.setVisibility(View.VISIBLE);
                }
            }



        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
                        return rootView;
    }
}