package com.mbp.sushruta_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DoctorRecyclerView extends RecyclerView.Adapter<DoctorRecyclerView.recyclerholder> {
    public Context ct;
    List <GetDoctorDetails> obj_list;
    Dialog infodialog;
    PopupMenu rightpopup;
    LayoutInflater inflater ;
    Button message,disapprove;
    TextView t1,t2,t3,t4;
    ImageView close,imageView;

    private static final String TAG = "MainActivity";
    Activity a;
    public DoctorRecyclerView(Context c,List <GetDoctorDetails>obj_list,Activity a){
        this.obj_list = new ArrayList<GetDoctorDetails>();
        this.obj_list=obj_list;
        ct=c;
        this.a=a;
    }


    @NonNull
    @Override
    public DoctorRecyclerView.recyclerholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(ct);
        View view=inflater.inflate(R.layout.recycler_layout_doctor,viewGroup,false);
        return new recyclerholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final DoctorRecyclerView.recyclerholder viewHolder, int i) {

        final GetDoctorDetails obj=obj_list.get(i);
        viewHolder.t1.setText(obj.getName());
        Glide.with(ct).load(obj.getImageUrl()).into(viewHolder.im);

        viewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(ct,"You clicked "+userarray[viewHolder.getAdapterPosition()], Toast.LENGTH_LONG ).show();
                Intent i1 = new Intent(a, SubDoctorListActivity.class);
                int pos=viewHolder.getAdapterPosition();
                GetDoctorDetails objj=obj_list.get(pos);
                i1.putExtra("user",objj.getUsername());
                //Log.i(TAG, objj.getUsername());
                a.startActivity(i1);

            }
        });


        viewHolder.rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Context wrapper = new ContextThemeWrapper(ct, R.style.AppCompatAlertDialogStyle);

                rightpopup=new PopupMenu(wrapper,viewHolder.rl,Gravity.RIGHT);


//                rightpopup=new PopupMenu(ct,viewHolder.rl,Gravity.RIGHT);

                rightpopup.getMenuInflater().inflate(R.menu.right_click_menu,rightpopup.getMenu());

                rightpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int pos=viewHolder.getAdapterPosition();
                        final GetDoctorDetails objj=obj_list.get(pos);
                        String user=objj.getUsername();

                        String action=String.valueOf(item.getTitle());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        Log.i("Test",action);
                        if(action.equals("Message")){


                                    try{
                                        String text = "Message from sushruta app";
                                        String toNumber = objj.getPhoneNo();

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ct.startActivity(intent);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                        }

                        if(action.equals("Disapprove")){
                            DatabaseReference dataRef = firebaseDatabase.getReference().child("sushruta").child("Details").child("Doctor").child(user).child("Approval");
                            dataRef.setValue("Not Approved");
                        }
                        return true;
                    }
                });


                rightpopup.show();
                return true;
            }
        });




        viewHolder.im.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos=viewHolder.getAdapterPosition();
                GetDoctorDetails objj=obj_list.get(pos);
                infodialog=new Dialog(a);
                infodialog.setContentView(R.layout.popup);


                imageView=(ImageView)infodialog.findViewById(R.id.view4);
                t1=(TextView)infodialog.findViewById(R.id.textView);
                t2=(TextView)infodialog.findViewById(R.id.textView2);
                t3=(TextView)infodialog.findViewById(R.id.textView3);
                close=(ImageView)infodialog.findViewById(R.id.button);

                Glide.with(ct).load(objj.getImageUrl()).into(imageView);
                t1.setText(objj.getName());
                t2.setText(objj.getDoctorID());
                t3.setText(objj.getSpecialization());

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infodialog.dismiss();
                    }
                });

                infodialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                infodialog.getWindow().setColorMode(Color.TRANSPARENT);
                infodialog.getWindow().setGravity(Gravity.CENTER);
                infodialog.show();



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
        public RelativeLayout r2;
        public recyclerholder(@NonNull View itemView) {
            super(itemView);
            t1=(TextView)itemView.findViewById(R.id.textView10);
            im=(ImageView)itemView.findViewById(R.id.imageButton);
            rl=(ConstraintLayout)itemView.findViewById(R.id.clayout);

        }
    }
}
