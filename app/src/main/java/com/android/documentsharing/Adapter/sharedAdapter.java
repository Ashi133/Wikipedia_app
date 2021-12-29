package com.android.documentsharing.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentsharing.Activities.preview;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.IconsHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
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
            container.property.setText(String.format("%s | %s | %s",dsize,dtime,ddate));
            container.receiver.setText(String.format("Shared with %s",dreceiver));
            container.icon.setImageResource(IconsHolder.getIcon(arrayList.get(position).getExtension()));
            container.option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu=new PopupMenu(context,container.option);
                    popupMenu.getMenuInflater().inflate(R.menu.shared_menu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            MenuItem item1=popupMenu.getMenu().findItem(R.id.denied_access);
                            DatabaseReference ref=
                            reference.child("Documents").child(auth.getCurrentUser().getUid()).child("shared")
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
                            switch (item.getItemId()){
                                case R.id.share_document:

                                    break;
                                case R.id.denied_access:
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                boolean access=snapshot.getValue(boolean.class);
                                                reference.child("Documents").child(auth.getCurrentUser().getUid()).child("shared")
                                                        .child(arrayList.get(position).getNodeKey()).child("access").setValue(!access);
                                                reference.child("Documents").child(arrayList.get(position).getUid()).child("received")
                                                        .child(arrayList.get(position).getNodeKey()).child("access").setValue(!access);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                case R.id.delete_document:

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
    public class Empty extends RecyclerView.ViewHolder{
        public Empty(@NonNull View itemView) {
            super(itemView);
        }

    }
    public class NotEmpty extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name,property,receiver;
        ImageView option;
        public NotEmpty(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.document_icon);
            property=itemView.findViewById(R.id.document_property);
            receiver=itemView.findViewById(R.id.document_receiver);
            option=itemView.findViewById(R.id.document_option);
            name=itemView.findViewById(R.id.document_name);
        }
    }
}
