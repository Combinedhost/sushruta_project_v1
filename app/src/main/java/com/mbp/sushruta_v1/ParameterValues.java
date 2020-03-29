package com.mbp.sushruta_v1;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ParameterValues extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;
    List<String> date_list=new ArrayList<>();
    List<String> rdate_list=new ArrayList<>();
    String param,user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_values);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        Bundle bundle=getIntent().getExtras();
         param=bundle.getString("param");
       user=bundle.getString("user");
        Log.i(getLocalClassName(),param);

        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("sushruta").child("Details").child("Parameters").child(user).child(param);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                date_list.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                    String cm=dataSnapshot1.getKey();
                    date_list.add(cm);
                    saveArray(date_list);




                }


                DateFormat df1 = new SimpleDateFormat("EEE");

                String day = df1.format(Calendar.getInstance().getTime());


                DateFormat df2 = new SimpleDateFormat("d MMM yyyy");

                String date = df2.format(Calendar.getInstance().getTime());

                String val=date+"   ("+day+")";
                rdate_list=loadArray();
                if(!(rdate_list.contains(val))){
                    rdate_list.add(val);
                }
                Collections.reverse(rdate_list);
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_parameter_values, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.save) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(rdate_list.get(position),user,param);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return rdate_list.size();

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }




    public  boolean saveArray(List<String> sKey)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ParameterValues.this);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putInt("Status_size", sKey.size());

        for(int i=0;i<sKey.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, sKey.get(i));
        }

        return mEdit1.commit();
    }



    public List<String> loadArray()
    {
        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(ParameterValues.this);
        List<String> stringList=new ArrayList<>();
        stringList.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for(int i=0;i<size;i++)
        {
            stringList.add(mSharedPreference1.getString("Status_" + i, null));

        }

        return stringList;

    }
}
