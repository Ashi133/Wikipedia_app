package com.android.documentsharing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings ("ALL")
public class UpdateOnlineStatus {
    public static void OnPause(Context context) {
        SharedPreferences sharedPreferences=context.getSharedPreferences("FREEZE",Context.MODE_PRIVATE);
        boolean val=sharedPreferences.getBoolean("freeze",false);
        try {
            if (!val){
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("online status")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("isOnline")
                        .setValue(getCurrentDateTime());
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("online status")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("lastTime")
                        .setValue(getCurrentDateTime());
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("online status")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("date")
                        .setValue(getCurrentDate());
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("online status")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("flag")
                        .setValue(false);
            }else {
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("online status")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("lastTime")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("online status")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("isOnline")
                                            .setValue(snapshot.getValue(String.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OnResume() {
        try {
            FirebaseDatabase.getInstance().getReference().child("online status").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("isOnline").setValue("Online");
            FirebaseDatabase.getInstance().getReference().child("online status").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("flag").setValue(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDateTime() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.d(TAG, "getCurrentDateTime: greater than O");
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else {
            //Log.d(TAG, "getCurrentDateTime: less than O");
            @SuppressLint ("SimpleDateFormat") SimpleDateFormat SDFormat = new SimpleDateFormat("hh:mm a");
            return SDFormat.format(new Date());
        }
    }

    public static String getCurrentDate() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.d(TAG, "getCurrentDateTime: greater than O");
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd:MM:yyyy"));
        } else {
            //Log.d(TAG, "getCurrentDateTime: less than O");
            @SuppressLint ("SimpleDateFormat") SimpleDateFormat SDFormat = new SimpleDateFormat("dd:MM:yyyy");
            return SDFormat.format(new Date());
        }
    }

    public static String seenDay(String seenday) {
        String[] months ={"January","February","March","April","May","June","July","August","September","October","November","December"};
        String currentDate = getCurrentDate();
        String day=null;
        if (!currentDate.equals(seenday)) {
            if (( ( Integer.parseInt(currentDate.split(":")[ 1 ]) - ( Integer.parseInt(seenday.split(":")[ 1 ]) ) ) == 1 )) {
                if (( ( Integer.parseInt(seenday.split(":")[ 0 ]) - ( Integer.parseInt(currentDate.split(":")[ 0 ]) ) ) == 27 )
                        || ( ( Integer.parseInt(seenday.split(":")[ 0 ]) - ( Integer.parseInt(currentDate.split(":")[ 0 ]) ) ) == 28 )
                        || ( ( Integer.parseInt(seenday.split(":")[ 0 ]) - ( Integer.parseInt(currentDate.split(":")[ 0 ]) ) ) == 29 )
                        || ( ( Integer.parseInt(seenday.split(":")[ 0 ]) - ( Integer.parseInt(currentDate.split(":")[ 0 ]) ) ) == 30 )) {
                    day="Yesterday";
                }
            }else if (currentDate.split(":")[1].equals(seenday.split(":")[1])){
                if (Integer.parseInt(currentDate.split(":")[0])-Integer.parseInt(seenday.split(":")[0])==1){
                    day="Yesterday";
                }else {
                    day=String.format("%s %s,%s",seenday.split(":")[0],months[Integer.parseInt(seenday.split(":")[1])-1],seenday.split(":")[2]);
                }
            }else {
                day=String.format("%s %s,%s",seenday.split(":")[0],months[Integer.parseInt(seenday.split(":")[1])-1],seenday.split(":")[2]);
            }
        } else{
            day="Today";
        }
        return day;
    }
    public static boolean check_network_state(Context context){
        ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint ("MissingPermission") NetworkInfo info=manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}