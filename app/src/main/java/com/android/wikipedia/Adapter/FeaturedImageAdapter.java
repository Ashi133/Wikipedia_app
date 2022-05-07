package com.android.wikipedia.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.bumptech.glide.Glide;

import java.lang.annotation.Target;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class FeaturedImageAdapter extends RecyclerView.Adapter<FeaturedImageAdapter.viewHolder> {
    ArrayList<Holder> arrayList;
    Context context;

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
        String url=arrayList.get(position).getUrl();
        holder.mTitle.setText(title);
        //loading an image from a server we use glide third party library.
        try{
            Glide.with(context).load(url).centerCrop().placeholder(R.drawable.wikipedia).override(680,680).into(holder.imageView);
        }catch (Exception e){
            Log.e("Load error =",e.getLocalizedMessage());
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