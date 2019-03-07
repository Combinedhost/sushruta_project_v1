package com.mbp.sushruta_v1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Test extends AppCompatActivity {
Button button;
ProgressDialog d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        button=(Button)findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast = Toast.makeText(getApplicationContext(),"ssknskn", Toast.LENGTH_LONG);
                View view = toast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter
                view.getBackground().setColorFilter(getResources().getColor(R.color.popup), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                TextView text = view.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.textColor));

                toast.show();





                d=new ProgressDialog(Test.this, R.style.AppCompatAlertDialogStyle);
                d.setTitle("Downloading");
                d.setButton(ProgressDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });
                d.show();
            }
        });

    }
}
