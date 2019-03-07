package com.mbp.sushruta_v1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PatientRecyclerView extends RecyclerView.Adapter<PatientRecyclerView.recyclerholder3> {
    public Context ct;
    List <GetPatientDetails> obj_list;
    String subdoctor;


    public PatientRecyclerView(Context c, List <GetPatientDetails>obj_list, String subdoctor){
    this.obj_list = new ArrayList<GetPatientDetails>();
    this.obj_list=obj_list;
    ct=c;
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
            Intent i1 = new Intent(ct, Patient_Information.class);
            int pos=viewHolder.getAdapterPosition();
            GetPatientDetails objj=obj_list.get(pos);
            i1.putExtra("Subdoctor",subdoctor);
            i1.putExtra("Patient",objj.getUserName());
            ct.startActivity(i1);

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
