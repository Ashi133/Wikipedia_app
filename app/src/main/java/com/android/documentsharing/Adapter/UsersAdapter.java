package com.android.documentsharing.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.android.documentsharing.Holder.Users;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.isValidNumber;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
@SuppressWarnings("ALL")
public class UsersAdapter extends RecyclerView.Adapter{
    ArrayList<Users> arrayList;
    ArrayList<String> numbers;
    Context context;
    String name,about,url,uri,path;
    int count;
    private static final int empty=0;
    private static final int not_empty=1;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    DatabaseReference database=FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Documents");
    String []months={"Jan","Feb","March","April","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    public UsersAdapter(Context context,ArrayList<Users> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == empty){
            return new Empty(LayoutInflater.from(context).inflate(R.layout.empty_user,parent,false));
        }else {
            return new NotEmpty(LayoutInflater.from(context).inflate(R.layout.user_row,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == NotEmpty.class){
            database=FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
            NotEmpty container=(NotEmpty)holder;
            loadContact();
            name=arrayList.get(position).getName();
            about=arrayList.get(position).getAbout();
            url=arrayList.get(position).getUrl();
            try {
                container.name.setText(name);
                container.about.setText(about);
                if (numbers.contains(arrayList.get(position).getFinalNo()) || numbers.contains(arrayList.get(position).getNumber())){
                    container.InContact.setVisibility(View.VISIBLE);
                }else {
                    container.InContact.setVisibility(View.GONE);
                }
                Glide.with(context)
                        .load(Uri.parse(url))
                        .placeholder(R.drawable.user)
                        .centerCrop()
                        .into(container.profile);
            }catch (Exception e){
                Toast.makeText(context, "Users Adapter:Error ="+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            container.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (uri != null && path != null){
                        try{
                            String array[]=path.split("/");
                            String name=array[array.length-1];
                            String extension=name.split("\\.")[1];
                            String time= UpdateOnlineStatus.getCurrentDateTime();
                            String date= formatDate(UpdateOnlineStatus.getCurrentDate());
                            String size= getSize(path);
                            String receiver=arrayList.get(position).getName();
                            database.child("Document_user").child(auth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String owner=snapshot.getValue(String.class);
                                        if (owner != null){
                                            String node=FirebaseDatabase.getInstance().getReference().push().getKey();
                                            StorageReference reference = storageReference.child(auth.getCurrentUser().getUid()).child(node)
                                                    .child(name);
                                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                            builder.setCancelable(false);
                                            builder.setMessage("Sharing Please wait...");
                                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    reference.delete();
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog dialog=builder.create();
                                            dialog.show();
                                            reference.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    if (taskSnapshot.getTask().isSuccessful()){
                                                        dialog.dismiss();
                                                        documentHolder holder1=new documentHolder(name,extension,size,date,time,auth.getCurrentUser().getUid(),owner,node);
                                                        holder1.setReceiverName(receiver);
                                                        holder1.setAccess(true);
                                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri4) {
                                                                holder1.setUrl(uri4.toString());
                                                                database.child("Documents").child(auth.getCurrentUser().getUid()).child("shared").child(node).setValue(holder1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        holder1.setReceiverName(owner);
                                                                        database.child("Documents").child(arrayList.get(position).getuId()).child("received").child(node).setValue(holder1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                ((AppCompatActivity)context).finish();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(context, "User Adapter-1 :Failed to update database due to : "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(context, "User Adapter-2 :Failed to update database due to : "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(context, "User Adapter-3:Failed to get url due to : "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(context, "User Adapter-5:Failed to upload due to : "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }catch (Exception e){
                            Log.e("User adapter:error=",e.getLocalizedMessage());
                        }
                    }
                }
            });
        }

    }
    private String getSize(String pth) {
        String app_size;
        long size = new File(pth).length();
        DecimalFormat df=new DecimalFormat("0.00");
        Float sizeKb=1024.0f;
        Float sizeMb=sizeKb*sizeKb;
        Float sizeGb=sizeMb*sizeMb;
        Float sizeTb=sizeGb*sizeGb;
        if (size<1024){
            app_size=df.format(size)+"B";
        }
        else if (size<sizeMb){
            app_size=df.format(size/sizeKb)+"KB";
        }
        else if (size<sizeGb){
            app_size=df.format(size/sizeMb)+"MB";
        }
        else{
            app_size=df.format(size/sizeGb)+"GB";
        }
        return app_size;
    }

    private String formatDate(String currentDate) {
        String date = "";
        if (currentDate != null){
            String array[]=currentDate.split(":");
            date=String.format("%s %s,%s",array[0],months[(Integer.parseInt(array[1])-1)],array[2]);
        }
        return date;
    }

    @Override
    public int getItemViewType(int position) {
        if (count == -1){
            return empty;
        }else {
            return not_empty;
        }
    }
    private void loadContact() {
        numbers=new ArrayList<>();
        try{
            @SuppressLint ("Recycle") Cursor cursor= context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()){
                @SuppressLint ("Range") String phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber.length() >= 10){
                    numbers.add(isValidNumber.Number(phoneNumber));
                }
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
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

    public void notifyPath(String uri2,String path2) {
        uri=uri2;
        path=path2;
    }

    public class Empty extends RecyclerView.ViewHolder{

        public Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
    public class NotEmpty extends RecyclerView.ViewHolder{
        TextView name,about,InContact;
        CircleImageView profile;
        public NotEmpty(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.user_name);
            about=itemView.findViewById(R.id.user_about);
            InContact=itemView.findViewById(R.id.user_in_contact);
            profile=itemView.findViewById(R.id.userProfile);
        }

    }
}
