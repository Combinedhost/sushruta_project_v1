package com.mbp.sushruta_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 2000;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPref;
        sharedPref = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        final String userType = sharedPref.getString("user_type", null);

        prefManager = new PrefManager(this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (prefManager.isFirstTimeLaunch()) {
                    Log.i("test", "fist time");
                    Intent i = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }

                if (!prefManager.isFirstTimeLaunch() && userType == null) {
                    Log.i("test", "not first time");
                    Intent i = new Intent(SplashScreen.this, LoginTypeSelectionActivity.class);
                    startActivity(i);
                    finish();
                }

                if (userType != null) {
                    if (userType.equals("doctor")) {
                        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    if (userType.equals("patient")) {
                        Intent i = new Intent(SplashScreen.this, PatientLoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }


}
