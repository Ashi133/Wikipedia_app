package com.android.documentsharing;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class downloadFile implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final long BYTE_SIZE = 524288000;
    @SuppressLint ("StaticFieldLeak")
    public static Context mContext;
    public static String mName,mUrl;
    @SuppressLint ("StaticFieldLeak")
    private static NotificationManagerCompat compat;

    public static void download(String name,Context context,String url){
        mContext=context;
        mName=name;
        mUrl=url;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },25);
        }else {
            downloadDocument();
        }
    }
    private static void downloadDocument() {
        StorageReference st= FirebaseStorage.getInstance().getReferenceFromUrl(mUrl);
        File f= new File(Environment.getExternalStorageDirectory() + File.separator + "Document Sharing");
        if (!(f.exists() && f.isDirectory())){
            f.mkdirs();
            Toast.makeText(mContext, "Directory created!", Toast.LENGTH_SHORT).show();
        }
        String folder = Environment.getExternalStorageDirectory() + File.separator + "Document Sharing" + File.separator + mName;
        File file=new File(folder);
        if (!file.isFile() && !file.exists()){
            popUpNotification(mName);
            st.getBytes(BYTE_SIZE).addOnSuccessListener(bytes -> {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                    compat.cancel(123);
                    Toast.makeText(mContext, "Downloaded successfully at " + file, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    compat.cancel(123);
                    Log.e("Download error =",e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        }else {
            Toast.makeText(mContext, "File already exists!", Toast.LENGTH_SHORT).show();
        }
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
        builder.setSmallIcon(R.drawable.sharing);
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
