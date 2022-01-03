package com.android.documentsharing.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.Activities.Users;
import com.android.documentsharing.Activities.preview;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.IconsHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.downloadFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class receivedAdapter extends RecyclerView.Adapter {
    private ArrayList<documentHolder> arrayList;
    private Context context;
    private static final int empty=0;
    private static final int not_empty=1;
    private int count;
    private String dname,dsize,dtime,ddate,dreceiver;
    boolean daccess,New;
    DatabaseReference database= FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Documents");
    FirebaseAuth auth=FirebaseAuth.getInstance();
    public receivedAdapter(Context context,ArrayList<documentHolder> arrayList) {
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
        if (holder.getClass()== NotEmpty.class){
            NotEmpty container=(NotEmpty) holder;
            dname=arrayList.get(position).getName();
            dsize=arrayList.get(position).getSize();
            dtime=arrayList.get(position).getTime();
            ddate=arrayList.get(position).getDate();
            dreceiver=arrayList.get(position).getOwnerName();
            daccess=arrayList.get(position).isAccess();
            New = arrayList.get(position).isNew();
            container.name.setSelected(true);
            container.property.setSelected(true);
            container.receiver.setSelected(true);
            container.name.setText(dname);
            container.property.setText(String.format("%s | %s | %s",dsize,dtime,ddate));
            container.receiver.setText(String.format("Received from %s",dreceiver));
            if (daccess){
                container.relativeLayout.setBackgroundResource(R.drawable.bg);
            }else {
                container.relativeLayout.setBackgroundResource(R.drawable.bg2);
            }
            if (New){
                container.New.setVisibility(View.VISIBLE);
            }else {
                container.New.setVisibility(View.GONE);
            }
            int res=IconsHolder.getIcon(arrayList.get(position).getExtension());
            try {
                container.icon.setImageResource(res);
            }catch (Exception e){
                Toast.makeText(context, "Icon not found for file extension : "+arrayList.get(position).getExtension(), Toast.LENGTH_SHORT).show();
                container.icon.setImageResource(R.drawable.user);
            }
            container.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Date = "+ UpdateOnlineStatus.getCurrentDate(), Toast.LENGTH_SHORT).show();
                }
            });
            container.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    daccess=arrayList.get(position).isAccess();
                    if (!daccess){
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle("Access Denied");
                        builder.setMessage("Access is denied by "+arrayList.get(position).getOwnerName());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }else{
                        String name=arrayList.get(position).getName();
                        String extension=arrayList.get(position).getExtension();
                        String url=arrayList.get(position).getUrl();
                        Intent intent=new Intent(context, preview.class);
                        intent.putExtra("name",name);
                        intent.putExtra("url",url);
                        intent.putExtra("ext",extension);
                        if (arrayList.get(position).isNew()){
                            database.child("Documents").child(auth.getCurrentUser().getUid()).child("received").child(arrayList.get(position).getNodeKey())
                                    .child("new").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    context.startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("received adapter : set new error : ",e.getLocalizedMessage());
                                }
                            });
                        }else {
                            context.startActivity(intent);
                        }
                    }
                }
            });
            container.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu=new PopupMenu(context,container.option);
                    popupMenu.getMenuInflater().inflate(R.menu.received_menu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.share:
                                    Intent intent=new Intent(context, Users.class);
                                    intent.putExtra("shareTo", (Parcelable) arrayList.get(position));
                                    intent.putExtra("fromReceiver",true);
                                    context.startActivity(intent);
                                    break;
                                case R.id.download:
                                    if (!UpdateOnlineStatus.check_network_state(context)){
                                        Toast.makeText(context, "Connection error!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String n=arrayList.get(position).getName();
                                        if (arrayList.get(position).isAccess()){
                                            downloadFile.download(n,context,arrayList.get(position).getUrl());
                                        }else {
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Access Denied")
                                                    .setMessage("Download Access Is Denied By "+arrayList.get(position).getOwnerName()+" for this file!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                        }
                                    }
                                    break;
                                case R.id.delete:
                                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                    builder.setCancelable(false);
                                    builder.setTitle("Delete");
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String node=arrayList.get(position).getNodeKey();
                                            String id=auth.getCurrentUser().getUid();
                                            database.child("Documents").child(id).child("received").child(node).setValue(null);
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
        }else if (holder.getClass() == Empty.class){
            Empty hold=(Empty) holder;
            hold.info.setText("Nothing is received ! please check internet connection or pull down to refresh list!");
        }
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

    public void updateList(ArrayList<documentHolder> temp) {
        arrayList=temp;
        notifyDataSetChanged();
    }

    public class Empty extends RecyclerView.ViewHolder{
        TextView info;
        public Empty(@NonNull View itemView) {
            super(itemView);
            info=itemView.findViewById(R.id.document_info);
        }

    }
    public class NotEmpty extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name,property,receiver,New;
        ImageView option;
        RelativeLayout relativeLayout;
        public NotEmpty(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.document_icon);
            property=itemView.findViewById(R.id.document_property);
            receiver=itemView.findViewById(R.id.document_receiver);
            option=itemView.findViewById(R.id.document_option);
            name=itemView.findViewById(R.id.document_name);
            relativeLayout=itemView.findViewById(R.id.outline_rel);
            New=itemView.findViewById(R.id.newDoc);
        }
    }
}
