package com.android.documentsharing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.android.documentsharing.R;
import com.android.documentsharing.TypeWriter;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    static final long Timer=2000;
    ActivitySplashBinding binding;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        final TypeWriter tw = findViewById(R.id.type_writer);
        tw.setText("");
        tw.setCharacterDelay(35);
        tw.animateText("Developed By : \n Ashi And Rahmatullah");
        auth = FirebaseAuth.getInstance();
        new Handler().postDelayed(() -> {
            authStateListener = firebaseAuth -> {
                FirebaseUser user = auth.getCurrentUser();
                SharedPreferences sharedPreferences = getSharedPreferences("state", MODE_PRIVATE);
                if (!UpdateOnlineStatus.check_network_state(Splash.this)) {
                    startActivity(new Intent(Splash.this, HomeScreen.class));
                } else {
                    if (user == null) {
                        startActivity(new Intent(Splash.this, Login.class));
                    } else if (!sharedPreferences.getBoolean("isSaved", false)) {
                        startActivity(new Intent(Splash.this, saveProfile.class));
                    } else {
                        startActivity(new Intent(Splash.this, HomeScreen.class));
                    }
                }
                finish();
            };
            auth.addAuthStateListener(authStateListener);
        }, Timer);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.removeAuthStateListener(authStateListener);
    }
}