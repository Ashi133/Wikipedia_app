package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.documentsharing.databinding.ActivityContactUsBinding;

import java.util.Objects;

public class Contact_Us extends AppCompatActivity {
    ActivityContactUsBinding binding;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.myNo.setOnClickListener(view -> CheckPermission(binding.myNo.getText().toString()));
        binding.sisNo.setOnClickListener(view -> CheckPermission(binding.sisNo.getText().toString()));
        binding.myMail.setOnClickListener(view -> OpenMail(binding.myMail.getText().toString()));
        binding.sisMail.setOnClickListener(view -> OpenMail(binding.sisMail.getText().toString()));
        binding.contactBack.setOnClickListener(view -> finish());
    }

    private void OpenMail(String s) {
        try {
            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"+s));
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "contact us :Error while open email:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckPermission(String s) {
        number=s;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CALL_PHONE },21);
        }else {
            makeCall(number);
        }
    }

    private void makeCall(String s) {
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+s));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall(number);
            }else {
                Toast.makeText(this, "contact us :Permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}