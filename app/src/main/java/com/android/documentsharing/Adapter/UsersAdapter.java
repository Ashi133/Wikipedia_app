package com.android.documentsharing.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.documentsharing.Holder.Users;
import com.android.documentsharing.R;
import com.android.documentsharing.isValidNumber;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
@SuppressWarnings("ALL")
public class UsersAdapter extends RecyclerView.Adapter{
    ArrayList<Users> arrayList;
    ArrayList<String> numbers;
    Context context;
    String name,about,url;
    int count=1;
    private static final int empty=0;
    private static final int not_empty=1;
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
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (count == 1){
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
            count=1;
        }else {
            count=arrayList.size();
        }
        return count;
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
