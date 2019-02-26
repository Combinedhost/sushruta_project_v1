package com.mbp.sushruta_v1;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {
ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new RegisterAdapter(getSupportFragmentManager()));

    }

    private class RegisterAdapter extends FragmentPagerAdapter {
        private RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if(i==0){
                return new Screen1();

            }

            if(i==1){
                return new Screen2();
            }


            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


    }
}
