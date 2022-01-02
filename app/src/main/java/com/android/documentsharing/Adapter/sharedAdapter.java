package com.android.documentsharing.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.Activities.Users;
import com.android.documentsharing.Activities.preview;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.IconsHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.downloadFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class sharedAdapter extends RecyclerView.Adapter {
    private ArrayList<documentHolder> arrayList;
    private Context context;
    private static final int empty=0;
    private static final int not_empty=1;
    private int count;
    private String dname,dsize,dtime,ddate,dreceiver;
    boolean daccess;
    DatabaseReference database=FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Documents");
    FirebaseAuth auth=FirebaseAuth.getInstance();
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
        if (holder.getClass()== NotEmpty.class){
            NotEmpty container=(NotEmpty) holder;
            dname=arrayList.get(position).getName();
            dsize=arrayList.get(position).getSize();
            dtime=arrayList.get(position).getTime();
            ddate=arrayList.get(position).getDate();
            dreceiver=arrayList.get(position).getReceiverName();
            daccess=arrayList.get(position).isAccess();
            container.name.setSelected(true);
            container.property.setSelected(true);
            container.receiver.setSelected(true);
            container.name.setText(dname);
            boolean New=arrayList.get(position).isNew();
            if (New){
                container.New.setVisibility(View.VISIBLE);
            }else {
                container.New.setVisibility(View.GONE);
            }
            container.property.setText(String.format("%s | %s | %s",dsize,dtime,ddate));
            if (dreceiver.contains(":")){
                String array[]=dreceiver.split(":");
                container.receiver.setText(String.format("Shared with %s + %d others",array[0],array.length-1));
            }else {
                container.receiver.setText(String.format("Shared with %s",dreceiver));
            }
            if (daccess){
                container.relativeLayout.setBackgroundResource(R.drawable.bg);
            }else {
                container.relativeLayout.setBackgroundResource(R.drawable.bg2);
            }
            try {
                int res=IconsHolder.getIcon(arrayList.get(position).getExtension());
                container.icon.setImageResource(res);
            }catch (Exception e){
                Toast.makeText(context, "Icon not found for file extension : "+arrayList.get(position).getExtension(), Toast.LENGTH_SHORT).show();
                container.icon.setImageResource(R.drawable.user);
            }
            container.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu=new PopupMenu(context,container.option);
                    popupMenu.getMenuInflater().inflate(R.menu.shared_menu,popupMenu.getMenu());
                    MenuItem item1=popupMenu.getMenu().findItem(R.id.denied_access);
                    DatabaseReference ref=
                            database.child("Documents").child(auth.getCurrentUser().getUid()).child("shared")
                                    .child(arrayList.get(position).getNodeKey()).child("access");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                boolean access=snapshot.getValue(boolean.class);
                                if (access){
                                    item1.setTitle("Block Access");
                                }else {
                                    item1.setTitle("Allow Access");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.share_document:
                                    Intent intent=new Intent(context, Users.class);
                                    intent.putExtra("shareTo", (Parcelable) arrayList.get(position));
                                    intent.putExtra("fromAdapter",true);
                                    context.startActivity(intent);
                                    break;
                                case R.id.denied_access:
                                    if (!UpdateOnlineStatus.check_network_state(context)){
                                        Toast.makeText(context, "Connection error!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    boolean access=snapshot.getValue(boolean.class);
                                                    database.child("Documents").child(auth.getCurrentUser().getUid()).child("shared")
                                                            .child(arrayList.get(position).getNodeKey()).child("access").setValue(!access);
                                                    database.child("Documents").child(arrayList.get(position).getUid()).child("received")
                                                            .child(arrayList.get(position).getNodeKey()).child("access").setValue(!access);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    break;
                                case R.id.delete_document:
                                    if (!UpdateOnlineStatus.check_network_state(context)){
                                        Toast.makeText(context, "Connection error!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                        builder.setCancelable(false);
                                        builder.setTitle("Delete");
                                        builder.setMessage("Do you want to delete this document ?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AlertDialog.Builder builder1=new AlertDialog.Builder(context);
                                                View view1=LayoutInflater.from(context).inflate(R.layout.delete_document,null);
                                                AlertDialog dialog= builder1.create();
                                                dialog.setView(view1);
                                                dialog.show();
                                                dialogInterface.dismiss();
                                                CheckBox checkBox=view1.findViewById(R.id.checkbox_delete);
                                                TextView label,yes,no;
                                                label=view1.findViewById(R.id.checkbox_label);
                                                yes=view1.findViewById(R.id.delete_yes1);
                                                no=view1.findViewById(R.id.delete_no1);
                                                no.setOnClickListener(view2 ->dialog.dismiss());
                                                checkBox.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        checkBox.setChecked(checkBox.isChecked());
                                                    }
                                                });
                                                label.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        checkBox.setChecked(!checkBox.isChecked());
                                                    }
                                                });
                                                String sId=auth.getCurrentUser().getUid();
                                                String rId=arrayList.get(position).getUid();
                                                String node=arrayList.get(position).getNodeKey();
                                                String url=arrayList.get(position).getUrl();
                                                StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                                yes.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                        ProgressDialog progressDialog=new ProgressDialog(context);
                                                        progressDialog.setProgressStyle(0);
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.setMessage("Deleting , please wait a moment...");
                                                        progressDialog.show();
                                                        if (checkBox.isChecked()){
                                                            ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    database.child("Documents").child(sId).child("shared").child(node).setValue(null);
                                                                    database.child("Documents").child(rId).child("received").child(node).setValue(null);
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(context, "shared adapter : unable to delete due to : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }else {
                                                            database.child("Documents").child(sId).child("shared").child(node).setValue(null);
                                                            progressDialog.dismiss();
                                                            Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                    break;
                                case R.id.download_document:
                                    if (!UpdateOnlineStatus.check_network_state(context)){
                                        Toast.makeText(context, "Connection error!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String n=arrayList.get(position).getName();
                                        String ext=arrayList.get(position).getExtension();
                                        downloadFile.download(n,ext,context,arrayList.get(position).getUrl());
                                    }
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            container.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name=arrayList.get(position).getName();
                    String extension=arrayList.get(position).getExtension();
                    String url=arrayList.get(position).getUrl();
                    Intent intent=new Intent(context, preview.class);
                    intent.putExtra("name",name);
                    intent.putExtra("url",url);
                    intent.putExtra("ext",extension);
                    context.startActivity(intent);
                }
            });
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
        public Empty(@NonNull View itemView) {
            super(itemView);
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
