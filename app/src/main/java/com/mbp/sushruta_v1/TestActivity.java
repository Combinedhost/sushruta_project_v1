package com.mbp.sushruta_v1;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {

    private Button MessageButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test0);

        MessageButton = (Button)findViewById(R.id.message);

        MessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    dataRef = firebaseDatabase.getReference().child("");
                    String text = "This is a test";
                    String toNumber = "919994874831";

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }
}
