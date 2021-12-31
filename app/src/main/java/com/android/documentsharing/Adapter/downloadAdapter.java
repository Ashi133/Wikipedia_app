package com.android.documentsharing.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.IconsHolder;
import com.android.documentsharing.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class downloadAdapter extends RecyclerView.Adapter {
    ArrayList<File> arrayList;
    Context context;
    int count;
    int empty=0,not_empty=1;
    String []months={"Jan","Feb","March","April","May","June","July","Aug","Sep","Oct","Nov","Dec"};
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

    @SuppressLint ({ "SetTextI18n", "NonConstantResourceId" })
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == Empty.class){
            Empty container=(Empty) holder;
            container.label.setText("Nothing has been downloaded !");
            container.label.setGravity(Gravity.CENTER);
        }else if (holder.getClass() == not_Empty.class){
            not_Empty container=(not_Empty) holder;
            try {
                container.receiver.setVisibility(View.GONE);
                File file=arrayList.get(position);
                String name=file.getName();
                String path=file.getPath();
                String size=getSize(path);
                long time=file.lastModified();
                Date date=new Date(time);
                @SuppressLint ("SimpleDateFormat") SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
                @SuppressLint ("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat("dd:MM:yyyy");
                String Time=timeFormat.format(date);
                String mDate= dateFormat.format(date);
                String finalDate=formatDate(mDate);
                container.New.setVisibility(View.VISIBLE);
                container.New.setText("Downloaded");
                container.name.setSelected(true);
                container.property.setSelected(true);
                container.name.setText(name);
                container.property.setText(String.format("%s | %s | %s",size,Time,finalDate));
                String[] array =name.split("\\.");
                String ext=array[array.length-1];
                int res= IconsHolder.getIcon(ext);
                container.icon.setImageResource(res);
                container.option.setOnClickListener(view -> {
                    PopupMenu popupMenu=new PopupMenu(context,container.option);
                    popupMenu.getMenuInflater().inflate(R.menu.downloaded_menu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getItemId()){
                            case R.id.Share:

                                break;
                            case R.id.Delete:

                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                });
            }catch (Exception e){
                Toast.makeText(context, "Download Adapter : error = "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

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
            label=itemView.findViewById(R.id.document_info);
        }

    }
    public static class not_Empty extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name,property,receiver,New;
        ImageView option;
        public not_Empty(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.document_icon);
            property=itemView.findViewById(R.id.document_property);
            receiver=itemView.findViewById(R.id.document_receiver);
            option=itemView.findViewById(R.id.document_option);
            name=itemView.findViewById(R.id.document_name);
            New=itemView.findViewById(R.id.newDoc);
        }

    }
}
