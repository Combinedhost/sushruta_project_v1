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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;






public class Not_Approval_SubDoctor_Recyclerview extends RecyclerView.Adapter<Not_Approval_SubDoctor_Recyclerview.subrecyclerholder> {
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
    public Not_Approval_SubDoctor_Recyclerview(Context c, List <GetDoctorDetails>obj_list, Activity a){
        this.obj_list = new ArrayList<GetDoctorDetails>();
        this.obj_list=obj_list;
        ct=c;
        this.a=a;
    }


    @NonNull
    @Override
    public Not_Approval_SubDoctor_Recyclerview.subrecyclerholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.recycler_layout_doctor,viewGroup,false);
        return new subrecyclerholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final Not_Approval_SubDoctor_Recyclerview.subrecyclerholder viewHolder, int i) {

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

                        DatabaseReference dataref1 = fd.getReference("sushruta").child("Details").child("Doctor").child(obj.getUsername());
                        dataref.child("Approval").setValue("Approved");

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

    public class subrecyclerholder extends RecyclerView.ViewHolder{
        TextView t1,t2,t3;
        ImageView im;
        public ConstraintLayout rl;
        public subrecyclerholder(@NonNull View itemView) {
            super(itemView);
            t1=(TextView)itemView.findViewById(R.id.textView10);
            im=(ImageView)itemView.findViewById(R.id.imageButton);
            rl=(ConstraintLayout) itemView.findViewById(R.id.clayout);
        }
    }
}
