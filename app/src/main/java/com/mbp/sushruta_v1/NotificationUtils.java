package com.mbp.sushruta_v1;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationUtils {

    public static void sendFCMPush(Context context, String topic, String title, String msg, String Activity, String channelId) {

        final String Legacy_SERVER_KEY = "AIzaSyD2ZLfhwQ7Mna9kwky99m3UGzcOYWlDxYs";
//        String msg = "Sir, Doctor "+sd+" has registered under you as sub doctor. Click the notification to take action";
//        String title = "New SubDoctor Registration";
        String token = "/topics/"+topic;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("android_channel_id", channelId);
            obj.put("to", token);
            obj.put("notification", objData);

            Log.e("PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://android.googleapis.com/gcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SUCCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Errors--", error + "");
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
