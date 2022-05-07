package com.android.wikipedia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wikipedia.Activities.preview;
import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.android.wikipedia.downloadFile;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.Viewholder>{
    ArrayList<Holder> arrayList;
    Context context;
    public ArticlesAdapter(Context context,ArrayList<Holder> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.article_item,parent,false ));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String title=arrayList.get(position).getTitle();
        String content=arrayList.get(position).getContent();
        holder.mTitle.setText(title);
        holder.mContent.setText(content);
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,preview.class);
                intent.putExtra("title",arrayList.get(position).getTitle());
                intent.putExtra("content",arrayList.get(position).getContent());
                intent.putExtra("flag",true);
                context.startActivity(intent);
            }
        });
        holder.mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Download")
                        .setCancelable(false)
                        .setMessage("Do you want to download this file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String path=downloadFile.download(arrayList.get(position).getTitle(),context,arrayList.get(position).getUrl(),"Images");
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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void update(ArrayList<Holder> temp) {
        arrayList=temp;
        notifyDataSetChanged();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView mTitle,mContent;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            mTitle=itemView.findViewById(R.id.articleTitle);
            mContent=itemView.findViewById(R.id.articleContent);
            mContent.setMovementMethod(new ScrollingMovementMethod());
        }

    }

}