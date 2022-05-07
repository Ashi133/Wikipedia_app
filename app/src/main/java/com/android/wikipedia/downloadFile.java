package com.android.wikipedia;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.xpath.XPath;

public class downloadFile implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final long BYTE_SIZE = 524288000;
    @SuppressLint ("StaticFieldLeak")
    public static Context mContext;
    public static String mName,mUrl,mType;
    public static String path="";
    @SuppressLint ("StaticFieldLeak")
    private static NotificationManagerCompat compat;

    public static String download(String name,Context context,String url,String type){
        mContext=context;
        mName=name;
        mUrl=url;
        mType=type;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },25);
        }else {
            path=downloadDocument();
        }
        return path;
    }
    private static String downloadDocument() {
        File f= new File(Environment.getExternalStorageDirectory() + File.separator + "Wikipedia");
        if (!f.exists() && !f.isDirectory()){
            if (f.mkdir()){
                Toast.makeText(mContext, "Directory created!", Toast.LENGTH_SHORT).show();
            }
        }
        File f1=new File(Environment.getExternalStorageDirectory()+File.separator+"Wikipedia"+File.separator+mType);
        if (!f1.exists() && !f1.isDirectory()){
            if (f1.mkdir()){
                Toast.makeText(mContext, "Sub Directory created!", Toast.LENGTH_SHORT).show();
            }
        }
        String folder="";
        if (mType.equalsIgnoreCase("Texts")){
            folder = Environment.getExternalStorageDirectory() + File.separator + "Wikipedia"+File.separator+mType + File.separator + mName+".txt";
        }else{
            folder = Environment.getExternalStorageDirectory() + File.separator + "Wikipedia"+File.separator+mType + File.separator + mName;
        }
        File file=new File(folder);
        if (!file.isFile() && !file.exists()){
            popUpNotification(mName);
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                //downloading logic goes here
                if (mType.equalsIgnoreCase("Texts")){
                    fos.write(mUrl.getBytes());
                    fos.flush();
                    fos.close();
                }else{
                        Glide.with(mContext).asBitmap().load(mUrl).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                resource.compress(Bitmap.CompressFormat.JPEG,100,fos);
                                try {
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                }
                compat.cancel(123);
                Toast.makeText(mContext, "Downloaded successfully at " + file, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                compat.cancel(123);
                Log.e("Download error =", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }else {
            Toast.makeText(mContext, "File already exists!", Toast.LENGTH_SHORT).show();
        }
        return file.toString();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 25){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                downloadDocument();
            }else {
                Toast.makeText(mContext, "Permission Required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private static void popUpNotification(String fName) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext, String.valueOf(123));
        builder.setSmallIcon(R.drawable.wikipedia);
        builder.setContentTitle("Downloading");
        builder.setContentText("Downloading file "+fName);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        Notification notification=builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("123","my notification", NotificationManager.IMPORTANCE_DEFAULT);
            //channel.setDescription("My notification");
            NotificationManager manager=(NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        compat= NotificationManagerCompat.from(mContext);
        compat.notify(123,notification);
    }
}
