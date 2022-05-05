package com.android.wikipedia.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.wikipedia.Fragments.CategoryList;
import com.android.wikipedia.R;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CategoryListAdapter extends RecyclerView.Adapter {
    ArrayList<File> arrayList;
    Context context;
    int count;
    int empty=0,not_empty=1;
    CategoryList fragmentActivity;
    String []months={"Jan","Feb","March","April","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    public CategoryListAdapter(Context context, ArrayList<File> arrayList, CategoryList fragmentActivity) {
        this.arrayList = arrayList;
        this.context = context;
        this.fragmentActivity=fragmentActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return null;
    }

    @SuppressLint ({ "SetTextI18n", "NonConstantResourceId" })
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint ("RecyclerView") int position) {

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
    private String getSize(String pth) {
        String app_size;
        long size = new File(pth).length();
        DecimalFormat df=new DecimalFormat("0.00");
        float sizeKb=1024.0f;
        float sizeMb=sizeKb*sizeKb;
        float sizeGb=sizeMb*sizeMb;
        float sizeTb=sizeGb*sizeGb;
        if (size<1024){
            app_size=df.format(size)+"B";
        }
        else if (size<sizeMb){
            app_size=df.format(size/sizeKb)+"KB";
        }
        else if (size<sizeGb){
            app_size=df.format(size/sizeMb)+"MB";
        }
        else if (size<sizeTb){
            app_size=df.format(size/sizeGb)+"GB";
        }else {
            app_size= df.format(size/sizeTb)+"TB";
        }
        return app_size;
    }
    private String formatDate(String currentDate) {
        String date = "";
        if (currentDate != null){
            String[] array =currentDate.split(":");
            date=String.format("%s %s,%s",array[0],months[(Integer.parseInt(array[1])-1)],array[2]);
        }
        return date;
    }
    @Override
    public int getItemViewType(int position) {
        if (count == -1)
            return empty;
        else
            return not_empty;
    }

    @SuppressLint ("NotifyDataSetChanged")
    public void updateList(ArrayList<File> temp) {
        arrayList=temp;
        notifyDataSetChanged();
    }

    public static class Empty extends RecyclerView.ViewHolder{
        TextView label;
        public Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
    public static class not_Empty extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name,property,receiver,New;
        ImageView option;
        public not_Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
}
