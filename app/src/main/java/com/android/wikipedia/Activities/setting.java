package com.android.wikipedia.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.wikipedia.UpdateTheme;
import com.android.wikipedia.databinding.ActivitySettingBinding;

public class setting extends AppCompatActivity {
ActivitySettingBinding binding;
Switch darkTheme,lightTheme;
    @SuppressLint ("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        darkTheme=binding.switch1;
        lightTheme=binding.switch2;
        int theme1=UpdateTheme.getTheme("dark",setting.this);
        int theme2=UpdateTheme.getTheme("light",setting.this);
        Log.d("Value === ",theme1+" ===="+theme2);
        if (theme1 == 2){
            darkTheme.setChecked(true);
            binding.darkTheme.setTextColor(Color.WHITE);
            binding.lightTheme.setTextColor(Color.WHITE);
            lightTheme.setChecked(false);
        }
        if (theme2==1){
            darkTheme.setChecked(false);
            lightTheme.setChecked(true);
            binding.lightTheme.setTextColor(Color.BLACK);
            binding.darkTheme.setTextColor(Color.BLACK);
        }
        darkTheme.setOnCheckedChangeListener((compoundButton, b) -> {
            if (darkTheme.isChecked()){
                UpdateTheme.setTheme("dark", AppCompatDelegate.MODE_NIGHT_YES,setting.this);
                darkTheme.setChecked(b);
                lightTheme.setChecked(!b);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                UpdateTheme.setTheme("dark", AppCompatDelegate.MODE_NIGHT_NO,setting.this);
            }
        });
        lightTheme.setOnCheckedChangeListener((compoundButton, b) -> {
            if (lightTheme.isChecked()){
                UpdateTheme.setTheme("light", AppCompatDelegate.MODE_NIGHT_NO,setting.this);
                darkTheme.setChecked(!b);
                lightTheme.setChecked(b);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else{
                UpdateTheme.setTheme("light", AppCompatDelegate.MODE_NIGHT_YES,setting.this);
            }
        });
    }
}
