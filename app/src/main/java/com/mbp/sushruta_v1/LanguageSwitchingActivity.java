package com.mbp.sushruta_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class LanguageSwitchingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_switching);

        loadLocale();

        Button english = (Button)findViewById(R.id.lang_english);
        Button tamil = (Button)findViewById(R.id.lang_tamil);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
//                loadLocale();
                Toast.makeText(getApplicationContext(), "You have chosen English language", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PatientLoginActivity.class);
                startActivity(intent);
            }
        });

        tamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("ta");
//                loadLocale();
                Toast.makeText(getApplicationContext(), "You have chosen Tamil language", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), PatientLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("mypref", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        String language = pref.getString("language", "");
        setLocale(language);
    }
}
