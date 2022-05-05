package com.android.wikipedia.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.wikipedia.UpdateTheme;
import com.android.wikipedia.databinding.ActivitySettingBinding;

public class setting extends AppCompatActivity {
ActivitySettingBinding binding;
Switch darkTheme,lightTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        darkTheme=binding.switch1;
        lightTheme=binding.switch2;
        //AppCompatDelegate.setDefaultNightMode();
        int theme=UpdateTheme.getTheme("dark",setting.this);
        if (theme == 0){
            darkTheme.setChecked(false);
            if ((UpdateTheme.getTheme("light",setting.this))==0){
                lightTheme.setChecked(false);
            }else{
                lightTheme.setChecked(true);
            }
        }else{
            darkTheme.setChecked(true);
            lightTheme.setChecked(false);
        }
        darkTheme.setOnCheckedChangeListener((compoundButton, b) -> {
            UpdateTheme.setTheme("dark", AppCompatDelegate.MODE_NIGHT_YES,setting.this);
            darkTheme.setChecked(b);
            lightTheme.setChecked(false);
        });
        lightTheme.setOnCheckedChangeListener((compoundButton, b) -> {
            UpdateTheme.setTheme("light", AppCompatDelegate.MODE_NIGHT_NO,setting.this);
            darkTheme.setChecked(false);
            lightTheme.setChecked(b);
        });
    }
}
