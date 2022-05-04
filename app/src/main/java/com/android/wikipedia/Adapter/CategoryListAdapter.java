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

import com.android.wikipedia.BuildConfig;
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
        if (viewType == empty){
            return new Empty(LayoutInflater.from(context).inflate(R.layout.empty_document,parent,false));
        }else {
            return new not_Empty(LayoutInflater.from(context).inflate(R.layout.shared_document_item,parent,false));
        }
    }

    @SuppressLint ({ "SetTextI18n", "NonConstantResourceId" })
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint ("RecyclerView") int position) {
        if (holder.getClass() == Empty.class){
            Empty container=(Empty) holder;
            container.label.setText("Nothing has been downloaded ! pull down to refresh!");
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
                                View view3=LayoutInflater.from(context).inflate(R.layout.share_via,null);
                                AlertDialog dialog=new AlertDialog.Builder(context)
                                        .setCancelable(false)
                                        .setTitle("Share Via")
                                        .setView(view3)
                                        .setPositiveButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                                        .create();
                                dialog.show();
                                LinearLayout layout,layout1;
                                layout=view3.findViewById(R.id.linear2);
                                layout1=view3.findViewById(R.id.linear3);
                                //sharing in this application.
                                layout.setOnClickListener(view12 -> {
                                    dialog.dismiss();
                                    String path1 =arrayList.get(position).getPath();
                                    File file1=arrayList.get(position);
                                    String uri=Uri.fromFile(file1).toString();
                                    Intent intent=new Intent(context, Users.class);
                                    intent.putExtra("path", path1);
                                    intent.putExtra("uri",uri);
                                    context.startActivity(intent);
                                });
                                layout1.setOnClickListener(view1 -> {//sharing to other apps.
                                    dialog.dismiss();
                                    Intent intent=new Intent(Intent.ACTION_SEND);
                                    intent.setType("*/*");
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(arrayList.get(position).getPath()));
                                    context.startActivity(intent);
                                });
                                break;
                            case R.id.Delete:
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Delete");
                                builder.setMessage("Do you want to delete this file ?");
                                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                                    File file1=arrayList.get(position);
                                    if (file1.delete()){
                                        dialogInterface.dismiss();
                                        fragmentActivity.loadData(false);
                                        Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        dialogInterface.dismiss();
                                        Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                                builder.setCancelable(false);
                                builder.show();
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                });
                container.itemView.setOnClickListener(view -> {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri= FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",arrayList.get(position));
                    intent.setDataAndType(uri,"application/*");
                    Intent intent2=Intent.createChooser(intent,"Open with..");
                    //intent2.putExtra(Intent.EXTRA_INITIAL_INTENTS,intent);
                    try {
                        context.startActivity(intent2);
                    }catch (ActivityNotFoundException e){
                        Toast.makeText(context, "No app found to open this file!", Toast.LENGTH_SHORT).show();
                    }
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
