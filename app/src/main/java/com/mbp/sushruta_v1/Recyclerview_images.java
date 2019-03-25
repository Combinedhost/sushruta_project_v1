package com.mbp.sushruta_v1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.List;

public class Recyclerview_images extends RecyclerView.Adapter<Recyclerview_images.Recyclerview_viewholder> {

    Context context;
    List<String> UrlList,NameList,UIDList,MimeList;
    String user;

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

        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=viewHolder.getAdapterPosition();
                String url=UrlList.get(pos);
//                Intent intent=new Intent(context,Webview.class);
//                intent.putExtra("Url",UrlList.get(pos));
//                context.startActivity(intent);






            }
        });
    }

    @Override
    public int getItemCount() {
        return UrlList.size();
    }

    public class Recyclerview_viewholder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;
        public Recyclerview_viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.imageView);
            textView=(TextView)itemView.findViewById(R.id.name);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.relativelayout);
        }
    }
}
