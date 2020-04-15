package com.mbp.sushruta_v1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientRecyclerView extends RecyclerView.Adapter<PatientRecyclerView.recyclerholder3> {
    public Context ct;
    List <GetPatientDetails> obj_list;
    String subdoctor;
    List<String> UIDList;

    public PatientRecyclerView(Context c, List <GetPatientDetails>obj_list, String subdoctor,List<String> UIDList){
    this.obj_list = new ArrayList<GetPatientDetails>();
    this.obj_list=obj_list;
    ct=c;
    this.UIDList=UIDList;
    this.subdoctor=subdoctor;
}

@NonNull
@Override
public PatientRecyclerView.recyclerholder3 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater=LayoutInflater.from(ct);
    View view=inflater.inflate(R.layout.recycler_layout_doctor,viewGroup,false);
    return new recyclerholder3(view);
}

@Override
public void onBindViewHolder(@NonNull final PatientRecyclerView.recyclerholder3 viewHolder, int i) {

    GetPatientDetails obj=obj_list.get(i);
    viewHolder.t1.setText(obj.getName());
    Glide.with(ct).load(obj.getImageUrl()).into(viewHolder.im);

    viewHolder.rl.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            //Toast.makeText(ct,"You clicked "+userarray[viewHolder.getAdapterPosition()], Toast.LENGTH_LONG ).show();
            Intent i1 = new Intent(ct, PatientInformation.class);
            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            int pos=viewHolder.getAdapterPosition();
            GetPatientDetails objj=obj_list.get(pos);
            i1.putExtra("Patient",objj.getPatientID());
            ct.startActivity(i1);

        }
    });

    viewHolder.rl.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Context wrapper = new ContextThemeWrapper(ct, R.style.AppCompatAlertDialogStyle);

            PopupMenu rightpopup = new PopupMenu(wrapper, viewHolder.rl, Gravity.RIGHT);

            rightpopup.getMenuInflater().inflate(R.menu.right_click_patient,rightpopup.getMenu());

            SharedPreferences sharedPref = ct.getSharedPreferences("mypref", Context.MODE_PRIVATE);

            String position = sharedPref.getString("Position", "Doctor");
            Log.i("test", position);
            Menu popupMenu = rightpopup.getMenu();

            if (position.equals("SubDoctor")) {
                popupMenu.findItem(R.id.delete).setVisible(true);
            }
            else {
                popupMenu.findItem(R.id.delete).setVisible(false);
            }

            rightpopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int pos=viewHolder.getAdapterPosition();
                    final GetPatientDetails objj=obj_list.get(pos);
                    String user = objj.getPatientID();
                    String phno = objj.getPhoneNumber();
                    String name = objj.getName();
                    Log.i("Patient Information", phno);

                    String action=String.valueOf(item.getTitle());

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    Log.i("Test",action);
                    if(action.equals("Message")) {

                        try {
                            String text = "Hi " + name;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=91"+ phno +"&text="+ text));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ct.startActivity(intent);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    if(action.equals("Delete")){
                        DatabaseReference dataRef = firebaseDatabase.getReference().child("sushruta").child("Details").child("Patient").child(user);
                        dataRef.removeValue();

                        DatabaseReference regref= firebaseDatabase.getReference().child("sushruta").child("PatientActivity").child(subdoctor).child(UIDList.get(pos));

                        regref.removeValue();

                        final DatabaseReference docref= firebaseDatabase.getReference().child("sushruta").child("Details").child("Parameters");
                        docref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(objj.getPatientID())){
                                    docref.child(objj.getPatientID()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        final DatabaseReference paramref= firebaseDatabase.getReference().child("sushruta").child("Details").child("Documents");
                        paramref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(objj.getPatientID())){
                                    paramref.child(objj.getPatientID()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                    return true;
                }
            });


            rightpopup.show();
            return true;
        }
    });

}


@Override
public int getItemCount() {
    return obj_list.size();
}

public class recyclerholder3 extends RecyclerView.ViewHolder{
    TextView t1,t2,t3;
    ImageView im;
    public ConstraintLayout rl;
    public recyclerholder3(@NonNull View itemView) {
        super(itemView);
        t1=(TextView)itemView.findViewById(R.id.textView10);
        im=(ImageView)itemView.findViewById(R.id.imageButton);
        rl=(ConstraintLayout) itemView.findViewById(R.id.clayout);
    }
}
}
