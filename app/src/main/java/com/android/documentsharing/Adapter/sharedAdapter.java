package com.android.documentsharing.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.R;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class sharedAdapter extends RecyclerView.Adapter {
    private ArrayList<documentHolder> arrayList;
    private Context context;
    private static final int empty=0;
    private static final int not_empty=1;
    private int count;

    public sharedAdapter(Context context,ArrayList<documentHolder> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==empty){
            return new Empty(LayoutInflater.from(context).inflate(R.layout.empty_document,parent,false));
        }else {
            return new NotEmpty(LayoutInflater.from(context).inflate(R.layout.shared_document_item,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (count == -1){
            return empty;
        }
        else{
            return not_empty;
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
    public class Empty extends RecyclerView.ViewHolder{
        public Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
    public class NotEmpty extends RecyclerView.ViewHolder{

        public NotEmpty(@NonNull View itemView) {
            super(itemView);
        }

    }
}
