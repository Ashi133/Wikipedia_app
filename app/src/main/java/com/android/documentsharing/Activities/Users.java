package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.android.documentsharing.Adapter.UsersAdapter;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.ActivityUsersBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class Users extends AppCompatActivity {
    ActivityUsersBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    UsersAdapter adapter;
    ArrayList<com.android.documentsharing.Holder.Users> arrayList;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.usersBack.setOnClickListener(view -> finish());
        arrayList=new ArrayList<>();
        adapter=new UsersAdapter(this,arrayList);
        binding.usersRv.setHasFixedSize(true);
        binding.usersRv.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRv.setAdapter(adapter);
        auth=FirebaseAuth.getInstance();
        path=getIntent().getStringExtra("uri");
        if (path != null){
            adapter.notifyPath(path);
        }
        reference= FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
        loadUsers();//loading users.
        binding.usersSwipe.setOnRefreshListener(() -> {
            loadUsers();
            binding.usersSwipe.setRefreshing(false);
        });


    }

    private void loadUsers() {
        if (!UpdateOnlineStatus.check_network_state(this)){
            Toast.makeText(this, "Internet connection error!", Toast.LENGTH_SHORT).show();
        }if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_CONTACTS },22);
        }else {
            binding.usersRv.showShimmerAdapter();
            reference.child("Document_user").addValueEventListener(new ValueEventListener() {
                @SuppressLint ({ "NotifyDataSetChanged", "SetTextI18n" })
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        arrayList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            com.android.documentsharing.Holder.Users users=dataSnapshot.getValue(com.android.documentsharing.Holder.Users.class);
                            if (users != null){
                                if (!users.getuId().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                                    arrayList.add(users);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (arrayList.size() == 0){
                            binding.usersCount.setVisibility(View.GONE);
                        }else {
                            binding.usersCount.setVisibility(View.VISIBLE);
                            if (arrayList.size() < 10){
                                binding.usersCount.setText("0"+arrayList.size());
                            }else{
                                binding.usersCount.setText(String.valueOf(arrayList.size()));
                            }
                        }
                        binding.usersRv.hideShimmerAdapter();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}