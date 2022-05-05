package com.android.wikipedia;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateTheme {
    public static String FOLDER="Theme";
    public static int getTheme(String key, Context context){
        return context.getSharedPreferences(FOLDER,Context.MODE_PRIVATE).getInt(key,0);
    }
    public static void setTheme(String key,int value,Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(FOLDER,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
}
