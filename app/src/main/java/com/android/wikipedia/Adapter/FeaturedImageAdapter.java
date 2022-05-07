package com.android.wikipedia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wikipedia.Activities.preview;
import com.android.wikipedia.Database.*;
import com.android.wikipedia.Holder.*;
import com.android.wikipedia.R;
import com.android.wikipedia.*;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class FeaturedImageAdapter extends RecyclerView.Adapter<FeaturedImageAdapter.viewHolder> {
    ArrayList<Holder> arrayList;
    Context context;
    DatabaseManager manager;
    public FeaturedImageAdapter(Context context,ArrayList<Holder> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.image_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String title=arrayList.get(position).getTitle();
        manager=DatabaseManager.getINSTANCE(context);
        if (UpdateTheme.check_network_state(context)){
            String url=arrayList.get(position).getUrl();
            holder.mTitle.setText(title);
            //loading an image from a server we use glide third party library.
            try{
                Glide.with(context).load(url).centerCrop().placeholder(R.drawable.wikipedia1).override(680,680).into(holder.imageView);
            }catch (Exception e){
                Log.e("Load error =",e.getLocalizedMessage());
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Download")
                            .setCancelable(false)
                            .setMessage("Do you want to download this file?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String title=arrayList.get(position).getTitle();
                                    String path= downloadFile.download(title,context,arrayList.get(position).getUrl(),"Images");
                                    Entities entities=new Entities(title,path);
                                    manager.dao().insertData(entities);
                                    arrayList.get(position).setPath(path);
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return false;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, preview.class);
                    intent.putExtra("title",arrayList.get(position).getTitle());
                    intent.putExtra("url",arrayList.get(position).getDescriptionUrl());
                    intent.putExtra("flag",false);
                    context.startActivity(intent);
                }
            });
        }else{
            String uri=arrayList.get(position).getPath();
            holder.mTitle.setText(title);
            holder.imageView.setImageURI(Uri.parse(uri));
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void update(ArrayList<Holder> temp) {
        arrayList=temp;
        notifyDataSetChanged();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView mTitle;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.featuredImage);
            mTitle=itemView.findViewById(R.id.featuredImageTitle);
        }

    }

}