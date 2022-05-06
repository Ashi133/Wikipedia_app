package com.android.wikipedia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.android.wikipedia.R;
import com.android.wikipedia.*;
import com.android.wikipedia.databinding.ActivitySplashBinding;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    static final long Timer=2000;
    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        final TypeWriter tw = findViewById(R.id.type_writer);
        tw.setText("");
        tw.setCharacterDelay(35);
        tw.animateText("Developed By:\n Ashi Agrawal");
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, HomeScreen.class));
            finish();
        }, Timer);
    }
}