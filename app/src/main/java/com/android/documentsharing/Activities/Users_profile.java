package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.android.documentsharing.R;
import com.android.documentsharing.databinding.ActivityUsersProfileBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class Users_profile extends AppCompatActivity {
    ActivityUsersProfileBinding binding;
    String name,about,number,url,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUsersProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        name=getIntent().getStringExtra("name");
        about=getIntent().getStringExtra("about");
        url=getIntent().getStringExtra("url");
        id=getIntent().getStringExtra("id");
        number=getIntent().getStringExtra("number");
        try {
            binding.userName1.setText(name);
            binding.userAbout1.setText(about);
            binding.userContact1.setText(number);
            Glide.with(getApplicationContext()).load(Uri.parse(url))
                    .placeholder(R.drawable.user)
                    .centerCrop()
                    .into(binding.userProfile1);
        }catch (Exception e){
            e.printStackTrace();
        }
        binding.userCall1.setOnClickListener(view -> {
            if (id != null){
                FirebaseDatabase.getInstance().getReference().child("DocumentSharing").child("Document_user")
                        .child(id).child("Private").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            boolean value=snapshot.getValue(boolean.class);
                            if (!value){
                                if (ContextCompat.checkSelfPermission(Users_profile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(Users_profile.this, new String[]{ Manifest.permission.CALL_PHONE },21);
                                }else {
                                    makeCall();
                                }
                            }else {
                                new AlertDialog.Builder(Users_profile.this)
                                        .setCancelable(false)
                                        .setTitle("Privacy")
                                        .setMessage("Account is private you can't call to "+name)
                                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
            }else {
                Toast.makeText(this, "Users profile :Permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void makeCall() {
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
        startActivity(intent);
    }

}