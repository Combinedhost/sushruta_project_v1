package com.mbp.sushruta_v1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Test extends AppCompatActivity {
Button button;
ProgressDialog d;
ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        button=(Button)findViewById(R.id.button3);
//        FirebaseMessaging.getInstance().subscribeToTopic("Sivaraman");
//        FirebaseMessaging.getInstance().subscribeToTopic("SriDevi");


        FirebaseMessaging.getInstance().unsubscribeFromTopic("Sivaraman");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("SriDevi");


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//To do//
                            return;
                        }

// Get the Instance ID token//
                        String token = task.getResult().getToken();

                        Log.d("dcd", token);

                    }
                });


        String topic = "highScores";



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



        //sendFCMPush();
    }


    private void sendFCMPush() {

        final String Legacy_SERVER_KEY = "AIzaSyD2ZLfhwQ7Mna9kwky99m3UGzcOYWlDxYs";
        String msg = "this is test message,.,,.,.";
        String title = "my title";
        String token = "/topics/Sivaraman";

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();
            objData.put("body", msg);
            objData.put("title", title);

            obj.put("to", token);
            obj.put("notification", objData);

            Log.e("PASS", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://android.googleapis.com/gcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Errors", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
