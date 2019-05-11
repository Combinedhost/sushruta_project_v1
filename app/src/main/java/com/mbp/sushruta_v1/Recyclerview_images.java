package com.mbp.sushruta_v1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class Recyclerview_images extends RecyclerView.Adapter<Recyclerview_images.Recyclerview_viewholder> {

    Context context;
    List<String> UrlList,NameList,UIDList,MimeList;
    String user;
    Uri uri;
    int exist;
    public  Recyclerview_images(Context context, List<String> UrlList,List<String> nameList,List<String> UIDList,List<String> MimeList,String user)
    {
        this.context=context;
        this.UrlList=UrlList;
        this.NameList=nameList;
        this.UIDList=UIDList;
        this.user=user;
        this.MimeList=MimeList;
    }
    @NonNull
    @Override
    public Recyclerview_viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_layout_docments,null,false);
        return new Recyclerview_viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Recyclerview_viewholder viewHolder, int i) {
        String url=UrlList.get(i);
        String name=NameList.get(i);

        viewHolder.textView.setText(name);
        String mime=MimeList.get(i);

        if(mime.equals("image/png") || mime.equals("image/jpeg") || mime.equals("image/x-mms-bmp")){
            Glide.with(context).load(url).into(viewHolder.imageView);
        }




        if(mime.equals("application/msword")){  //MSWORD
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fword.png?alt=media&token=64e8d3f8-024d-41d1-9fa5-c4af05d3644c").into(viewHolder.imageView);
        }
        if(mime.equals("application/pdf")){  //PDF
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fpdf.png?alt=media&token=409c534f-21f0-4f83-96d9-6cf938affb50").into(viewHolder.imageView);
        }

        if(mime.equals("application/vnd.ms-excel")){  //EXCEL
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fexcel.png?alt=media&token=5e0eec97-2fea-411c-aa47-5c20f670d65f").into(viewHolder.imageView);
        }

        if(mime.equals("application/mspowerpoint")){  //MS
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fppt.png?alt=media&token=543353e2-e991-45e9-a29d-7e9b99b75aa9").into(viewHolder.imageView);
        }

        if(mime.equals("text/plain") || mime.equals("text/html")){  //TEXT
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Ftxt.png?alt=media&token=9b21d083-add8-4fa7-b7eb-f4aa6a0559b4").into(viewHolder.imageView);
        }

        if(mime.equals("application/zip")){  //ZIP
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fzip.png?alt=media&token=e4b1cb21-8e7f-45ed-96c2-f4ffa4f87b81").into(viewHolder.imageView);
        }

        if(mime.equals("video/mpeg") || mime.equals("video/mp4") || mime.equals("video/3gpp")  || mime.equals("video/3gpp2") || mime.equals("video/webm") || mime.equals("video/avi")){  //ZIP
            Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/sushruta-aafa9.appspot.com/o/icons%2Fvideo.png?alt=media&token=ad3d09f9-1e24-4307-bc14-b9d97d8e7b51").into(viewHolder.imageView);
        }

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Sushruta");
        File file=new File(rootPath,name);
        if(file.exists()){

            viewHolder.status.setImageResource(R.drawable.tick);
        }
        else{
            viewHolder.status.setImageResource(R.drawable.download);
            }


        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Glide.with(context).asGif().load(R.drawable.loading1).into(viewHolder.status);
                final int pos=viewHolder.getAdapterPosition();
                final String name=NameList.get(pos);
                final String mime=MimeList.get(pos);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://sushruta-aafa9.appspot.com");
                StorageReference storageReference=storageRef.child("images").child(name);

                File rootPath = new File(Environment.getExternalStorageDirectory(), "Sushruta");
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }
                File downfile = new File(rootPath, name);
                if(downfile.exists()){
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "Sushruta/"+name);


                    viewHolder.status.setImageResource(R.drawable.tick);
                    Uri uri;
                    if (Build.VERSION.SDK_INT < 24)
                    {
                        uri = Uri.fromFile(file);
                    } else
                        {
                        uri = Uri.parse(file.getPath()); // My work-around for new SDKs, causes ActivityNotFoundException in API 10.
                    }
                    try{
                        Log.i("TEST",uri.toString());
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setDataAndType(uri, mime);

                        context.startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else {

                    storageReference.getFile(downfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();
                            viewHolder.status.setImageResource(R.drawable.tick);

                            File file = new File(Environment.getExternalStorageDirectory(),
                                    "Sushruta/"+name);


                            viewHolder.status.setImageResource(R.drawable.tick);
                            Uri uri;
                            if (Build.VERSION.SDK_INT < 24)
                            {
                                uri = Uri.fromFile(file);
                            } else
                            {
                                uri = Uri.parse(file.getPath()); // My work-around for new SDKs, causes ActivityNotFoundException in API 10.
                            }
                            try{
                                Log.i("TEST",uri.toString());
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setDataAndType(uri, mime);

                                context.startActivity(intent);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("TEST", exception.toString());
                        }
                    });

                }






            }
        });

        viewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int pos=viewHolder.getAdapterPosition();
                String name=NameList.get(pos);
                final String mime=MimeList.get(pos);

                Log.i("Test",mime);
                File file = new File(Environment.getExternalStorageDirectory(),
                        "Sushruta/"+name);

                exist=0;
                if(file.exists()) {
                    Log.i("Test","Exists");
                    exist=1;
                }
                else
                {
                    Log.i("TAG","not exists");
                    exist=0;
                }



                if (Build.VERSION.SDK_INT < 24)
                {
                    uri = Uri.fromFile(file);
                } else
                {
//                    uri = Uri.parse(file.getPath());
                   uri= FileProvider.getUriForFile(context, "com.mbp.sushruta_v1.fileprovider", file);
                }
                Log.i("Test",uri.toString());


//                PopupMenu rightclick=new PopupMenu(context,viewHolder.relativeLayout,Gravity.END,0, R.style.AppCompatAlertDialogStyle);
                PopupMenu rightclick=new PopupMenu(context,viewHolder.relativeLayout,Gravity.END);
                rightclick.getMenuInflater().inflate(R.menu.right_click_documents,rightclick.getMenu());
                rightclick.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                        public boolean onMenuItemClick(MenuItem item) {



                        String action=item.getTitle().toString();

                        switch(action){
                            case "Delete":
                                FirebaseDatabase getfirebaseDatabase=FirebaseDatabase.getInstance();
                                DatabaseReference getdatabaseReference = getfirebaseDatabase.getReference("sushruta").child("Details").child("Documents").child(user);
                                getdatabaseReference.child(UIDList.get(pos)).removeValue();
                                break;
                            case "Share":

                                if(exist==1){
                                    Intent share_intent = new Intent();
                                    share_intent.setAction(Intent.ACTION_SEND);
                                    share_intent.setType(mime);
                                    share_intent.putExtra(Intent.EXTRA_STREAM,uri);
                                    share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    share_intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    share_intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    share_intent.putExtra(Intent.EXTRA_SUBJECT,
                                            "Share");
                                    share_intent.putExtra(Intent.EXTRA_TEXT,
                                            "This document is shared via Sushruta App");
                                    try
                                    {
                                        context.startActivity(Intent.createChooser(share_intent,
                                                "ShareThroughChooser Test"));
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                               else
                                {
                                    Toast.makeText(context,"Download the file to share",Toast.LENGTH_SHORT).show();
                                }



                                break;

                        }





                        return true;
                    }
                });

                rightclick.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return UrlList.size();
    }

    public class Recyclerview_viewholder extends RecyclerView.ViewHolder
    {
        ImageView imageView,status;
        TextView textView;
        RelativeLayout relativeLayout;
        public Recyclerview_viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.imageView);
            textView=(TextView)itemView.findViewById(R.id.name);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.relativelayout);
            status=(ImageView)itemView.findViewById(R.id.tick);
        }
    }
}
