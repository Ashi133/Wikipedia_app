package com.android.documentsharing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
    public static String mName,mExt,mUrl;
    public static void download(String name, String extension, Context context,String url){
        mContext=context;
        mName=name;
        mExt=extension;
        mUrl=url;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },25);
        }else {
            downloadDocument();
        }
    }
    private static void downloadDocument() {
        StorageReference st= FirebaseStorage.getInstance().getReferenceFromUrl(mUrl);
        String folder = Environment.getExternalStorageDirectory() + File.separator + "Document Sharing" + File.separator + mName;
        File file=new File(folder);
        if (!file.isFile() && !file.exists()){
            st.getBytes(BYTE_SIZE).addOnSuccessListener(bytes -> {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                    Toast.makeText(mContext, "Downloaded successfully at " + file, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadDocument();
            }else {
                Toast.makeText(mContext, "Permission Required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
