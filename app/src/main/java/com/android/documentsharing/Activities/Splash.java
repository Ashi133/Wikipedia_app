package com.android.documentsharing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.documentsharing.R;
import com.android.documentsharing.TypeWriter;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    static final long Timer=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();
        final TypeWriter tw = findViewById(R.id.type_writer);
        tw.setText("");
        tw.setCharacterDelay(35);
        tw.animateText("Developed By : \n Ashi And Rahmatullah");

        new Handler().postDelayed(() -> {
            Intent intent=new Intent(Splash.this,Login.class);
            startActivity(intent);
            finish();
        }, Timer);
    }

}