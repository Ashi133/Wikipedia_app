package com.android.documentsharing.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.documentsharing.R;
import com.android.documentsharing.databinding.ActivityUserProfileBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {
    ActivityUserProfileBinding binding;
    String name,about,number,url,id;
    @SuppressLint ("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        name=getIntent().getStringExtra("name");
        about=getIntent().getStringExtra("about");
        number=getIntent().getStringExtra("number");
        url=getIntent().getStringExtra("url");
        id=getIntent().getStringExtra("id");
        try {
            binding.userName1.setText(name);
            binding.userAbout1.setText(about);
            Glide.with(getApplicationContext()).load(Uri.parse(url))
                    .placeholder(R.drawable.user)
                    .centerCrop()
                    .into(binding.userProfile1);
        }catch (Exception e){
            Log.d("Error ======================================",e.getLocalizedMessage());
        }
        binding.profileBack.setOnClickListener(v->finish());
        binding.userCall1.setOnClickListener(view -> {
            if (id != null){
                FirebaseDatabase.getInstance().getReference().child("DocumentSharing").child("Document_user")
                        .child(id).child("private").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            boolean value=snapshot.getValue(boolean.class);
                            if (!value){
                                if (ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(UserProfile.this, new String[]{ Manifest.permission.CALL_PHONE },21);
                                }else {
                                    makeCall();
                                }
                            }else {
                                new AlertDialog.Builder(UserProfile.this)
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

    @Override
    public void onBackPressed() {
        binding.profileBack.performClick();
    }

}