
package com.mbp.sushruta_v1;

        import android.app.Activity;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.constraint.ConstraintLayout;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.ContextThemeWrapper;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.PopupMenu;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.android.volley.AuthFailureError;
        import com.android.volley.DefaultRetryPolicy;
        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.RetryPolicy;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.Volley;
        import com.bumptech.glide.Glide;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;


public class Not_Approval_DoctorRecyclerView extends RecyclerView.Adapter<Not_Approval_DoctorRecyclerView.recyclerholder> {
    public Context ct;
    List<GetDoctorDetails> obj_list;

    Activity a;
    Dialog d;
    LayoutInflater inflater ;
    ImageView imageView,close,approve;
    TextView t1,t2,t3,t4,t5;

    PopupMenu rightpopup;
    Button b;
    GetDoctorDetails obj;
    public Not_Approval_DoctorRecyclerView(Context c, List <GetDoctorDetails>obj_list, Activity a){
        this.obj_list = new ArrayList<GetDoctorDetails>();
        this.obj_list=obj_list;
        ct=c;
        this.a=a;
    }


    @NonNull
    @Override
    public Not_Approval_DoctorRecyclerView.recyclerholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.recycler_layout_doctor,viewGroup,false);
        return new recyclerholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final Not_Approval_DoctorRecyclerView.recyclerholder viewHolder, int i) {

        obj=obj_list.get(i);
        viewHolder.t1.setText(obj.getName());

        Glide.with(ct).load(obj.getImageUrl()).into(viewHolder.im);



        viewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=viewHolder.getAdapterPosition();

                d=new Dialog(a);



                d.setContentView(R.layout.popup_notapproved);


                imageView=(ImageView)d.findViewById(R.id.view4);
                t1=(TextView)d.findViewById(R.id.textView);
                t2=(TextView)d.findViewById(R.id.textView2);
                t3=(TextView)d.findViewById(R.id.textView3);
                t4=(TextView)d.findViewById(R.id.textView5);
                t5=(TextView)d.findViewById(R.id.textView6);
                close=(ImageView)d.findViewById(R.id.image);
                approve=(ImageView)d.findViewById(R.id.tick);


                Glide.with(ct).load(obj.getImageUrl()).into(imageView);
                t1.setText(obj.getName());
                t2.setText(obj.getDoctorID());
                t3.setText(obj.getSpecialization());
                t4.setText(obj.getLicense());
                t5.setText(obj.getPhoneNo());

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase fd=FirebaseDatabase.getInstance();
                        DatabaseReference dataref = fd.getReference("sushruta").child("Details").child("Doctor").child(obj.getUsername());
                        dataref.child("Approval").setValue("Approved");

//                        DatabaseReference dataref1 = fd.getReference("sushruta").child("Details").child("Doctor").child(obj.getUsername());
//                        dataref.child("Approval").setValue("Approved");

                        sendFCMPush("Hello sir,we are happy to intimate that you are approved by the Head",obj.getUsername());
                        sendFCMPush("Hello sir, you have approved doctor "+obj.getUsername(),"Head");
                        d.dismiss();
                    }
                });

                d.show();
                d.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


            }
        });




    }

    @Override
    public int getItemCount() {
        return obj_list.size();
    }

    public class recyclerholder extends RecyclerView.ViewHolder{
        TextView t1,t2,t3;
        ImageView im;
        public ConstraintLayout rl;
        public recyclerholder(@NonNull View itemView) {
            super(itemView);
            t1=(TextView)itemView.findViewById(R.id.textView10);
            im=(ImageView)itemView.findViewById(R.id.imageButton);
            rl=(ConstraintLayout) itemView.findViewById(R.id.clayout);
        }
    }

    private void sendFCMPush(String msg,String topic) {

        final String Legacy_SERVER_KEY = "AIzaSyD2ZLfhwQ7Mna9kwky99m3UGzcOYWlDxYs";
        //msg = "You are approved by the doctor";
        String title = "Approval Notification";
        String token = "/topics/"+topic;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("android_channel_id","Approval Notification");



            obj.put("to", token);;
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
        RequestQueue requestQueue = Volley.newRequestQueue(ct);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
