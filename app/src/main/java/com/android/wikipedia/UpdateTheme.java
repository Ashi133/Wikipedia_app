package com.android.wikipedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class UpdateTheme {
    public static String FOLDER="Theme";
    public static int getTheme(String key, Context context){
        return context.getSharedPreferences(FOLDER,Context.MODE_PRIVATE).getInt(key,1);
    }
    public static void setTheme(String key,int value,Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(FOLDER,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
    public  static boolean check_network_state(Context context){
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint ("MissingPermission") NetworkInfo info=manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
