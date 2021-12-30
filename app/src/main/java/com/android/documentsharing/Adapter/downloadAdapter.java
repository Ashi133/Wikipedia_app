package com.android.documentsharing.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.R;

import java.io.File;
import java.util.ArrayList;

public class downloadAdapter extends RecyclerView.Adapter {
    ArrayList<File> arrayList;
    Context context;
    int count;
    int empty=0,not_empty=1;
    public downloadAdapter(Context context,ArrayList<File> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == empty){
            return new Empty(LayoutInflater.from(context).inflate(R.layout.empty_document,parent,false));
        }else {
            return new not_Empty(LayoutInflater.from(context).inflate(R.layout.shared_document_item,parent,false));
        }
    }

    @SuppressLint ("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == Empty.class){
            Empty container=(Empty) holder;
            container.label.setText("Nothing has been downloaded !");
            container.label.setGravity(Gravity.CENTER);
        }else if (holder.getClass() == not_Empty.class){
            not_Empty container=(not_Empty) holder;
        }
    }
    @Override
    public int getItemCount() {
        if (arrayList.size() == 0){
            count=-1;
            return count+2;
        }else {
            count=arrayList.size();
            return count;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (count == -1)
            return empty;
        else
            return not_empty;
    }
    public static class Empty extends RecyclerView.ViewHolder{
        TextView label;
        public Empty(@NonNull View itemView) {
            super(itemView);
            label=itemView.findViewById(R.id.document_info);
        }

    }
    public static class not_Empty extends RecyclerView.ViewHolder{

        public not_Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
}
