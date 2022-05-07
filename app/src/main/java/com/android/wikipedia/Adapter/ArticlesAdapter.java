package com.android.wikipedia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
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
        }

    }

}