package com.mbp.sushruta_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends Activity {
private static int SPLASH_TIME_OUT=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        final String userType = sharedPref.getString("user_type", "");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                assert userType != null;
                if (userType.equals("patient")) {
                    Intent i = new Intent(SplashScreen.this, PatientLoginActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}
